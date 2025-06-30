package fridget.fridget.user;


import fridget.fridget.common.EntityNotFoundException;
import fridget.fridget.user.dto.LoginReqDto;
import fridget.fridget.user.dto.UserCreateReqDto;
import fridget.fridget.user.dto.UserPreferenceDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    public User create(UserCreateReqDto userCreateReqDto) {
        if (userRepository.findByUserId(userCreateReqDto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("UserID is already in use.");
        }
        userCreateReqDto.setUserPassword(passwordEncoder.encode(userCreateReqDto.getUserPassword()));
        User user = User.toEntity(userCreateReqDto);
        return userRepository.save(user);
    }

    public User login(LoginReqDto loginReqDto) throws IllegalArgumentException {
        User user = userRepository.findByUserId(loginReqDto.getUserId()).orElseThrow(()
                -> new IllegalArgumentException("Login Failed! There's no such user."));
        if (!passwordEncoder.matches(loginReqDto.getUserPassword(), user.getUserPassword())) {
            throw new IllegalArgumentException("Wrong password!");
        }
        return user;
    }

    public UserPreferenceDto findMyPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(
                "There's no such user."));
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setVegan(user.getVegan());
        userPreferenceDto.setMeatConsumption(user.getMeatConsumption());
        userPreferenceDto.setFishConsumption(user.getFishConsumption());
        userPreferenceDto.setVegeConsumption(user.getVegeConsumption());
        userPreferenceDto.setSpiciness(user.getSpiciness());
        userPreferenceDto.setAllergies(user.getAllergies());
        return userPreferenceDto;
    }

}
