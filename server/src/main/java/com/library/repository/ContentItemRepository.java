package com.library.repository;

import com.library.model.entity.ContentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ContentItemRepository extends JpaRepository<ContentItem, UUID> {

    List<ContentItem> findByIsPublishedTrueOrderByCreatedAtDesc();

    List<ContentItem> findByIsPublishedTrueAndIdNotIn(Set<UUID> ids);

    List<ContentItem> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title);

    Page<ContentItem> findByIsPublishedTrue(Pageable pageable);

    Page<ContentItem> findByIsPublishedTrueAndContentType(String contentType, Pageable pageable);

    @Query("SELECT c FROM ContentItem c WHERE c.isPublished = true "
            + "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContentItem> searchPublished(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM ContentItem c WHERE c.isPublished = true "
            + "AND c.contentType = :contentType "
            + "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContentItem> searchPublishedByType(@Param("keyword") String keyword,
                                             @Param("contentType") String contentType,
                                             Pageable pageable);

    List<ContentItem> findByUploadedBy(UUID userId);
}