package com.library.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private UUID id;

    private UUID uploaderId;

    private String title;

    private String description;

    private String contentType;

    private String uploaderName;

    private LocalDateTime uploadedAt;
}
