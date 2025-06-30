package fridget.fridget.recipe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fridget.fridget.ingredient.IngredientService;
import fridget.fridget.ingredient.UserIngredient;
import fridget.fridget.user.UserService;
import fridget.fridget.user.dto.UserPreferenceDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

// 内部类用于解析Flask返回的JSON结构
class RecipeWrapper {
    private Recipe recipe;
    private double score;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

@Service
public class RecipeService {

    private final IngredientService ingredientService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    // 缓存键前缀
    private static final String RECIPE_CACHE_PREFIX = "recipe:";
    // 缓存过期时间（24小时）
    private static final long CACHE_EXPIRATION_HOURS = 24;

    public RecipeService(IngredientService ingredientService, UserService userService,
            RedisTemplate<String, String> redisTemplate) {
        this.ingredientService = ingredientService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据用户食材列表生成缓存键
     * 
     * @param userIngredients 用户食材列表
     * @return 缓存键
     */
    private String generateCacheKey(List<String> userIngredients) {
        try {
            // 对食材列表进行排序以确保一致性
            List<String> sortedIngredients = new ArrayList<>(userIngredients);
            Collections.sort(sortedIngredients);

            // 将食材列表转换为字符串
            String ingredientsString = String.join(",", sortedIngredients);

            // 使用MD5生成哈希值作为缓存键的一部分
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(ingredientsString.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return RECIPE_CACHE_PREFIX + sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果MD5不可用，使用简单的哈希码
            return RECIPE_CACHE_PREFIX + Math.abs(userIngredients.hashCode());
        }
    }

    /**
     * 从缓存获取食谱
     * 
     * @param cacheKey 缓存键
     * @return 缓存的食谱列表，如果不存在则返回null
     */
    private List<Recipe> getRecipesFromCache(String cacheKey) {
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                System.out.println("🎯 从缓存获取食谱: " + cacheKey);
                List<RecipeWrapper> recipeWrappers = objectMapper.readValue(cachedJson,
                        new TypeReference<List<RecipeWrapper>>() {
                        });
                List<Recipe> recipes = new ArrayList<>();
                for (RecipeWrapper wrapper : recipeWrappers) {
                    recipes.add(wrapper.getRecipe());
                }
                return recipes;
            }
        } catch (Exception e) {
            System.out.println("⚠️ 缓存读取失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 将食谱存储到缓存
     * 
     * @param cacheKey    缓存键
     * @param jsonContent Flask返回的原始JSON内容
     */
    private void saveRecipesToCache(String cacheKey, String jsonContent) {
        try {
            redisTemplate.opsForValue().set(cacheKey, jsonContent, CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);
            System.out.println("💾 食谱已缓存: " + cacheKey + " (过期时间: " + CACHE_EXPIRATION_HOURS + "小时)");
        } catch (Exception e) {
            System.out.println("⚠️ 缓存存储失败: " + e.getMessage());
        }
    }

    public List<Recipe> generateRecipes() {
        long totalStart = System.currentTimeMillis();
        try {
            // 1. 사용자 재료 가져오기
            List<UserIngredient> userIngredientsList = ingredientService.findMyIngredients();
            List<String> userIngredients = new ArrayList<>();
            for (UserIngredient ingredient : userIngredientsList) {
                userIngredients.add(ingredient.getName());
            }
            System.out.println("🥬 사용자 재료: " + userIngredients);

            // 2. 缓存键生成
            String cacheKey = generateCacheKey(userIngredients);
            System.out.println("🔑 缓存键: " + cacheKey);

            // 3. 缓存检查
            List<Recipe> cachedRecipes = getRecipesFromCache(cacheKey);
            if (cachedRecipes != null) {
                // 计算缺失食材信息
                for (Recipe recipe : cachedRecipes) {
                    List<String> missingIngredients = new ArrayList<>();
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (!userIngredients.contains(ingredient.getName().toLowerCase())) {
                            missingIngredients.add(ingredient.getName());
                        }
                    }
                    recipe.setMissingIngredients(missingIngredients);
                }

                long totalEnd = System.currentTimeMillis();
                System.out.println("总耗时（缓存命中）: " + (totalEnd - totalStart) + " ms");
                return cachedRecipes;
            }

            System.out.println("🚫 缓存未命中，调用AI生成食谱...");

            UserPreferenceDto userPreferenceDto = userService.findMyPreferences();
            // 4. Flask
            String flaskUrl = "http://localhost:5001/generate";

            Map<String, Object> body = new HashMap<>();
            body.put("userIngredients", userIngredients);
            body.put("userPreferences", userPreferenceDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            long callStart = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, entity, String.class);
            long callEnd = System.currentTimeMillis();

            if (response.getStatusCode() != HttpStatus.OK) {
                System.out.println("❌ Flask 서버 호출 실패: " + response.getStatusCode() + " - 不缓存失败结果");
                return Collections.emptyList();
            }

            // 5. 결과 파싱
            String jsonContent = response.getBody();
            System.out.println("Flask 응답 JSON: " + jsonContent);

            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                System.out.println("❌ Flask 返回空内容 - 不缓存失败结果");
                return Collections.emptyList();
            }

            List<RecipeWrapper> recipeWrappers;
            List<Recipe> recipes;
            try {
                recipeWrappers = objectMapper.readValue(jsonContent, new TypeReference<List<RecipeWrapper>>() {
                });

                // RecipeWrapper에서 Recipe 객체만 추출
                recipes = new ArrayList<>();
                for (RecipeWrapper wrapper : recipeWrappers) {
                    recipes.add(wrapper.getRecipe());
                }

                // 6. 成功时缓存结果
                if (!recipes.isEmpty()) {
                    saveRecipesToCache(cacheKey, jsonContent);
                } else {
                    System.out.println("⚠️ 生成的食谱列表为空 - 不缓存空结果");
                }

            } catch (Exception parseException) {
                System.out.println("❌ JSON解析失败: " + parseException.getMessage() + " - 不缓存失败结果");
                return Collections.emptyList();
            }

            // 7. 재료 누락 정보 계산
            for (Recipe recipe : recipes) {
                List<String> missingIngredients = new ArrayList<>();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (!userIngredients.contains(ingredient.getName().toLowerCase())) {
                        missingIngredients.add(ingredient.getName());
                    }
                }
                recipe.setMissingIngredients(missingIngredients);
            }

            long totalEnd = System.currentTimeMillis();
            System.out.println("🚀 Flask 호출 시간: " + (callEnd - callStart) + " ms");
            System.out.println("⏱️ 총 소요 시간（AI生成）: " + (totalEnd - totalStart) + " ms ("
                    + ((totalEnd - totalStart) / 1000.0) + "초)");
            System.out.println("✅ 成功生成 " + recipes.size() + " 个食谱并已缓存");

            return recipes;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}