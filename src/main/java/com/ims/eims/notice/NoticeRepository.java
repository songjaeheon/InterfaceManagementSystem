package com.ims.eims.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Notice Repository.
 *
 * <p>
 * This interface extends {@code JpaRepository}.
 * Spring Data JPA will automatically implement this interface at runtime.
 * We get CRUD operations, paging, and sorting for free!
 * No implementation code required. How cool is that?
 * </p>
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
