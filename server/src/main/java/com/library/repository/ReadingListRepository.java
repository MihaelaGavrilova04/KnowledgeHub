package com.library.repository;

import com.library.model.entity.ReadingList;
import com.library.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReadingListRepository extends JpaRepository<ReadingList, UUID> {

    Optional<ReadingList> findFirstByUser(User user);
}
