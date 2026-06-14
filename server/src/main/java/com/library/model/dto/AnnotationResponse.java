package com.library.model.dto;

import com.library.model.entity.Annotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationResponse {

    private UUID id;

    private String noteText;

    private String authorName;

    private UUID authorId;

    private LocalDateTime createdAt;

    private List<AnnotationCommentResponse> comments;

    public static AnnotationResponse from(Annotation annotation,
                                           List<AnnotationCommentResponse> comments) {
        return AnnotationResponse.builder()
                .id(annotation.getId())
                .noteText(annotation.getNoteText())
                .authorName(annotation.getUser().getName())
                .authorId(annotation.getUser().getId())
                .createdAt(annotation.getCreatedAt())
                .comments(comments)
                .build();
    }
}
