package com.pastebin.pastebinlite;

import com.pastebin.pastebinlite.model.Paste;
import com.pastebin.pastebinlite.repository.PasteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@RestController
public class PasteController {

    private final PasteRepository repo;

    public PasteController(PasteRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/api/pastes")
    public ResponseEntity<?> createPaste(@RequestBody Map<String, Object> body) {

        String content = (String) body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "content is required"));
        }

        Integer ttl = body.get("ttl_seconds") == null ? null : (Integer) body.get("ttl_seconds");
        Integer maxViews = body.get("max_views") == null ? null : (Integer) body.get("max_views");

        Paste p = new Paste();
        p.setId(UUID.randomUUID().toString());
        p.setContent(content);
        p.setCreatedAt(System.currentTimeMillis());
        p.setViewsUsed(0);
        p.setMaxViews(maxViews);

        if (ttl != null) {
            p.setExpiresAt(System.currentTimeMillis() + ttl * 1000L);
        }

        repo.save(p);

        return ResponseEntity.ok(
                Map.of(
                        "id", p.getId(),
                        "url", "http://localhost:4321/p/" + p.getId()
                )
        );
    }

    @GetMapping("/api/pastes/{id}")
    public ResponseEntity<?> fetchPaste(@PathVariable String id, HttpServletRequest request) {

        Optional<Paste> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of());
        }

        Paste p = opt.get();
        long now = currentTime(request);

        if (p.getExpiresAt() != null && now > p.getExpiresAt()) {
            return ResponseEntity.status(404).body(Map.of());
        }

        if (p.getMaxViews() != null && p.getViewsUsed() >= p.getMaxViews()) {
            return ResponseEntity.status(404).body(Map.of());
        }

        p.setViewsUsed(p.getViewsUsed() + 1);
        repo.save(p);

        Map<String, Object> response = new HashMap<>();
        response.put("content", p.getContent());
        response.put("remaining_views",
                p.getMaxViews() == null ? null : p.getMaxViews() - p.getViewsUsed());
        response.put("expires_at", p.getExpiresAt());

        return ResponseEntity.ok(response);

    }
    
    @GetMapping("/test")
    public String test() {
        return "PASTE CONTROLLER WORKING";
    }


    @GetMapping("/p/{id}")
    public ResponseEntity<String> viewPaste(@PathVariable String id, HttpServletRequest request) {

        Optional<Paste> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Not Found");
        }

        Paste p = opt.get();
        long now = currentTime(request);

        if (p.getExpiresAt() != null && now > p.getExpiresAt()) {
            return ResponseEntity.status(404).body("Expired");
        }

        if (p.getMaxViews() != null && p.getViewsUsed() >= p.getMaxViews()) {
            return ResponseEntity.status(404).body("View limit exceeded");
        }

        return ResponseEntity.ok(
                "<html><body><pre>" +
                        HtmlUtils.htmlEscape(p.getContent()) +
                        "</pre></body></html>"
        );
    }

    private long currentTime(HttpServletRequest request) {
        if ("1".equals(System.getenv("TEST_MODE"))
                && request.getHeader("x-test-now-ms") != null) {
            return Long.parseLong(request.getHeader("x-test-now-ms"));
        }
        return System.currentTimeMillis();
    }
}
