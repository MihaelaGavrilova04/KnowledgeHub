package com.library.controller;

import com.library.model.dto.AnnotationCommentRequest;
import com.library.model.dto.AnnotationCommentResponse;
import com.library.model.dto.AnnotationRequest;
import com.library.model.dto.AnnotationResponse;
import com.library.service.AnnotationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/annotations")
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping
    public ResponseEntity<List<AnnotationResponse>> list(@RequestParam UUID contentId) {
        return ResponseEntity.ok(annotationService.getForContent(contentId));
    }

    @PostMapping
    public ResponseEntity<AnnotationResponse> create(@RequestBody AnnotationRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(annotationService.create(authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        annotationService.delete(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<AnnotationCommentResponse> addComment(
            @PathVariable UUID id,
            @RequestBody AnnotationCommentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                annotationService.addComment(authentication.getName(), id, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                               Authentication authentication) {
        annotationService.deleteComment(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }
}
