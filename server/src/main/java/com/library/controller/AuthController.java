package com.library.controller;

import com.library.model.dto.GoogleAuthRequest;
import com.library.model.dto.UserResponse;
import com.library.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int COOKIE_MAX_AGE_SECONDS = 86400;
    private static final String AUTH_COOKIE_NAME = "AUTH_TOKEN";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponse response = authService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<Void> googleLogin(@RequestBody GoogleAuthRequest request,
                                            HttpServletResponse response) {
        String jwtToken = authService.loginWithGoogleAndGetToken(request.getIdToken());
        response.addCookie(createAuthCookie(jwtToken));
        return ResponseEntity.ok().build();
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