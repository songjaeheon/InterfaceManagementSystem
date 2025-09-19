package com.ims.eims.service;

import com.ims.eims.dto.NoticeCreateRequestDto;
import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.dto.NoticeUpdateRequestDto;
import com.ims.eims.entity.Notice;
import com.ims.eims.repository.NoticeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // Single notice creation
    public NoticeDto createNotice(NoticeCreateRequestDto request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStartDate(Optional.ofNullable(request.getStartDate()).orElse(LocalDateTime.now()));
        notice.setEndDate(request.getEndDate());
        notice.setActive(request.isActive());
        notice.setPinned(request.isPinned());
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());

        Notice savedNotice = noticeRepository.save(notice);
        return new NoticeDto(savedNotice.getId(), savedNotice.getTitle(), savedNotice.getContent(), savedNotice.getStartDate(), savedNotice.getEndDate());
    }
    
    // Single notice retrieval
    public NoticeDto findNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));
        return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), notice.getStartDate(), notice.getEndDate());
    }

    // List of notices retrieval
    public List<NoticeListDto> findActiveNotices() {
        LocalDateTime now = LocalDateTime.now();
        List<Notice> notices = noticeRepository.findAllActiveAndValidNotices(now);
        
        return notices.stream()
                .map(n -> new NoticeListDto(n.getId(), n.getTitle(), n.getCreatedAt(), n.isPinned()))
                .collect(Collectors.toList());
    }

    // Singe notice updating
    @Transactional
    public NoticeDto updateNotice(Long id, NoticeUpdateRequestDto request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));

        // Update fields only if they are not null
        Optional.ofNullable(request.getTitle()).ifPresent(notice::setTitle);
        Optional.ofNullable(request.getContent()).ifPresent(notice::setContent);
        Optional.ofNullable(request.getStartDate()).ifPresent(notice::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(notice::setEndDate);
        Optional.ofNullable(request.getIsActive()).ifPresent(notice::setActive);
        Optional.ofNullable(request.getIsPinned()).ifPresent(notice::setPinned);
        
        notice.setUpdatedAt(LocalDateTime.now());

        Notice updatedNotice = noticeRepository.save(notice);
        return new NoticeDto(updatedNotice.getId(), updatedNotice.getTitle(), updatedNotice.getContent(), updatedNotice.getStartDate(), updatedNotice.getEndDate());
    }

    // Single notice deletion
    public void deleteNotice(Long id) {
        // Find the notice first to ensure it exists before attempting to delete
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));
        noticeRepository.delete(notice);
    }
}
