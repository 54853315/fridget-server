package fridget.fridget.recipe;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;

@RestController
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/recommend")
    public List<Recipe> generateRecipes() {
        List<Recipe> recipes = recipeService.generateRecipes();
        return recipes;
    }

//    @GetMapping("/recipe/recommend/test")
//    @PermitAll // 또는 security config에서 인증 제외 설정
//    public List<Recipe> generateTestRecipes() {
//        return recipeService.generateRecipesForTest(); // 인증 없이 작동 가능한 메서드
//    }
}
