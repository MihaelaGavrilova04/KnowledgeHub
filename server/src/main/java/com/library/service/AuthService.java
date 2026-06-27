package com.library.service;

import com.library.model.dto.UserResponse;
import com.library.model.entity.User;
import com.library.repository.UserRepository;
import com.library.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       GoogleAuthService googleAuthService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.googleAuthService = googleAuthService;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return UserResponse.from(user);
    }

    @Transactional
    public String loginWithGoogleAndGetToken(String idToken) {
        User user = googleAuthService.authenticate(idToken);
        return jwtUtil.generate(user.getId(), user.getEmail());
    }
}