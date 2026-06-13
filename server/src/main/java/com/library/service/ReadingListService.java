package com.library.service;

import com.library.model.dto.ContentItemResponse;
import com.library.model.entity.ContentItem;
import com.library.model.entity.ReadingList;
import com.library.model.entity.ReadingListItem;
import com.library.model.entity.User;
import com.library.repository.ContentItemRepository;
import com.library.repository.ReadingListItemRepository;
import com.library.repository.ReadingListRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingListService {

    private static final String DEFAULT_LIST_NAME = "My Reading List";

    private final ReadingListRepository readingListRepository;

    private final ReadingListItemRepository readingListItemRepository;

    private final ContentItemRepository contentItemRepository;

    private final UserRepository userRepository;

    public ReadingListService(ReadingListRepository readingListRepository,
                               ReadingListItemRepository readingListItemRepository,
                               ContentItemRepository contentItemRepository,
                               UserRepository userRepository) {
        this.readingListRepository = readingListRepository;
        this.readingListItemRepository = readingListItemRepository;
        this.contentItemRepository = contentItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ContentItemResponse> getItems(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ReadingList list = getOrCreateList(user);
        return readingListItemRepository.findByListId(list.getId()).stream()
                .map(item -> ContentItemResponse.from(item.getContent()))
                .toList();
    }

    @Transactional
    public void addItem(String email, UUID contentId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ReadingList list = getOrCreateList(user);
        if (readingListItemRepository.existsByListIdAndContentId(list.getId(), contentId)) {
            return;
        }
        ContentItem content = contentItemRepository.findById(contentId).orElseThrow();
        readingListItemRepository.save(ReadingListItem.builder().list(list).content(content).build());
    }

    @Transactional
    public void removeItem(String email, UUID contentId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        ReadingList list = getOrCreateList(user);
        readingListItemRepository.findByListIdAndContentId(list.getId(), contentId)
                .ifPresent(readingListItemRepository::delete);
    }

    private ReadingList getOrCreateList(User user) {
        return readingListRepository.findFirstByUser(user).orElseGet(() ->
                readingListRepository.save(ReadingList.builder()
                        .user(user)
                        .name(DEFAULT_LIST_NAME)
                        .isPublic(false)
                        .build()));
    }
}
