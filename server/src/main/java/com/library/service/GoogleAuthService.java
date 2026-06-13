package com.library.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.library.model.entity.User;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private static final String GOOGLE_AUTH_PLACEHOLDER = "GOOGLE_OAUTH";

    private final GoogleIdTokenVerifier verifier;

    private final UserRepository userRepository;

    public GoogleAuthService(@Value("${google.client-id}") String clientId,
                             UserRepository userRepository) {
        this.userRepository = userRepository;
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Transactional
    public User authenticate(String idToken) {
        GoogleIdToken token = verify(idToken);
        GoogleIdToken.Payload payload = token.getPayload();
        return userRepository.findByEmail(payload.getEmail())
                .orElseGet(() -> createUser(payload));
    }

    private GoogleIdToken verify(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }
            return token;
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Failed to verify Google token");
        }
    }

    private User createUser(GoogleIdToken.Payload payload) {
        String name = (String) payload.get("name");
        return userRepository.save(User.builder()
                .email(payload.getEmail())
                .name(name != null ? name : payload.getEmail().split("@")[0])
                .avatarUrl((String) payload.get("picture"))
                .role("USER")
                .passwordHash(GOOGLE_AUTH_PLACEHOLDER)
                .build());
    }
}
