package fridget.fridget.common;


import fridget.fridget.user.Role;
import fridget.fridget.user.User;
import fridget.fridget.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitialDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUserId("admin").isEmpty()) {
            List<String> a = new ArrayList<>();
            a.add("tomato");
            a.add("peach");
            User admin = User.builder()
                    .name("Admin")
                    .userId("admin")
                    .userPassword(passwordEncoder.encode("1234"))
                    .role(Role.ADMIN)
                    .vegan("lacto")
                    .meatConsumption(1)
                    .fishConsumption(3)
                    .vegeConsumption(4)
                    .spiciness(3)
                    .allergies(a)
                    .build();
            userRepository.save(admin);
        }
    }
}