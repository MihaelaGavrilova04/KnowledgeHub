package com.library.repository;

import com.library.model.entity.ReadingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, UUID> {

    List<ReadingListItem> findByListId(UUID listId);

    Optional<ReadingListItem> findByListIdAndContentId(UUID listId, UUID contentId);

    boolean existsByListIdAndContentId(UUID listId, UUID contentId);
}
