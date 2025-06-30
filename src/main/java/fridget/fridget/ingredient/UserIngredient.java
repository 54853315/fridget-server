package fridget.fridget.ingredient;

import fridget.fridget.ingredient.dto.IngredientsReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "UserIngredient")
public class UserIngredient {
    @Id
    private String id;
    private String name;
    private String category;

    public static UserIngredient toEntity(IngredientsReqDto ingredientsReqDto) {
        UserIngredientBuilder ingredientBuilder = UserIngredient.builder();
        ingredientBuilder.name(ingredientsReqDto.getName())
                .category(ingredientsReqDto.getCategory());
        return ingredientBuilder.build();
    }
}
