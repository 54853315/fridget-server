package fridget.fridget.recipe;

import java.io.*;
import java.util.List;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @Document(collection = "Recipe")
public class Recipe {
    private String name;
    private String description;
    private Nutrition nutrition;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private String imageUrl;
    private String reference;
    private List<String> missingIngredients;
    private int spiceLevel;
    private String cookingDifficultyLevel;
}
