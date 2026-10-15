package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemAnswerDto> items = new ArrayList<>();

    public ItemRequestResponseDto() {
    }

    public ItemRequestResponseDto(Long id, String description, LocalDateTime created, List<ItemAnswerDto> items) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.items = items;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<ItemAnswerDto> getItems() {
        return items;
    }

    public void setItems(List<ItemAnswerDto> items) {
        this.items = items;
    }

    public static class ItemAnswerDto {
        private Long id;
        private String name;
        private Long ownerId;

        public ItemAnswerDto() {
        }

        public ItemAnswerDto(Long id, String name, Long ownerId) {
            this.id = id;
            this.name = name;
            this.ownerId = ownerId;
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

        public Long getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(Long ownerId) {
            this.ownerId = ownerId;
        }
    }
}
