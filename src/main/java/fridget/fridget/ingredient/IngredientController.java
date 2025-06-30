package fridget.fridget.ingredient;
import fridget.fridget.common.CommonResponse;
import fridget.fridget.ingredient.dto.IngredientsReqDto;
import fridget.fridget.ingredient.dto.CookingPreferenceReqDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/user/ingredients")
    public List<UserIngredient> findMyIngredients() {
        return ingredientService.findMyIngredients();

    }

    @PostMapping("/user/ingredients/create")
    public ResponseEntity<CommonResponse> createIngredients(@RequestBody List<IngredientsReqDto> ingredientsReqDtos) {
        List<UserIngredient> userIngredients = ingredientService.createIngredients(ingredientsReqDtos);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Ingredients added!", userIngredients), HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/ingredients/delete")
    public ResponseEntity<CommonResponse> deleteIngredients(@RequestBody List<IngredientsReqDto> ingredientsReqDtos) {
        List<UserIngredient> userIngredient = ingredientService.deleteIngredients(ingredientsReqDtos);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Ingredients removed!", userIngredient), HttpStatus.OK);
    }

    // 获取用户烹饪偏好
    @GetMapping("/user/cooking-preference")
    public ResponseEntity<CommonResponse> getMyCookingPreference() {
        UserCookingPreference preference = ingredientService.getMyCookingPreference();
        if (preference == null) {
            return new ResponseEntity<>(new CommonResponse(HttpStatus.NOT_FOUND, "No cooking preference found!", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Cooking preference retrieved!", preference), HttpStatus.OK);
    }

    // 创建或更新用户烹饪偏好
    @PostMapping("/user/cooking-preference")
    public ResponseEntity<CommonResponse> saveCookingPreference(@RequestBody CookingPreferenceReqDto cookingPreferenceReqDto) {
        try {
            UserCookingPreference preference = ingredientService.saveCookingPreference(cookingPreferenceReqDto);
            return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Cooking preference saved!", preference), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    // 删除用户烹饪偏好
    @DeleteMapping("/user/cooking-preference")
    public ResponseEntity<CommonResponse> deleteCookingPreference() {
        try {
            ingredientService.deleteCookingPreference();
            return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Cooking preference deleted!", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
