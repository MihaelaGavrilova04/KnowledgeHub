package com.library.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int PASSWORD_HASH_MAX_LENGTH = 255;
    public static final int NAME_MAX_LENGTH = 100;
    public static final int AVATAR_URL_MAX_LENGTH = 500;
    public static final int ROLE_MAX_LENGTH = 20;
    public static final int SPECIALIZATION_MAX_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(name = "password_hash", nullable = false, length = PASSWORD_HASH_MAX_LENGTH)
    private String passwordHash;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "avatar_url", length = AVATAR_URL_MAX_LENGTH)
    private String avatarUrl;

    @Column(nullable = false, length = ROLE_MAX_LENGTH)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = SPECIALIZATION_MAX_LENGTH)
    private String specialization;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}