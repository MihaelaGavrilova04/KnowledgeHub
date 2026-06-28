package com.library.controller;

import com.library.model.dto.ResourceResponse;
import com.library.repository.UserRepository;
import com.library.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    private final UserRepository userRepository;

    public ResourceController(ResourceService resourceService, UserRepository userRepository) {
        this.resourceService = resourceService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ResourceResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasType = contentType != null && !contentType.isBlank();
        if (hasKeyword && hasType) {
            return ResponseEntity.ok(resourceService.searchByType(keyword, contentType, pageable));
        }
        if (hasKeyword) {
            return ResponseEntity.ok(resourceService.search(keyword, pageable));
        }
        if (hasType) {
            return ResponseEntity.ok(resourceService.listByType(contentType, pageable));
        }
        return ResponseEntity.ok(resourceService.listAll(pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceResponse> upload(
            @RequestPart MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "OTHER") String contentType,
            Authentication authentication) {
        UUID uploaderId = getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.upload(file, title, description, contentType, uploaderId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Map<String, String>> download(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("url", resourceService.getDownloadUrl(id)));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Map<String, String>> downloadFile(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("url", resourceService.getFileDownloadUrl(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        resourceService.delete(id, getUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    private UUID getUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow().getId();
    }
}
