package com.ims.eims.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Notice Service.
 *
 * <p>
 * This service encapsulates our business logic.
 * We mark it with {@code @Service} to let Spring manage it as a bean.
 * The {@code @Transactional} annotation ensures that our data operations are safe and atomic.
 * It's the best practice to keep logic here, not in the controller!
 * </p>
 */
@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public Notice findById(Long id) {
        return noticeRepository.findOne(id);
    }

    public Notice save(Notice notice) {
        if (notice.getCreatedAt() == null) {
            notice.setCreatedAt(LocalDateTime.now());
        }
        return noticeRepository.save(notice);
    }

    public void delete(Long id) {
        noticeRepository.delete(id);
    }
}
