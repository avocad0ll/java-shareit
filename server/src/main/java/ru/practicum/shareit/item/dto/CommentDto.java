package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto() {
    }

    public CommentDto(Long id, String text, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }

    public static CommentDtoBuilder builder() {
        return new CommentDtoBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public static class CommentDtoBuilder {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;

        public CommentDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommentDtoBuilder text(String text) {
            this.text = text;
            return this;
        }

        public CommentDtoBuilder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public CommentDtoBuilder created(LocalDateTime created) {
            this.created = created;
            return this;
        }

        public CommentDto build() {
            return new CommentDto(id, text, authorName, created);
        }
    }
}
