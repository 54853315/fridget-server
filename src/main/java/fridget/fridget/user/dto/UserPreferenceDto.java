package fridget.fridget.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPreferenceDto {
    private String vegan;
    private int meatConsumption;
    private int fishConsumption;
    private int vegeConsumption;
    private int spiciness;
    private List<String> allergies;
}