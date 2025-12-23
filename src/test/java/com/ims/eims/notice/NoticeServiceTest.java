package com.ims.eims.notice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Notice Service Test.
 *
 * <p>
 * Look at that! A simple integration test to make sure our service and repository are wired correctly.
 * We're using the {@code @SpringBootTest} annotation to load the context.
 * It's Bootifully simple!
 * </p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Test
    public void testSaveAndFind() {
        Notice notice = new Notice("Test Title", "Test Content");
        Notice saved = noticeService.save(notice);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());

        Notice found = noticeService.findById(saved.getId());
        assertEquals("Test Title", found.getTitle());
    }
}
