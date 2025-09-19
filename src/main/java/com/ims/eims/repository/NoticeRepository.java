package com.ims.eims.repository;

import com.ims.eims.entity.Notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // Spring Data JPA Query Derivation:
    // Finds all active notices that are within the valid date range
    List<Notice> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByIsPinnedDescCreatedAtDesc(LocalDateTime now1, LocalDateTime now2);
    
    // Using @Query annotation for more complex or readable queries
    @Query("SELECT n FROM notices n " +
           "WHERE n.isActive = true " +
           "AND n.startDate <= :now " +
           "AND (n.endDate IS NULL OR n.endDate >= :now) " +
           "ORDER BY n.isPinned DESC, n.createdAt DESC")
    List<Notice> findAllActiveAndValidNotices(LocalDateTime now);
}
