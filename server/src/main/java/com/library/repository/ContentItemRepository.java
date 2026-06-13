package com.library.repository;

import com.library.model.entity.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContentItemRepository extends JpaRepository<ContentItem, UUID> {

    List<ContentItem> findByIsPublishedTrueOrderByCreatedAtDesc();

    List<ContentItem> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title);
}
