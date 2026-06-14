package com.library.model.dto;

import com.library.model.entity.AnnotationComment;
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
public class AnnotationCommentResponse {

    private UUID id;

    private String commentText;

    private String authorName;

    private LocalDateTime createdAt;

    public static AnnotationCommentResponse from(AnnotationComment comment) {
        return AnnotationCommentResponse.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .authorName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
