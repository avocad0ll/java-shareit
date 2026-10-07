package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    public ItemRequest() {
    }

    public ItemRequest(Long id, String description, User requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }

    public static ItemRequestBuilder builder() {
        return new ItemRequestBuilder();
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

    public User getRequestor() {
        return requestor;
    }

    public void setRequestor(User requestor) {
        this.requestor = requestor;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public static class ItemRequestBuilder {
        private Long id;
        private String description;
        private User requestor;
        private LocalDateTime created;

        public ItemRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ItemRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ItemRequestBuilder requestor(User requestor) {
            this.requestor = requestor;
            return this;
        }

        public ItemRequestBuilder created(LocalDateTime created) {
            this.created = created;
            return this;
        }

        public ItemRequest build() {
            return new ItemRequest(id, description, requestor, created);
        }
    }
}
