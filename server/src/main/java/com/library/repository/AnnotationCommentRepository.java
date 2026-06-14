package com.library.repository;

import com.library.model.entity.AnnotationComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnotationCommentRepository extends JpaRepository<AnnotationComment, UUID> {

    List<AnnotationComment> findByAnnotationIdAndParentIsNullOrderByCreatedAtAsc(UUID annotationId);
}
