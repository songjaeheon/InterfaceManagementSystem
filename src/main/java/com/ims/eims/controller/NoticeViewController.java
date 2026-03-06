package com.ims.eims.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Notice View Controller
 * <p>
 * "Here we are returning those rock-solid JSPs. Spring MVC's ViewResolver intercepts these
 * string returns and maps them perfectly to our WEB-INF/views directory."
 */
@Controller
@RequestMapping("/notices")
public class NoticeViewController {

    @GetMapping
    public String showNoticeList() {
        return "notice-list";
    }

    @GetMapping("/pinned")
    public String showPinnedNotices() {
        return "notice-pinned-list";
    }
}
