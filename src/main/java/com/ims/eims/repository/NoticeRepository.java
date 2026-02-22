package com.ims.eims.repository;

import com.ims.eims.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // "Spring Data JPA is magic! Just define the interface and let it do the heavy lifting."
    
    // Custom query to find active notices, pinned ones first
    @Query("SELECT n FROM Notice n " +
           "WHERE n.isActive = true " +
           "AND n.startDate <= :now " +
           "AND (n.endDate IS NULL OR n.endDate >= :now) " +
           "ORDER BY n.isPinned DESC, n.createdAt DESC")
    List<Notice> findAllActiveAndValidNotices(@Param("now") LocalDateTime now);
}
