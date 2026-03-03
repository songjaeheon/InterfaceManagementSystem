package com.ims.eims.repository;

import com.ims.eims.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
}
