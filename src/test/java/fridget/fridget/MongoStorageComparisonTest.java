package fridget.fridget;//package com.fridge.fridgeproject;
//
//import com.fridge.fridgeproject.ingredient.UserIngredient;
//import com.fridge.fridgeproject.ingredient.UserIngredientRepository;
//import com.fridge.fridgeproject.recipe.Ingredient;
//import com.fridge.fridgeproject.recipe.Nutrition;
//import com.fridge.fridgeproject.recipe.Recipe;
//import com.fridge.fridgeproject.recipe.RecipeRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.bson.Document;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//public class MongoStorageComparisonTest {
//
//    @Autowired
//    private RecipeRepository recipeRepository;
//
//    @Autowired
//    private UserIngredientRepository userIngredientRepository;
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    private static final int COUNT = 100000;
//
//    @Test
//    public void testCompareStorageUsage() {
//        // 레시피 생성
//        List<Recipe> recipes = new ArrayList<>();
//        for (int i = 0; i < COUNT; i++) {
//            Recipe r = new Recipe();
//            r.setName("Recipe " + i);
//            r.setDescription("Delicious and easy recipe number " + i);
//            r.setReference("example.com" + i);
//            r.setSteps(List.of(
//                    "Step 1: Prepare ingredients.",
//                    "Step 2: Cook ingredients.",
//                    "Step 3: Serve and enjoy."
//            ));
//
//            r.setNutrition(new Nutrition("280", "17g", "46g", "19g", "4g", "10g", "772mg"));
//
//            r.setIngredients(List.of(
//                    new Ingredient("ingredient1", "100g"),
//                    new Ingredient("ingredient2", "50g"),
//                    new Ingredient("ingredient3", "30g")
//            ));
//
//            r.setMissingIngredients(new ArrayList<>());
//            recipes.add(r);
//        }
//        recipeRepository.saveAll(recipes);
//
//        // 재료 생성
//        List<UserIngredient> userIngredients = new ArrayList<>();
//        for (int i = 0; i < COUNT; i++) {
//            userIngredients.add(new UserIngredient("id" + i, "apple", "fruit"));
//        }
//        userIngredientRepository.saveAll(userIngredients);
//
//    }
//}