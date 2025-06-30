package fridget.fridget.ingredient;

import fridget.fridget.common.EntityNotFoundException;
import fridget.fridget.ingredient.dto.IngredientsReqDto;
import fridget.fridget.ingredient.dto.CookingPreferenceReqDto;
import fridget.fridget.user.User;
import fridget.fridget.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class IngredientService {
    private final UserRepository userRepository;
    private final UserIngredientRepository userIngredientRepository;
    private final UserCookingPreferenceRepository cookingPreferenceRepository;

    public IngredientService(UserRepository userRepository,
                             UserIngredientRepository userIngredientRepository,
                             UserCookingPreferenceRepository cookingPreferenceRepository) {
        this.userRepository = userRepository;
        this.userIngredientRepository = userIngredientRepository;
        this.cookingPreferenceRepository = cookingPreferenceRepository;
    }

    public List<UserIngredient> findMyIngredients() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                "There's no such user."));
        return user.getUserIngredients();
    }

    public List<UserIngredient> createIngredients(@RequestBody List<IngredientsReqDto> ingredientsReqDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                "There's no such user."));
        List<UserIngredient> newIngredients = ingredientsReqDtos.stream()
                .map(UserIngredient::toEntity)
                .map(userIngredientRepository::save)
                .collect(Collectors.toList());
        user.getUserIngredients().addAll(newIngredients);
        userRepository.save(user);
        return user.getUserIngredients();
    }

    public List<UserIngredient> deleteIngredients(List<IngredientsReqDto> ingredientsReqDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                "There's no such user."));
        List<UserIngredient> userIngredients = user.getUserIngredients();
        List<UserIngredient> ingredientsToRemove = userIngredients.stream()
                .filter(ingredient -> ingredientsReqDtos.stream()
                        .anyMatch(dto -> dto.getName().equals(ingredient.getName())
                                && dto.getCategory().equals(ingredient.getCategory())))
                .collect(Collectors.toList());
        if (ingredientsToRemove.isEmpty()) {
            throw new EntityNotFoundException("No matching ingredients found for deletion!");
        }
        userIngredients.removeAll(ingredientsToRemove);
        userIngredientRepository.deleteAll(ingredientsToRemove);
        userRepository.save(user);
        return ingredientsToRemove;
    }

    // 获取用户烹饪偏好
    public UserCookingPreference getMyCookingPreference() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return cookingPreferenceRepository.findByUserId(userId)
                .orElse(null);
    }

    // 创建或更新用户烹饪偏好
    public UserCookingPreference saveCookingPreference(CookingPreferenceReqDto cookingPreferenceReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        // 检查用户是否存在
        userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                "There's no such user."));
        
        // 查找现有偏好设置
        Optional<UserCookingPreference> existingPreference = cookingPreferenceRepository.findByUserId(userId);
        
        UserCookingPreference cookingPreference;
        if (existingPreference.isPresent()) {
            // 更新现有偏好
            cookingPreference = existingPreference.get();
            cookingPreference.setCookingEquipment(cookingPreferenceReqDto.getCookingEquipment());
            cookingPreference.setCookingMethods(cookingPreferenceReqDto.getCookingMethods());
            cookingPreference.setServingSize(cookingPreferenceReqDto.getServingSize());
            cookingPreference.setAdditionalInfo(cookingPreferenceReqDto.getAdditionalInfo());
        } else {
            // 创建新的偏好设置
            cookingPreference = UserCookingPreference.toEntity(userId, cookingPreferenceReqDto);
        }
        
        return cookingPreferenceRepository.save(cookingPreference);
    }

    // 删除用户烹饪偏好
    public void deleteCookingPreference() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        UserCookingPreference preference = cookingPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("No cooking preference found for this user."));
        
        cookingPreferenceRepository.deleteByUserId(userId);
    }
}
