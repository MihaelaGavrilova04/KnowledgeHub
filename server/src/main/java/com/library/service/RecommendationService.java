package com.library.service;

import com.library.model.dto.ResourceResponse;
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class RecommendationService {

    private static final int MAX_RECOMMENDATIONS = 5;

    private final UserRepository userRepository;

    private final ReadingListRepository readingListRepository;

    private final ReadingListItemRepository readingListItemRepository;

    private final ContentItemRepository contentItemRepository;

    public RecommendationService(UserRepository userRepository,
                                  ReadingListRepository readingListRepository,
                                  ReadingListItemRepository readingListItemRepository,
                                  ContentItemRepository contentItemRepository) {
        this.userRepository = userRepository;
        this.readingListRepository = readingListRepository;
        this.readingListItemRepository = readingListItemRepository;
        this.contentItemRepository = contentItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getRecommendations(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Optional<ReadingList> listOpt = readingListRepository.findFirstByUser(user);

        Set<UUID> savedIds = new HashSet<>();
        Map<String, Long> typeFrequency = new HashMap<>();

        if (listOpt.isPresent()) {
            List<ReadingListItem> items = readingListItemRepository.findByListId(listOpt.get().getId());
            for (ReadingListItem item : items) {
                savedIds.add(item.getContent().getId());
                typeFrequency.merge(item.getContent().getContentType(), 1L, Long::sum);
            }
        }

        List<ContentItem> candidates = savedIds.isEmpty()
                ? contentItemRepository.findByIsPublishedTrueOrderByCreatedAtDesc()
                : contentItemRepository.findByIsPublishedTrueAndIdNotIn(savedIds);

        return candidates.stream()
                .sorted(Comparator
                        .comparingLong((ContentItem c) -> typeFrequency.getOrDefault(c.getContentType(), 0L))
                        .reversed()
                        .thenComparing(Comparator.comparing(ContentItem::getCreatedAt).reversed()))
                .limit(MAX_RECOMMENDATIONS)
                .map(this::toResponse)
                .toList();
    }

    private ResourceResponse toResponse(ContentItem item) {
        String uploaderName = item.getUploader() != null ? item.getUploader().getName() : "Unknown";
        return ResourceResponse.builder()
                .id(item.getId())
                .uploaderId(item.getUploadedBy())
                .title(item.getTitle())
                .description(item.getDescription())
                .contentType(item.getContentType())
                .uploaderName(uploaderName)
                .uploadedAt(item.getCreatedAt())
                .build();
    }
}
