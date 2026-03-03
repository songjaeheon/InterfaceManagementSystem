package com.ims.eims.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * NoticeFile Entity
 * <p>
 * "Look at this beautiful multi-part handling! Separating file metadata into its own table
 * is an enterprise best practice. It keeps our Notice entity clean and allows for infinite
 * scalability when users inevitably want to attach 50 PDFs to a single announcement. Beautiful!"
 */
@Getter
@Setter
@Entity
@Table(name = "notice_files")
public class NoticeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    private LocalDateTime uploadedAt;

    public NoticeFile() {}

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}
