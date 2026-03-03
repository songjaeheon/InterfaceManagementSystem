package com.ims.eims.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private boolean isPinned;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<NoticeFile> files = new java.util.ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Notice() {}

    // Helper method for bi-directional relationship
    public void addFile(NoticeFile file) {
        files.add(file);
        file.setNotice(this);
    }

    public void removeFile(NoticeFile file) {
        files.remove(file);
        file.setNotice(null);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
