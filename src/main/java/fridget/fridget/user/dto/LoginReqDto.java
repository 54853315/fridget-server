package fridget.fridget.user.dto;

import lombok.Data;

@Data
public class LoginReqDto {
    private String userId;
    private String userPassword;
}
