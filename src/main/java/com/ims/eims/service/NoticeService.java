package com.ims.eims.service;

import com.ims.eims.dto.NoticeCreateRequestDto;
import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.dto.NoticeUpdateRequestDto;
import com.ims.eims.entity.Notice;
import com.ims.eims.repository.NoticeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final Path fileStorageLocation;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
        // Define upload directory, e.g., "uploads" in the current working directory
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    // Single notice creation with file upload support
    public NoticeDto createNotice(NoticeCreateRequestDto request, MultipartFile file) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());

        // Handle dates - if startDate is null, default to now.
        // Wait, DTO might have nulls if not sent.
        if (request.getStartDate() == null) {
            notice.setStartDate(LocalDateTime.now());
        } else {
            notice.setStartDate(request.getStartDate());
        }

        notice.setEndDate(request.getEndDate());
        notice.setActive(request.isActive());
        notice.setPinned(request.isPinned());

        // Save file if present
        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            notice.setAttachmentPath(fileName);
        }

        Notice savedNotice = noticeRepository.save(notice);
        return mapToDto(savedNotice);
    }
    
    // Single notice retrieval
    public NoticeDto findNoticeById(Long id) {
        Notice notice = noticeRepository.findOne(id);
        if (notice == null) {
            throw new IllegalArgumentException("Notice not found with id: " + id);
        }
        return mapToDto(notice);
    }

    // List of notices retrieval - Cached!
    // "We need speed! Let's cache these results so we don't hit the database every time."
    @Cacheable("notices")
    public List<NoticeListDto> findActiveNotices() {
        LocalDateTime now = LocalDateTime.now();
        List<Notice> notices = noticeRepository.findAllActiveAndValidNotices(now);
        
        return notices.stream()
                .map(n -> new NoticeListDto(n.getId(), n.getTitle(), n.getCreatedAt(), n.isPinned()))
                .collect(Collectors.toList());
    }

    // Cache Eviction - Scheduled
    // "Stale data is bad data. We'll refresh our cache every 5 minutes to keep things fresh."
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    @CacheEvict(value = "notices", allEntries = true)
    public void evictCache() {
        // Log or just let it happen
        System.out.println("Evicting notice cache...");
    }

    // Single notice updating
    @Transactional
    @CacheEvict(value = "notices", allEntries = true) // Also clear cache on update
    public NoticeDto updateNotice(Long id, NoticeUpdateRequestDto request) {
        Notice notice = noticeRepository.findOne(id);
        if (notice == null) {
            throw new IllegalArgumentException("Notice not found with id: " + id);
        }

        // Update fields only if they are not null
        Optional.ofNullable(request.getTitle()).ifPresent(notice::setTitle);
        Optional.ofNullable(request.getContent()).ifPresent(notice::setContent);
        Optional.ofNullable(request.getStartDate()).ifPresent(notice::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(notice::setEndDate);
        Optional.ofNullable(request.getIsActive()).ifPresent(notice::setActive);
        Optional.ofNullable(request.getIsPinned()).ifPresent(notice::setPinned);
        
        // Note: File update logic not implemented here as per requirements focusing on creation form

        Notice updatedNotice = noticeRepository.save(notice);
        return mapToDto(updatedNotice);
    }

    // Single notice deletion
    @CacheEvict(value = "notices", allEntries = true) // Also clear cache on delete
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findOne(id);
        if (notice == null) {
             throw new IllegalArgumentException("Notice not found with id: " + id);
        }
        noticeRepository.delete(notice);
    }

    private String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Generate unique filename to avoid conflicts
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    private NoticeDto mapToDto(Notice notice) {
        return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getStartDate(), notice.getEndDate(), notice.getAttachmentPath());
    }
}
