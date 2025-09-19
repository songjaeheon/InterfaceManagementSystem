package com.ims.eims.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Notice() {}

    // Getters and Setters (omitted for brevity)
    // You can use Lombok to reduce boilerplate
}
