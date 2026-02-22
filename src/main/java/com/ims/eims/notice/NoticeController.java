package com.ims.eims.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Notice Controller (MVC).
 *
 * <p>
 * This is a classic Spring MVC controller, annotated with {@code @Controller}.
 * Unlike the REST controller, this one returns Strings that resolve to JSP views.
 * It's the bridge between our backend logic and the server-side rendered HTML.
 * </p>
 */
@Controller
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public String list(Model model) {
        // We put the data into the model so the JSP can render it.
        model.addAttribute("notices", noticeService.findAll());
        return "notice/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.findById(id));
        return "notice/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.findById(id));
        return "notice/form";
    }

    @PostMapping
    public String save(@ModelAttribute Notice notice) {
        noticeService.save(notice);
        return "redirect:/notices";
    }

    // Also mapping the root context or a dashboard to the index page which uses Vue
    @GetMapping("/dashboard")
    public String dashboard() {
        return "notice/index";
    }
}
