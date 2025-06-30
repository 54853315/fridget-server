from flask import Flask, request, jsonify
import os
import requests
import json
import re
import spacy
import numpy as np
from sklearn.neighbors import NearestNeighbors
from sklearn.metrics.pairwise import cosine_similarity
import time

app = Flask(__name__)
ALI_API_KEY = os.getenv('ALI_API_KEY', 'YOUR KEY')
PEXELS_API_KEY = os.getenv('PEXELS_API_KEY', 'YOUR KEY')
nlp = spacy.load("zh_core_web_md")

vegan_dict = {
    "strict": {"meat": -1, "fish": -1, "egg": -1, "dairy": -1, "vegetable": 1},
    "lacto": {"meat": -1, "fish": -1, "egg": -1, "dairy": 1, "vegetable": 1},
    "ovo": {"meat": -1, "fish": -1, "egg": 1, "dairy": -1, "vegetable": 1},
    "pescatarian": {"meat": -1, "fish": 1, "egg": 1, "dairy": 1, "vegetable": 1},
    "flexitarian": {"meat": 0.5, "fish": 0.5, "egg": 1, "dairy": 1, "vegetable": 1},
    "none": {"meat": 1, "fish": 1, "egg": 1, "dairy": 1, "vegetable": 1}
}

@app.errorhandler(Exception)
def handle_exception(e):
    print(f"未捕获异常: {str(e)}")
    return jsonify({"error": "服务器内部错误", "detail": str(e)}), 500

def get_spacy_similarity(word1, word2):
    vec1 = nlp(word1).vector
    vec2 = nlp(word2).vector
    return cosine_similarity([vec1], [vec2])[0][0]

def search_recipe_image(query):
    if PEXELS_API_KEY and PEXELS_API_KEY != 'YOUR_PEXELS_API_KEY':
        headers = {"Authorization": PEXELS_API_KEY}
        params = {"query": query, "per_page": 1, "orientation": "landscape"}
        try:
            response = requests.get("https://api.pexels.com/v1/search", headers=headers, params=params, timeout=10)
            response.raise_for_status()
            data = response.json()
            print(f"Pexles API 返回的图片数据",data["photos"])
            if data["photos"]:
                return data["photos"][0]["src"]["large"]
        except requests.exceptions.RequestException as e:
            print(f"Pexels API 请求失败: {e}")
    return "" # 失败时返回默认图

def create_recipe_feature_vector(recipe, vegan_score, user_data):
    ingredients = [item["name"] for item in recipe["ingredients"]]

    meat_score = sum(get_spacy_similarity("meat", ing) * vegan_score["meat"] for ing in ingredients if get_spacy_similarity("meat", ing) > 0.3)
    fish_score = sum(get_spacy_similarity("fish", ing) * vegan_score["fish"] for ing in ingredients if get_spacy_similarity("fish", ing) > 0.3)
    egg_score = sum(get_spacy_similarity("egg", ing) * vegan_score["egg"] for ing in ingredients if get_spacy_similarity("egg", ing) > 0.3)
    dairy_score = sum(get_spacy_similarity("dairy", ing) * vegan_score["dairy"] for ing in ingredients if get_spacy_similarity("dairy", ing) > 0.3)
    vege_score = sum(get_spacy_similarity("vegetable", ing) * vegan_score["vegetable"] for ing in ingredients if get_spacy_similarity("vegetable", ing) > 0.3)

    penalty = min(meat_score + fish_score + egg_score + dairy_score, -5)
    reward = vege_score + 1 if vege_score > 2 else vege_score

    for allergic_food in user_data.get("allergies", []):
        for ingredient in ingredients:
            similarity = get_spacy_similarity(allergic_food, ingredient)
            if similarity > 0.15:
                penalty += -max(5, 10 * similarity)

    reward += 0.5 if abs(recipe.get("spiceLevel", 0) - user_data.get("spiciness", 0)) <= 1 else 0

    return [meat_score, fish_score, egg_score, dairy_score, vege_score, penalty, reward]

@app.route("/generate", methods=["POST"])
def generate():
    try:
        data = request.get_json()
        ingredients = data.get("userIngredients", [])
        user_data = data.get("userPreferences", {})
        vegan_score = vegan_dict.get(user_data.get("vegan", "none"), vegan_dict["none"])

        gen_start = time.perf_counter()

        prompt = f"""
        你可以获取用户冰箱中现有的食材列表：
可用食材：{', '.join(ingredients)}

        请基于这些食材，**只推荐1道可以制作的菜谱**。
        
        **重要：只输出一个包含1个对象的JSON数组，不要输出任何其他文字、解释或格式说明。**

        ### Output Format (Valid JSON)
        [
            {{
                "name": "Recipe Name",
                "description": "Brief description",
                "imageUrl": "https://example.com/recipe-image.jpg",
                "imageSearchQuery": "Stir-fried tomatoes and eggs",
                "nutrition": {{"calories": "", "protein": "", "carbs": "", "fat": "", "fiber": "", "sugar": "", "sodium": ""}},
                "ingredients": [{{"name": "ingredient", "quantity": "50g"}}],
                "steps": ["Step 1", "Step 2"],
                "spiceLevel": 2
            }}
        ]
        """

        headers = {
            'Authorization': f'Bearer {ALI_API_KEY}',
            'Content-Type': 'application/json',
            # 'X-DashScope-SSE': 'enable'   //steam
        }
        payload = {
            'model': 'qwen-plus-1220',
            'input':{
            'messages': [{"role": "system", "content": "你是一个食谱推荐助手"},{'role': 'user', 'content': prompt}],
            },
            # 'parameters':{"result_format":"json","top_p":0.8,"temperature":0.7,"enable_search":False}
        }
        
        print('请求头:', headers)
        if not request.is_json:
            print("无效的请求格式")
            return jsonify({"error": "请求必须是JSON格式"}), 400
        
        # 分离响应获取与JSON解析
        try:
            raw_response = requests.post(
                'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation',
                headers=headers,
                json=payload,
                timeout=180
            )
            raw_response.raise_for_status()  # 检查HTTP状态码
            print(f"API响应状态码: {raw_response.status_code}")
            
            response_data = raw_response.json()
        except requests.exceptions.RequestException as e:
            print(f"网络请求异常: {str(e)}")
            return jsonify({"error": "API请求失败"}), 500
        except ValueError as e:
            print(f"JSON解析失败: {str(e)}")
            return jsonify({"error": "API响应格式异常"}), 500
        
        recipe_data = []
        try:
            result_text = response_data['output']['text']
            # 清理Markdown代码块标记
            result_text = re.sub(r"```json|```", "", result_text).strip()
            recipes = json.loads(result_text)
            # 为每个食谱获取图片URL
            for recipe in recipes:
                search_query = recipe.get("imageSearchQuery", recipe.get("name"))
                image_url = search_recipe_image(search_query)
                recipe["imageUrl"] = image_url
                # 从最终结果中移除 imageSearchQuery
                if "imageSearchQuery" in recipe:
                    del recipe["imageSearchQuery"]
            if isinstance(recipes, list):
                recipe_data.extend(recipes)
            else:
                recipe_data.append(recipes)
        except (KeyError, json.JSONDecodeError) as e:
            print(f"解析API响应失败: {str(e)}")
            print(f"原始响应数据: {response_data}")
            return jsonify({"error": "解析API响应时出错"}), 500

        if not recipe_data:
            return jsonify({"error": "未生成有效食谱"}), 500
        
        gen_end = time.perf_counter()
        print(f"食谱生成耗时: {(gen_end - gen_start):.2f}s")

        filter_start = time.perf_counter()
        try:
            recipe_features = np.array([create_recipe_feature_vector(r, vegan_score, user_data) for r in recipe_data])
        except Exception as e:
            print(f"特征计算错误: {str(e)}")
            return jsonify({"error": "食谱特征计算失败"}), 500
        user_vector = np.array([
            user_data.get("meatConsumption", 0),
            user_data.get("fishConsumption", 0),
            0, 0,
            user_data.get("vegeConsumption", 0),
            0, 0
        ]).reshape(1, -1)

        nn_model = NearestNeighbors(n_neighbors=len(recipe_features), metric='euclidean')
        try:
            nn_model.fit(recipe_features)
        except ValueError as e:
            print(f"模型训练错误: {str(e)}")
            return jsonify({"error": "食谱匹配失败"}), 500

        distances, indices = nn_model.kneighbors(user_vector)
        max_distance = max(distances.flatten())
        # 防止零除错误
        if max_distance == 0:
            scores = [10] * len(distances.flatten())
        else:
            scores = [(10 - (dist / max_distance) * 10) for dist in distances.flatten()]

        sorted_recipes = sorted(
            [{"recipe": recipe_data[i], "score": scores[idx]} for idx, i in enumerate(indices.flatten())],
            key=lambda x: x["score"], reverse=True
        )

        filter_end = time.perf_counter()
        print(f"总执行时间: {(filter_end - gen_start) * 1000:.0f}ms")

        # result = [item["recipe"] for item in sorted_recipes]
        result = [{"recipe": item["recipe"], "score": item["score"]} for item in sorted_recipes]
        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
