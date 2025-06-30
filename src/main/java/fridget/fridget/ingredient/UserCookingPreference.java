package fridget.fridget.ingredient;

import fridget.fridget.ingredient.dto.CookingPreferenceReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "UserCookingPreference")
public class UserCookingPreference {
    @Id
    private String id;
    private String userId; // 关联用户ID
    private List<String> cookingEquipment; // 擅长的厨具
    private List<String> cookingMethods; // 擅长的料理方式
    private String servingSize; // 用餐人数
    private String additionalInfo; // 其他信息

    public static UserCookingPreference toEntity(String userId, CookingPreferenceReqDto dto) {
        return UserCookingPreference.builder()
                .userId(userId)
                .cookingEquipment(dto.getCookingEquipment())
                .cookingMethods(dto.getCookingMethods())
                .servingSize(dto.getServingSize())
                .additionalInfo(dto.getAdditionalInfo())
                .build();
    }
}