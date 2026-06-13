package com.library.repository;

import com.library.model.entity.ReadingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, UUID> {

    List<ReadingListItem> findByList_Id(UUID listId);

    Optional<ReadingListItem> findByList_IdAndContent_Id(UUID listId, UUID contentId);

    boolean existsByList_IdAndContent_Id(UUID listId, UUID contentId);
}
