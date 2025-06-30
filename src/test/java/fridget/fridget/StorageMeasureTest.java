package fridget.fridget;//package com.fridge.fridgeproject;
//
//import com.fridge.fridgeproject.ingredient.UserIngredientRepository;
//import com.fridge.fridgeproject.recipe.RecipeRepository;
//import org.bson.Document;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//@SpringBootTest
//public class StorageMeasureTest {
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
//    @Test
//    public void testMeasureExistingStorageUsage() {
//        // 총 저장된 개수
//        long recipeCount = recipeRepository.count();
//        long userIngredientCount = userIngredientRepository.count();
//        System.out.println("🍲 저장된 Recipe 개수: " + recipeCount);
//        System.out.println("🥬 저장된 UserIngredient 개수: " + userIngredientCount);
//
//        // storageSize 측정
//        long recipeSize = getCollectionStorageSize("Recipe");
//        long ingredientSize = getCollectionStorageSize("UserIngredient");
//
//        System.out.println("🍲 [Recipe 저장 용량] Mongo storageSize: " + formatSize(recipeSize));
//        System.out.println("🥬 [User 재료 저장 용량] Mongo storageSize: " + formatSize(ingredientSize));
//
//        double savingRatio = 100.0 * (1.0 - ((double) ingredientSize / recipeSize));
//        System.out.println("📉 저장 용량 절감율: " + String.format("%.2f", savingRatio) + "%");
//    }
//
//    private long getCollectionStorageSize(String collectionName) {
//        Document stats = mongoTemplate.executeCommand(new Document("collStats", collectionName));
//        return stats.get("storageSize", Number.class).longValue();
//    }
//
//    private String formatSize(long bytes) {
//        double kb = bytes / 1024.0;
//        double mb = kb / 1024.0;
//        return String.format("%.2f MB (%d bytes)", mb, bytes);
//    }
//}
