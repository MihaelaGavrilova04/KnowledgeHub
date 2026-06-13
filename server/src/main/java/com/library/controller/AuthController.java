package com.library.controller;

import com.library.model.dto.GoogleAuthRequest;
import com.library.model.dto.UserResponse;
import com.library.model.entity.User;
import com.library.repository.UserRepository;
import com.library.security.JwtUtil;
import com.library.service.GoogleAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int COOKIE_MAX_AGE_SECONDS = 86400;

    private static final String AUTH_COOKIE_NAME = "AUTH_TOKEN";

    private final UserRepository userRepository;

    private final GoogleAuthService googleAuthService;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          GoogleAuthService googleAuthService,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.googleAuthService = googleAuthService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/google")
    public ResponseEntity<UserResponse> googleLogin(@RequestBody GoogleAuthRequest request,
                                                    HttpServletResponse response) {
        User user = googleAuthService.authenticate(request.getIdToken());
        String token = jwtUtil.generate(user.getId(), user.getEmail());
        response.addCookie(createAuthCookie(token));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addCookie(createClearCookie());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Void> status(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        return cookie;
    }

    private Cookie createClearCookie() {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
