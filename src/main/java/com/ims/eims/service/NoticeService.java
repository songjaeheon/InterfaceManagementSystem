package com.ims.eims.service;

import com.ims.eims.dto.NoticeDto;
import com.ims.eims.dto.NoticeListDto;
import com.ims.eims.entity.Notice;
import com.ims.eims.repository.NoticeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
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
}