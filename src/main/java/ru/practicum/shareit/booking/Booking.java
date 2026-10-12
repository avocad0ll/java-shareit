package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    public Booking() {
    }

    public Booking(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getBooker() {
        return booker;
    }

    public void setBooker(User booker) {
        this.booker = booker;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public static class BookingBuilder {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Item item;
        private User booker;
        private BookingStatus status;

        public BookingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public BookingBuilder end(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public BookingBuilder item(Item item) {
            this.item = item;
            return this;
        }

        public BookingBuilder booker(User booker) {
            this.booker = booker;
            return this;
        }

        public BookingBuilder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public Booking build() {
            return new Booking(id, start, end, item, booker, status);
        }
    }
}
