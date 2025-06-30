package fridget.fridget.ingredient.dto;

import lombok.Data;

import java.util.List;

@Data
public class CookingPreferenceReqDto {
    private List<String> cookingEquipment; // 擅长的厨具，如："炒锅", "烤箱", "蒸锅", "微波炉"
    private List<String> cookingMethods; // 擅长的料理方式，如："炒", "煮", "烤", "蒸", "炸"
    private String servingSize; // 用餐人数
    private String additionalInfo; // 其他信息或备注
}