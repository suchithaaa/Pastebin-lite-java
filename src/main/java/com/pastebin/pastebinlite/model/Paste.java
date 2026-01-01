package com.pastebin.pastebinlite.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Paste {

    @Id
    private String id;

    @Lob
    private String content;

    private Long createdAt;
    private Long expiresAt;

    private Integer maxViews;
    private Integer viewsUsed = 0;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }

    public Integer getMaxViews() { return maxViews; }
    public void setMaxViews(Integer maxViews) { this.maxViews = maxViews; }

    public Integer getViewsUsed() { return viewsUsed; }
    public void setViewsUsed(Integer viewsUsed) { this.viewsUsed = viewsUsed; }
}
