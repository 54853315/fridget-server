package fridget.fridget.user;

import fridget.fridget.common.CommonResponse;
import fridget.fridget.securities.JwtTokenProvider;
import fridget.fridget.securities.RefreshTokenService;
import fridget.fridget.user.dto.LoginReqDto;
import fridget.fridget.user.dto.UserCreateReqDto;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService,
                          JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/user/create")
    public ResponseEntity<CommonResponse> userCreate(@Valid @RequestBody UserCreateReqDto userCreateReqDto) {
        try {
            User user = userService.create(userCreateReqDto);
            return new ResponseEntity<>(new CommonResponse(HttpStatus.CREATED, "Sign Up Success!", user), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/doLogin")
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        User user = userService.login(loginReqDto);
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("token", accessToken);
        userInfo.put("refreshToken", refreshToken);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Login Success!", userInfo), HttpStatus.OK);
    }

    @PostMapping("/doLogout")
    public ResponseEntity<CommonResponse> logout(@RequestBody Map<String, String> request) {
        String accessToken = request.get("token");
        Claims claims = jwtTokenProvider.getClaims(accessToken);
        String userId = claims.getSubject();
        refreshTokenService.deleteRefreshToken(userId);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "Logout Success", null), HttpStatus.OK);
    }

}
