package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private String status;

    public BookingDto() {
    }

    public BookingDto(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId, String status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
    }

    public static BookingDtoBuilder builder() {
        return new BookingDtoBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getBookerId() {
        return bookerId;
    }

    public void setBookerId(Long bookerId) {
        this.bookerId = bookerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class BookingDtoBuilder {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long itemId;
        private Long bookerId;
        private String status;

        public BookingDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingDtoBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public BookingDtoBuilder end(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public BookingDtoBuilder itemId(Long itemId) {
            this.itemId = itemId;
            return this;
        }

        public BookingDtoBuilder bookerId(Long bookerId) {
            this.bookerId = bookerId;
            return this;
        }

        public BookingDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookingDto build() {
            return new BookingDto(id, start, end, itemId, bookerId, status);
        }
    }
}
