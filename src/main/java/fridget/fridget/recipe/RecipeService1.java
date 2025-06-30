package fridget.fridget.recipe;//package com.fridge.fridgeproject.recipe;
//
//import com.fridge.fridgeproject.ingredient.UserIngredient;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fridge.fridgeproject.ingredient.IngredientService;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//
//@Service
//public class RecipeService1 {
//
//    // processbuilder 쓰는 방식
//    private final IngredientService ingredientService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public RecipeService1(IngredientService ingredientService) {
//        this.ingredientService = ingredientService;
//    }
//
//    public List<Recipe> generateRecipes() {
//        long totalStart = System.currentTimeMillis();
//        try {
//            List<UserIngredient> userIngredientsList = ingredientService.findMyIngredients();
//            List<String> userIngredients = new ArrayList<>();
//            for (UserIngredient ingredient : userIngredientsList) {
//                userIngredients.add(ingredient.getName());
//            }
//            System.out.println(userIngredients);
//            String jsonInput = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(userIngredients);
//            String projectRoot = System.getProperty("user.dir");
//            Path pythonPath = Paths.get(projectRoot, "venv", "bin", "python");
//            Path generateScript = Paths.get(projectRoot, "src", "main", "resources", "scripts", "recipes_generate.py");
//            Path filterScript = Paths.get(projectRoot, "src", "main", "resources", "scripts", "recipes_filter.py");
//            Path filteredJson = Paths.get(projectRoot, "src", "main", "resources", "data", "recipes_filtered.json");
//            Path generatedJson = Paths.get(projectRoot, "src", "main", "resources", "data", "recipes_generated.json");
//
//            // 레시피 생성 시작
//            long genStart = System.currentTimeMillis();
//            ProcessBuilder pb = new ProcessBuilder(pythonPath.toString(), generateScript.toString());
//            pb.redirectErrorStream(true);
//            Process process = pb.start();
//            OutputStream os = process.getOutputStream();
//            os.write(jsonInput.getBytes());
//            os.flush();
//            os.close();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder output = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            int exitCode = process.waitFor();
//            long genEnd = System.currentTimeMillis();
//            if (exitCode != 0) {
//                System.out.println("❌ 레시피 생성 실패 exitCode = " + exitCode);
//                System.out.println("🔍 출력 내용: \n" + output.toString());
//                return Collections.emptyList();
//            }
//            // 레시피 생성 끝
//
//            // 레시피 필터링 시작
//            long filterStart = System.currentTimeMillis();
//            ProcessBuilder pb2 = new ProcessBuilder(pythonPath.toString(), filterScript.toString());
//            pb2.redirectErrorStream(true);
//            Process process2 = pb2.start();
//
//            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
//            StringBuilder output2 = new StringBuilder();
//            while ((line = reader2.readLine()) != null) {
//                output2.append(line).append("\n");
//            }
//
//            long filterEnd = System.currentTimeMillis();
//            int exitCode2 = process2.waitFor();
//            if (exitCode2 != 0) {
//                System.out.println("❌ 레시피 필터링 실패 Exit code: " + exitCode2);
//                System.out.println("🔍 출력 내용: \n" + output2.toString());
//                return Collections.emptyList();
//            }
//            // 필터링 끝
//
//            File resultJsonFile = filteredJson.toFile();
//            if (!resultJsonFile.exists()) {
//                List<Recipe> emptyRecipeList = new ArrayList<>();
//                objectMapper.writeValue(resultJsonFile, emptyRecipeList);
//            }
//
//            String jsonContent = new String(Files.readAllBytes(filteredJson));
//            List<Recipe> sortedRecipes = objectMapper.readValue(jsonContent, new TypeReference<List<Recipe>>() {
//            });
//
//            for (Recipe sr : sortedRecipes) {
//                List<String> missingIngredients = new ArrayList<>();
//                for (Ingredient ingredient : sr.getIngredients()) {
//                    if (!userIngredients.contains(ingredient.getName().toLowerCase())) {
//                        missingIngredients.add(ingredient.getName());
//                    }
//                }
//                sr.setMissingIngredients(missingIngredients);
//            }
//            long totalEnd = System.currentTimeMillis();
//            System.out.println("✅ 레시피 생성 실행 시간: " + (genEnd - genStart) + " ms");
//            System.out.println("✅ 레시피 필터링 실행 시간: " + (filterEnd - filterStart) + " ms");
//            System.out.println("⏱️ 총 소요 시간: " + (totalEnd - totalStart) + " ms (" + ((totalEnd - totalStart) / 1000.0) + "초)");
//
//            return sortedRecipes;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
//
//    public List<Recipe> generateRecipesForTest() {
//        long totalStart = System.currentTimeMillis();
//        try {
//            // ✅ 테스트용 사용자 재료 (예시)
//            List<String> userIngredients = Arrays.asList("egg", "milk", "flour", "sugar");
//
//            // ✅ 재료를 JSON 문자열로 변환
//            String jsonInput = new ObjectMapper().writeValueAsString(userIngredients);
//
//            // ✅ Python 스크립트 경로 설정
//            String projectRoot = System.getProperty("user.dir");
//            Path pythonPath = Paths.get(projectRoot, "venv", "bin", "python");
//            Path generateScript = Paths.get(projectRoot, "src", "main", "resources", "scripts", "recipes_generate.py");
//            Path filterScript = Paths.get(projectRoot, "src", "main", "resources", "scripts", "recipes_filter.py");
//            Path filteredJson = Paths.get(projectRoot, "src", "main", "resources", "data", "recipes_filtered.json");
//
//            // ✅ 레시피 생성
//            long genStart = System.currentTimeMillis();
//            ProcessBuilder pb = new ProcessBuilder(pythonPath.toString(), generateScript.toString());
//            pb.redirectErrorStream(true);
//            Process process = pb.start();
//            try (OutputStream os = process.getOutputStream()) {
//                os.write(jsonInput.getBytes());
//                os.flush();
//            }
//
//            StringBuilder output = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    output.append(line).append("\n");
//                }
//            }
//
//            int exitCode = process.waitFor();
//            long genEnd = System.currentTimeMillis();
//            if (exitCode != 0) {
//                System.out.println("❌ 레시피 생성 실패: " + output);
//                return Collections.emptyList();
//            }
//
//            // ✅ 필터링
//            long filterStart = System.currentTimeMillis();
//            ProcessBuilder pb2 = new ProcessBuilder(pythonPath.toString(), filterScript.toString());
//            pb2.redirectErrorStream(true);
//            Process process2 = pb2.start();
//
//            StringBuilder output2 = new StringBuilder();
//            try (BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()))) {
//                String line;
//                while ((line = reader2.readLine()) != null) {
//                    output2.append(line).append("\n");
//                }
//            }
//
//            int exitCode2 = process2.waitFor();
//            long filterEnd = System.currentTimeMillis();
//            if (exitCode2 != 0) {
//                System.out.println("❌ 레시피 필터링 실패: " + output2);
//                return Collections.emptyList();
//            }
//
//            // ✅ 결과 파일 파싱
//            String jsonContent = new String(Files.readAllBytes(filteredJson));
//            List<Recipe> sortedRecipes = objectMapper.readValue(jsonContent, new TypeReference<List<Recipe>>() {});
//
//            for (Recipe recipe : sortedRecipes) {
//                List<String> missing = new ArrayList<>();
//                for (Ingredient ing : recipe.getIngredients()) {
//                    if (!userIngredients.contains(ing.getName().toLowerCase())) {
//                        missing.add(ing.getName());
//                    }
//                }
//                recipe.setMissingIngredients(missing);
//            }
//
//            long totalEnd = System.currentTimeMillis();
//            System.out.println("✅ 레시피 생성 실행 시간: " + (genEnd - genStart) + " ms");
//            System.out.println("✅ 레시피 필터링 실행 시간: " + (filterEnd - filterStart) + " ms");
//            System.out.println("⏱️ 총 소요 시간: " + (totalEnd - totalStart) + " ms (" + ((totalEnd - totalStart) / 1000.0) + "초)");
//
//            return sortedRecipes;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
//
//}
