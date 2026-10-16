package ru.practicum.shareit.item.dto;

import java.util.ArrayList;
import java.util.List;

public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();

    public ItemWithBookingsDto() {
    }

    public static ItemWithBookingsDtoBuilder builder() {
        return new ItemWithBookingsDtoBuilder();
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

    public BookingShortDto getLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(BookingShortDto lastBooking) {
        this.lastBooking = lastBooking;
    }

    public BookingShortDto getNextBooking() {
        return nextBooking;
    }

    public void setNextBooking(BookingShortDto nextBooking) {
        this.nextBooking = nextBooking;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public static class BookingShortDto {
        private Long id;
        private Long bookerId;

        public BookingShortDto() {
        }

        public BookingShortDto(Long id, Long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getBookerId() {
            return bookerId;
        }

        public void setBookerId(Long bookerId) {
            this.bookerId = bookerId;
        }
    }

    public static class ItemWithBookingsDtoBuilder {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
        private BookingShortDto lastBooking;
        private BookingShortDto nextBooking;
        private List<CommentDto> comments = new ArrayList<>();

        public ItemWithBookingsDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ItemWithBookingsDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemWithBookingsDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ItemWithBookingsDtoBuilder available(Boolean available) {
            this.available = available;
            return this;
        }

        public ItemWithBookingsDtoBuilder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public ItemWithBookingsDtoBuilder lastBooking(BookingShortDto lastBooking) {
            this.lastBooking = lastBooking;
            return this;
        }

        public ItemWithBookingsDtoBuilder nextBooking(BookingShortDto nextBooking) {
            this.nextBooking = nextBooking;
            return this;
        }

        public ItemWithBookingsDtoBuilder comments(List<CommentDto> comments) {
            this.comments = comments;
            return this;
        }

        public ItemWithBookingsDto build() {
            ItemWithBookingsDto dto = new ItemWithBookingsDto();
            dto.setId(id);
            dto.setName(name);
            dto.setDescription(description);
            dto.setAvailable(available);
            dto.setRequestId(requestId);
            dto.setLastBooking(lastBooking);
            dto.setNextBooking(nextBooking);
            dto.setComments(comments);
            return dto;
        }
    }
}
