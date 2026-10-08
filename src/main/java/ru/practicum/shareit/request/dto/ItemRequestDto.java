package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;

    public ItemRequestDto() {
    }

    public ItemRequestDto(Long id, String description, Long requestorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }

    public static ItemRequestDtoBuilder builder() {
        return new ItemRequestDtoBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(Long requestorId) {
        this.requestorId = requestorId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public static class ItemRequestDtoBuilder {
        private Long id;
        private String description;
        private Long requestorId;
        private LocalDateTime created;

        public ItemRequestDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ItemRequestDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ItemRequestDtoBuilder requestorId(Long requestorId) {
            this.requestorId = requestorId;
            return this;
        }

        public ItemRequestDtoBuilder created(LocalDateTime created) {
            this.created = created;
            return this;
        }

        public ItemRequestDto build() {
            return new ItemRequestDto(id, description, requestorId, created);
        }
    }
}
