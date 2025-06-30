package fridget.fridget.user.dto;

import fridget.fridget.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResDto {
    private String userId;

    public static UserResDto toUserResDto(User user) {
        UserResDtoBuilder userResDtoBuilder = UserResDto.builder();
        userResDtoBuilder.userId(user.getUserId());
        userResDtoBuilder.userId(user.getName());
        return userResDtoBuilder.build();
    }
}
