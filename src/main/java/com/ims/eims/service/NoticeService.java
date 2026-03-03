package com.ims.eims.service;

import com.ims.eims.dto.NoticeCreateRequestDto;
import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.dto.NoticeFileDto;
import com.ims.eims.dto.NoticeUpdateRequestDto;
import com.ims.eims.entity.Notice;
import com.ims.eims.entity.NoticeFile;
import com.ims.eims.repository.NoticeFileRepository;
import com.ims.eims.repository.NoticeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final Path fileStorageLocation;

    public NoticeService(NoticeRepository noticeRepository, NoticeFileRepository noticeFileRepository) {
        this.noticeRepository = noticeRepository;
        this.noticeFileRepository = noticeFileRepository;
        // "Enterprise grade storage! We configure a dedicated path for our files.
        // In a real prod environment, this might be an NFS mount or S3, but for now, local disk it is!"
        this.fileStorageLocation = Paths.get("/app/data/uploads/notices").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    // Single notice creation with robust multi-file upload support
    // "We need ACID guarantees here. If the database saves but the disk fails, we roll back everything!"
    @Transactional(rollbackFor = Exception.class)
    public NoticeDto createNotice(NoticeCreateRequestDto request, List<MultipartFile> files) {
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

        // Save files if present
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    NoticeFile noticeFile = storeFile(file);
                    notice.addFile(noticeFile);
                }
            }
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

    // Single notice deletion with robust file cleanup
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "notices", allEntries = true) // Also clear cache on delete
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findOne(id);
        if (notice == null) {
             throw new IllegalArgumentException("Notice not found with id: " + id);
        }

        // "Clean up time! Orphan removal handles the DB records, but we must manually delete the physical files."
        for (NoticeFile file : notice.getFiles()) {
            try {
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // We log and continue, or throw to abort deletion. In an enterprise system,
                // you might queue this for a background job if it fails, but for strictness:
                throw new RuntimeException("Failed to delete physical file: " + file.getStoredFilename(), e);
            }
        }

        noticeRepository.delete(notice);
    }

    private NoticeFile storeFile(MultipartFile file) {
        // Normalize file name
        String originalFilename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(originalFilename.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            // Generate unique filename to avoid conflicts
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            NoticeFile noticeFile = new NoticeFile();
            noticeFile.setOriginalFilename(originalFilename);
            noticeFile.setStoredFilename(storedFilename);
            noticeFile.setFilePath(targetLocation.toString());
            noticeFile.setFileSize(file.getSize());

            return noticeFile;

        } catch (IOException ex) {
            // "Throwing a RuntimeException here ensures our @Transactional boundary kicks in and rolls back the DB!"
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    // Download File
    public Resource loadFileAsResource(Long fileId) {
        try {
            NoticeFile noticeFile = noticeFileRepository.findOne(fileId);
            if (noticeFile == null) {
                throw new IllegalArgumentException("File not found with id: " + fileId);
            }

            Path filePath = Paths.get(noticeFile.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found on disk: " + noticeFile.getOriginalFilename());
            }
        } catch (Exception ex) {
            throw new RuntimeException("File download error", ex);
        }
    }

    public NoticeFile getNoticeFile(Long fileId) {
        NoticeFile noticeFile = noticeFileRepository.findOne(fileId);
        if (noticeFile == null) {
            throw new IllegalArgumentException("File not found with id: " + fileId);
        }
        return noticeFile;
    }

    private NoticeDto mapToDto(Notice notice) {
        List<NoticeFileDto> fileDtos = new ArrayList<>();
        if (notice.getFiles() != null) {
            fileDtos = notice.getFiles().stream()
                    .map(f -> new NoticeFileDto(f.getId(), f.getOriginalFilename(), f.getFileSize()))
                    .collect(Collectors.toList());
        }
        return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getStartDate(), notice.getEndDate(), notice.isPinned(), fileDtos);
    }
}
