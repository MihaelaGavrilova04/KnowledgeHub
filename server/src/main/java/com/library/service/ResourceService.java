package com.library.service;

import com.library.model.dto.ResourceResponse;
import com.library.model.entity.ContentItem;
import com.library.model.entity.User;
import com.library.repository.ContentItemRepository;
import com.library.repository.UserRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ResourceService {

    private static final int PRESIGNED_URL_EXPIRY_MINUTES = 5;

    private static final String DISPOSITION_PARAM = "response-content-disposition";

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/epub+zip",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    private final ContentItemRepository contentItemRepository;

    private final UserRepository userRepository;

    public ResourceService(MinioClient minioClient,
                           ContentItemRepository contentItemRepository,
                           UserRepository userRepository) {
        this.minioClient = minioClient;
        this.contentItemRepository = contentItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ResourceResponse> listAll(Pageable pageable) {
        return contentItemRepository.findByIsPublishedTrue(pageable)
                .map(item -> toResponse(item, getUploaderName(item)));
    }

    @Transactional(readOnly = true)
    public Page<ResourceResponse> search(String keyword, Pageable pageable) {
        return contentItemRepository.searchPublished(keyword, pageable)
                .map(item -> toResponse(item, getUploaderName(item)));
    }

    @Transactional
    public ResourceResponse upload(MultipartFile file, String title, String description, UUID uploaderId) {
        validateFile(file);
        String key = uploaderId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("File upload failed");
        }
        User uploader = userRepository.findById(uploaderId).orElseThrow();
        ContentItem item = ContentItem.builder()
                .title(title)
                .description(description)
                .contentType(file.getContentType())
                .authorId(uploaderId)
                .uploadedBy(uploaderId)
                .minioKey(key)
                .isPublished(true)
                .views(0L)
                .version(1)
                .build();
        ContentItem saved = contentItemRepository.save(item);
        return toResponse(saved, uploader.getName());
    }

    @Transactional(readOnly = true)
    public String getDownloadUrl(UUID resourceId) {
        ContentItem item = contentItemRepository.findById(resourceId).orElseThrow();
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(item.getMinioKey())
                    .method(Method.GET)
                    .expiry(PRESIGNED_URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Could not generate download URL");
        }
    }

    @Transactional(readOnly = true)
    public String getFileDownloadUrl(UUID resourceId) {
        ContentItem item = contentItemRepository.findById(resourceId).orElseThrow();
        String minioKey = item.getMinioKey();
        String filenamePart = minioKey.substring(minioKey.lastIndexOf('/') + 1);
        String filename = filenamePart.substring(filenamePart.indexOf('_') + 1);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(minioKey)
                    .method(Method.GET)
                    .expiry(PRESIGNED_URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                    .extraQueryParams(Map.of(DISPOSITION_PARAM, "attachment; filename=\"" + filename + "\""))
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Could not generate file download URL");
        }
    }

    @Transactional
    public void delete(UUID resourceId, UUID requesterId) {
        ContentItem item = contentItemRepository.findById(resourceId).orElseThrow();
        User requester = userRepository.findById(requesterId).orElseThrow();
        boolean isOwner = item.getUploadedBy().equals(requesterId);
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        if (!isOwner && !isAdmin) {
            throw new IllegalStateException("You are not allowed to delete this resource");
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(item.getMinioKey())
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Could not delete file from storage");
        }
        contentItemRepository.delete(item);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF, EPUB, and DOCX files are allowed");
        }
    }

    private String getUploaderName(ContentItem item) {
        User uploader = item.getUploader();
        return uploader != null ? uploader.getName() : "Unknown";
    }

    private ResourceResponse toResponse(ContentItem item, String uploaderName) {
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
