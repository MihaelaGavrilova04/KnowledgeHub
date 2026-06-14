package com.library.repository;

import com.library.model.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnotationRepository extends JpaRepository<Annotation, UUID> {

    List<Annotation> findByContentIdOrderByCreatedAtAsc(UUID contentId);
}
