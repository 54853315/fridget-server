package fridget.fridget.user.dto;

import fridget.fridget.user.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateReqDto {
    private String name;
    private String userId;
    private String userPassword;
    private Role role;
    private String vegan;
    private int meatConsumption;
    private int fishConsumption;
    private int vegeConsumption;
    private List<String> cookingMethod;
    private int spiciness;
    private List<String> allergies;
}
