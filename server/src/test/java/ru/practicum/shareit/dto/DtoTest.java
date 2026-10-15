package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void userDtoBuilderAndAccessors() {
        UserDto dto = UserDto.builder().id(1L).name("Alice").email("a@e.com").build();
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("a@e.com", dto.getEmail());

        dto.setId(2L);
        dto.setName("Bob");
        dto.setEmail("b@e.com");
        assertEquals(2L, dto.getId());
        assertEquals("Bob", dto.getName());
        assertEquals("b@e.com", dto.getEmail());
    }

    @Test
    void itemDtoBuilderAndAccessors() {
        ItemDto dto = ItemDto.builder().id(1L).name("Drill").description("d").available(true).requestId(9L).build();
        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals("d", dto.getDescription());
        assertEquals(true, dto.getAvailable());
        assertEquals(9L, dto.getRequestId());

        dto.setId(2L);
        dto.setName("Saw");
        dto.setDescription("s");
        dto.setAvailable(false);
        dto.setRequestId(10L);
        assertEquals(2L, dto.getId());
        assertEquals("Saw", dto.getName());
        assertEquals("s", dto.getDescription());
        assertEquals(false, dto.getAvailable());
        assertEquals(10L, dto.getRequestId());
    }

    @Test
    void commentDtoBuilderAndAccessors() {
        CommentDto dto = CommentDto.builder().id(1L).text("nice").authorName("Alice").created(now).build();
        assertEquals(1L, dto.getId());
        assertEquals("nice", dto.getText());
        assertEquals("Alice", dto.getAuthorName());
        assertEquals(now, dto.getCreated());

        dto.setId(2L);
        dto.setText("ok");
        dto.setAuthorName("Bob");
        dto.setCreated(null);
        assertEquals(2L, dto.getId());
        assertEquals("ok", dto.getText());
        assertEquals("Bob", dto.getAuthorName());
        assertNull(dto.getCreated());
    }

    @Test
    void itemWithBookingsDtoBuilderAndAccessors() {
        ItemWithBookingsDto.BookingShortDto last = new ItemWithBookingsDto.BookingShortDto(1L, 2L);
        ItemWithBookingsDto.BookingShortDto next = new ItemWithBookingsDto.BookingShortDto(3L, 4L);
        ItemWithBookingsDto dto = ItemWithBookingsDto.builder()
                .id(1L)
                .name("Drill")
                .description("d")
                .available(true)
                .requestId(9L)
                .lastBooking(last)
                .nextBooking(next)
                .comments(List.of(new CommentDto()))
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals("d", dto.getDescription());
        assertEquals(true, dto.getAvailable());
        assertEquals(9L, dto.getRequestId());
        assertEquals(1L, dto.getLastBooking().getId());
        assertEquals(2L, last.getBookerId());
        assertEquals(3L, dto.getNextBooking().getId());
        assertEquals(4L, next.getBookerId());
        assertEquals(1, dto.getComments().size());

        last.setId(5L);
        last.setBookerId(6L);
        assertEquals(5L, last.getId());
        assertEquals(6L, last.getBookerId());

        dto.setId(2L);
        dto.setName("Saw");
        dto.setDescription("s");
        dto.setAvailable(false);
        dto.setRequestId(10L);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());
        assertEquals(2L, dto.getId());
        assertEquals("Saw", dto.getName());
        assertEquals("s", dto.getDescription());
        assertEquals(false, dto.getAvailable());
        assertEquals(10L, dto.getRequestId());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertTrue(dto.getComments().isEmpty());
    }

    @Test
    void bookingDtoBuilderAndAccessors() {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .itemId(9L)
                .bookerId(2L)
                .status("APPROVED")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());
        assertEquals(9L, dto.getItemId());
        assertEquals(2L, dto.getBookerId());
        assertEquals("APPROVED", dto.getStatus());

        dto.setId(2L);
        dto.setStart(null);
        dto.setEnd(null);
        dto.setItemId(10L);
        dto.setBookerId(3L);
        dto.setStatus("WAITING");
        assertEquals(2L, dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertEquals(10L, dto.getItemId());
        assertEquals(3L, dto.getBookerId());
        assertEquals("WAITING", dto.getStatus());
    }

    @Test
    void bookingRequestDtoAccessors() {
        BookingRequestDto dto = new BookingRequestDto(9L, now, now.plusDays(1));
        assertEquals(9L, dto.getItemId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());

        dto.setItemId(10L);
        dto.setStart(null);
        dto.setEnd(null);
        assertEquals(10L, dto.getItemId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
    }

    @Test
    void bookingResponseDtoAccessors() {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(now);
        dto.setEnd(now.plusDays(1));
        dto.setStatus(BookingStatus.APPROVED);
        dto.setBooker(new BookingResponseDto.BookerDto(2L));
        dto.setItem(new BookingResponseDto.ItemDto(3L, "Drill"));

        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals(3L, dto.getItem().getId());
        assertEquals("Drill", dto.getItem().getName());

        BookingResponseDto.BookerDto booker = new BookingResponseDto.BookerDto();
        booker.setId(5L);
        assertEquals(5L, booker.getId());

        BookingResponseDto.ItemDto item = new BookingResponseDto.ItemDto();
        item.setId(6L);
        item.setName("Saw");
        assertEquals(6L, item.getId());
        assertEquals("Saw", item.getName());
    }

    @Test
    void itemRequestDtoBuilderAndAccessors() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("need drill")
                .requestorId(2L)
                .created(now)
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("need drill", dto.getDescription());
        assertEquals(2L, dto.getRequestorId());
        assertEquals(now, dto.getCreated());

        dto.setId(3L);
        dto.setDescription("x");
        dto.setRequestorId(4L);
        dto.setCreated(null);
        assertEquals(3L, dto.getId());
        assertEquals("x", dto.getDescription());
        assertEquals(4L, dto.getRequestorId());
        assertNull(dto.getCreated());
    }

    @Test
    void itemRequestResponseDtoAccessors() {
        ItemRequestResponseDto.ItemAnswerDto answer = new ItemRequestResponseDto.ItemAnswerDto(1L, "Drill", 2L);
        ItemRequestResponseDto dto = new ItemRequestResponseDto(3L, "need", now, List.of(answer));
        assertEquals(3L, dto.getId());
        assertEquals("need", dto.getDescription());
        assertEquals(now, dto.getCreated());
        assertEquals(1, dto.getItems().size());
        assertEquals(1L, answer.getId());
        assertEquals("Drill", answer.getName());
        assertEquals(2L, answer.getOwnerId());

        answer.setId(4L);
        answer.setName("Saw");
        answer.setOwnerId(5L);
        assertEquals(4L, answer.getId());
        assertEquals("Saw", answer.getName());
        assertEquals(5L, answer.getOwnerId());

        dto.setId(6L);
        dto.setDescription("y");
        dto.setCreated(null);
        dto.setItems(List.of());
        assertEquals(6L, dto.getId());
        assertEquals("y", dto.getDescription());
        assertNull(dto.getCreated());
        assertTrue(dto.getItems().isEmpty());
    }
}
