package com.library.controller;

import com.library.model.dto.ContentItemResponse;
import com.library.service.ReadingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reading-list")
public class ReadingListController {

    private final ReadingListService readingListService;

    public ReadingListController(ReadingListService readingListService) {
        this.readingListService = readingListService;
    }

    @GetMapping
    public ResponseEntity<List<ContentItemResponse>> getItems(Authentication authentication) {
        return ResponseEntity.ok(readingListService.getItems(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestBody Map<String, String> body,
                                         Authentication authentication) {
        readingListService.addItem(authentication.getName(), UUID.fromString(body.get("contentId")));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{contentId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID contentId,
                                            Authentication authentication) {
        readingListService.removeItem(authentication.getName(), contentId);
        return ResponseEntity.ok().build();
    }
}
