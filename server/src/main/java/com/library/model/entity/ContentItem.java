package com.library.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "content_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentItem {

    public static final int TITLE_MAX_LENGTH = 500;
    public static final int CONTENT_TYPE_MAX_LENGTH = 20;
    public static final int URL_MAX_LENGTH = 500;
    public static final int LANGUAGE_MAX_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_type", nullable = false, length = CONTENT_TYPE_MAX_LENGTH)
    private String contentType;  // "ARTICLE", "RESEARCH", "PDF"

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;

    @Column(name = "file_url", length = URL_MAX_LENGTH)
    private String fileUrl;

    @Column(name = "minio_key", length = URL_MAX_LENGTH)
    private String minioKey;

    @Column(name = "cover_url", length = URL_MAX_LENGTH)
    private String coverUrl;

    @Column(length = LANGUAGE_MAX_LENGTH)
    private String language;

    @Column(columnDefinition = "INTEGER DEFAULT 1")
    private Integer version;

    @Column(name = "is_published")
    private Boolean isPublished;

    private Long views;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", insertable = false, updatable = false)
    private User uploader;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}