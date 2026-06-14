package com.library.model.dto;

import com.library.model.entity.ContentItem;
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
public class ContentItemResponse {

    private UUID id;

    private String title;

    private String description;

    private String contentType;

    private String language;

    private String authorName;

    private String coverUrl;

    private Long views;

    private LocalDateTime createdAt;

    public static ContentItemResponse from(ContentItem item) {
        String author = item.getAuthor() != null ? item.getAuthor().getName() : null;
        return ContentItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .contentType(item.getContentType())
                .language(item.getLanguage())
                .authorName(author)
                .coverUrl(item.getCoverUrl())
                .views(item.getViews())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
