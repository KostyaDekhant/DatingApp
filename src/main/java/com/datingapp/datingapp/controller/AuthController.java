package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.RefreshToken;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.exception.TokenRefreshException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.security.JwtUtil;
import com.datingapp.datingapp.services.RefreshTokenService;
import com.datingapp.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    record AuthResponse(String token, Integer userId) {}

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil,
            UserService userService,
            RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    record TokenRefreshRequest(String refreshToken) {}
    record TokenRefreshResponse(String token, String refreshToken, Integer userId) {}

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestToken = request.refreshToken();
        return refreshTokenService.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    String username = token.getUser().getLogin();
                    String newAccessToken = jwtUtil.generateToken(username);
                    // Optionally: issue a new refresh token
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(token.getUser().getPkUser());
                    return ResponseEntity.ok(
                            new TokenRefreshResponse(newAccessToken, newRefreshToken.getToken(), token.getUser().getPkUser())
                    );
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh токена нет в базе данных!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestParam("userId") Integer userId) {
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "Log out successful!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String login    = user.getLogin();
            String password = user.getPassword();
            // Spring Security проверит логин/пароль через UserDetailsService
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );

            // Если нет исключения — выдаём токен
            String token = jwtUtil.generateToken(login);
            Integer id = userService.getPkUserByLogin(login);
            log.info("Authentication successful");
            //return ResponseEntity.ok(new AuthResponse(token, id)); //Map.of("token", token)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(id);
            return ResponseEntity.ok(
                    new TokenRefreshResponse(token, refreshToken.getToken(), id)
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Неверный логин или пароль"));
        } catch (UserNotExistsExceptions e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) throws UserNotExistsExceptions {
        Integer userId = userService.signupUser(user);
        String token = jwtUtil.generateToken(user.getLogin());
        Integer id = userService.getPkUserByLogin(user.getLogin());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(id);
        return ResponseEntity.ok(
                new TokenRefreshResponse(token, refreshToken.getToken(), id)
        );
        //return ResponseEntity.ok(new AuthResponse(token, id));
    }



}
