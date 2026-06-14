package com.library.service;

import com.library.model.dto.AnnotationCommentRequest;
import com.library.model.dto.AnnotationCommentResponse;
import com.library.model.dto.AnnotationRequest;
import com.library.model.dto.AnnotationResponse;
import com.library.model.entity.Annotation;
import com.library.model.entity.AnnotationComment;
import com.library.model.entity.ContentItem;
import com.library.model.entity.User;
import com.library.repository.AnnotationCommentRepository;
import com.library.repository.AnnotationRepository;
import com.library.repository.ContentItemRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AnnotationService {

    private final AnnotationRepository annotationRepository;

    private final AnnotationCommentRepository commentRepository;

    private final ContentItemRepository contentItemRepository;

    private final UserRepository userRepository;

    public AnnotationService(AnnotationRepository annotationRepository,
                              AnnotationCommentRepository commentRepository,
                              ContentItemRepository contentItemRepository,
                              UserRepository userRepository) {
        this.annotationRepository = annotationRepository;
        this.commentRepository = commentRepository;
        this.contentItemRepository = contentItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AnnotationResponse> getForContent(UUID contentId) {
        return annotationRepository.findByContentIdOrderByCreatedAtAsc(contentId).stream()
                .map(a -> AnnotationResponse.from(a, buildComments(a.getId())))
                .toList();
    }

    @Transactional
    public AnnotationResponse create(String email, AnnotationRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ContentItem content = contentItemRepository
                .findById(UUID.fromString(request.getContentId()))
                .orElseThrow();
        Annotation annotation = Annotation.builder()
                .user(user)
                .content(content)
                .rangeStart(0)
                .rangeEnd(0)
                .noteText(request.getNoteText())
                .build();
        return AnnotationResponse.from(annotationRepository.save(annotation), List.of());
    }

    @Transactional
    public void delete(String email, UUID id) {
        Annotation annotation = annotationRepository.findById(id).orElseThrow();
        if (!annotation.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorized to delete this annotation");
        }
        annotationRepository.delete(annotation);
    }

    @Transactional
    public AnnotationCommentResponse addComment(String email, UUID annotationId,
                                                 AnnotationCommentRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Annotation annotation = annotationRepository.findById(annotationId).orElseThrow();
        AnnotationComment comment = AnnotationComment.builder()
                .annotation(annotation)
                .user(user)
                .commentText(request.getCommentText())
                .build();
        return AnnotationCommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(String email, UUID commentId) {
        AnnotationComment comment = commentRepository.findById(commentId).orElseThrow();
        if (!comment.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    private List<AnnotationCommentResponse> buildComments(UUID annotationId) {
        return commentRepository.findByAnnotationIdAndParentIsNullOrderByCreatedAtAsc(annotationId)
                .stream()
                .map(AnnotationCommentResponse::from)
                .toList();
    }
}
