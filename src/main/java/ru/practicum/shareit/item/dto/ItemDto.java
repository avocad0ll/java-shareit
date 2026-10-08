package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Available field is required")
    private Boolean available;

    private Long requestId;

    public ItemDto() {
    }

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    public static ItemDtoBuilder builder() {
        return new ItemDtoBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public static class ItemDtoBuilder {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;

        public ItemDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ItemDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ItemDtoBuilder available(Boolean available) {
            this.available = available;
            return this;
        }

        public ItemDtoBuilder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public ItemDto build() {
            return new ItemDto(id, name, description, available, requestId);
        }
    }
}
