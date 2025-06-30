package fridget.fridget.user;


import fridget.fridget.ingredient.UserIngredient;
import fridget.fridget.user.dto.UserCreateReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "User")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String userId;
    private String userPassword;

    private Role role;

    private String vegan;
    private int meatConsumption;
    private int fishConsumption;
    private int vegeConsumption;
    private int spiciness;
    private List<String> allergies;

    @DBRef
    @Builder.Default
    private List<UserIngredient> userIngredients = new ArrayList<>();

    public static User toEntity(UserCreateReqDto userCreateReqDto) {
        UserBuilder userBuilder = User.builder();
        userBuilder.name(userCreateReqDto.getName())
                .userId(userCreateReqDto.getUserId())
                .userPassword(userCreateReqDto.getUserPassword())
                .role(userCreateReqDto.getRole())
                .vegan(userCreateReqDto.getVegan())
                .meatConsumption(userCreateReqDto.getMeatConsumption())
                .fishConsumption(userCreateReqDto.getFishConsumption())
                .vegeConsumption(userCreateReqDto.getVegeConsumption())
                .spiciness(userCreateReqDto.getSpiciness())
                .allergies(userCreateReqDto.getAllergies());
        if (userCreateReqDto.getRole() == null) {
            userBuilder.role(Role.USER);
        } else {
            userBuilder.role(userCreateReqDto.getRole());
        }
        return userBuilder.build();
    }
}
