package fridget.fridget.ingredient;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCookingPreferenceRepository extends MongoRepository<UserCookingPreference, String> {
    Optional<UserCookingPreference> findByUserId(String userId);
    void deleteByUserId(String userId);
}