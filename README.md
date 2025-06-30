# 🍏 AI 驱动的食谱推荐网站 Fridget Server

<img width="797" alt="开发阶段界面预览" src="https://github.com/54853315/fridget-frontend/blob/main/frontend/static/images/dev-stage-preview-image.jpeg?raw=true" />
👉 [FridgetFrontend 前端仓库](https://github.com/54853315/fridget-frontend)

如果只用冰箱里的食材就能找到美味的菜肴呢？
<br> Fridget 是一个基于 AI 的网站，利用大语言模型，根据用户拥有的食材推荐最佳食谱。
<br>别再为“今天吃什么？”而烦恼，让 AI 为您找到合适的食谱吧！

🚀 **主要功能**
<p> 1️⃣ **智能食谱搜索**：AI 搜索网络，找到适合您现有食材的食谱。
<p> 2️⃣ **个性化推荐**：利用最近邻算法分析并反映用户的饮食习惯和偏好。
<p> 3️⃣ **基于偏好的推荐排名**：考虑用户偏好，优先推荐合适的食谱，提供最佳选择。


> 本项目的初始版本来源于 [Fridget Server](https://github.com/sinaetown/FridgetServer.git) 项目，他们在 [YouTube](https://youtu.be/FFFVZ70Mt_E) 上展示的 Demo 极具启发性。在此向原团队（Sinae Hong, Hanseung Choi, Samuel Han, Hojun Kwak）表示诚挚的敬意和感谢！

在原项目的基础上，我进行了二次开发和一些技术调整，以便更好地适应本地化需求和进行技术探索。
## 🔧 主要技术改进

- **AI 模型迁移**: 将食谱生成的核心 AI 从 OpenAI 替换为**[Qwen3](https://www.aliyun.com/product/tongyi)**，以优化在中国大陆地区的访问速度和响应效果。
- **API 接口优化**:
  - 精简了 `/ingredients` 相关的数据传输对象 (DTO)，提升了交互效率。
  - 新增了 `/cooking-preference` 接口，以支持更丰富的用户偏好设置。
  - 新增了 PEXELS API 集成，为食谱推荐提供精美的配图展示，增强用户体验。
  - **缓存优化**: 首页推荐接口新增 24 小时 AI 结果缓存机制，提升了系统响应速度和资源利用率。
- **前端重构**: 前端使用 SvelteKit 进行了完全重构，旨在探索新的前端技术栈并提升开发体验。

## 🛠 技术栈

### 前端
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white) ![Chakra UI](https://img.shields.io/badge/Chakra%20UI-319795?style=for-the-badge&logo=chakraui&logoColor=white) ![Material UI](https://img.shields.io/badge/Material%20UI-0081CB?style=for-the-badge&logo=mui&logoColor=white)

### 后端
![Java 11](https://img.shields.io/badge/java%2011-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Java Spring](https://img.shields.io/badge/Java%20Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white) ![Flask](https://img.shields.io/badge/flask-%23000.svg?style=for-the-badge&logo=flask&logoColor=white) ![JSON Web Tokens](https://img.shields.io/badge/JSON%20Web%20Tokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

### AI/ML
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white) ![Qwen3](https://img.shields.io/badge/%E9%80%9A%E4%B9%89%E5%8D%83%E9%97%AE-412991?style=for-the-badge&logo=alibabacloud&logoColor=white) ![Scikit-learn](https://img.shields.io/badge/Scikit--learn-F7931E?style=for-the-badge&logo=scikit-learn&logoColor=white)

---

## 🏛️ 系统架构

![Fridget Architecture](https://github.com/user-attachments/assets/d9fd87f8-98a6-42b9-bb42-a1aaf5999612)

---

## 🚀 服务启动指南

### 1. 启动 Redis
```bash
brew services start redis
```

### 2. 启动 MongoDB

```bash
brew tap mongodb/brew
brew services start mongodb-community
```

### 3. 启动 Flask 服务

(1) 创建并激活虚拟环境
```bash
cd FridgetServer/flask
python3 -m venv venv
source venv/bin/activate
```

(2) 安装必要的依赖
  
```bash
# 使用 Python 3
pip install flask requests spacy scikit-learn
# 如果使用 Python 3.11+，需要手动安装 spaCy 模型
python -m spacy download zh_core_web_md
```

</details>

(3) 根据 [generate_recipes_flask.py](./flask/generate_recipes_flask.py) 中的注释设置 `ALI_API_KEY`和`PEXELS_API_KEY`

```
export ALI_API_KEY=''
export PEXELS_API_KEY=''
```

**重要**: 请将 `ALI_API_KEY` 和 `PEXELS_API_KEY` 作为环境变量进行配置，以避免密钥泄露。

(4) 启动 Flask 服务

```bash
python -m flask --app generate_recipes_flask run --host=0.0.0.0 --port=5001 #--debug
```

### 3. 启动 Spring Boot 服务
```bash
cd FridgetServer/
./gradlew compileJava #--stacktrace
./gradlew build
java -jar build/libs/fridgeproject-0.0.1-SNAPSHOT.jar
```

### 3.1 本地二次开发

```bash
cd FridgetServer/
./gradlew compileJava --stacktrace
./gradlew build --continuous
./gradlew bootRun
```