package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperTest {

    private final LocalDateTime now = LocalDateTime.now();

    private User user() {
        return new User(1L, "Alice", "alice@example.com");
    }

    @Test
    void userMapperHandlesNullsAndValues() {
        assertNull(UserMapper.toUserDto(null));
        assertNull(UserMapper.toUser(null));

        UserDto dto = UserMapper.toUserDto(user());
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());

        User mapped = UserMapper.toUser(dto);
        assertEquals(1L, mapped.getId());
        assertEquals("alice@example.com", mapped.getEmail());
    }

    @Test
    void itemMapperHandlesRequestAndNulls() {
        assertNull(ItemMapper.toItemDto(null));
        assertNull(ItemMapper.toItem(null));

        ItemRequest request = new ItemRequest(5L, "need drill", user(), now);
        Item withRequest = new Item(2L, "Drill", "powerful", true, user(), request);
        ItemDto dto = ItemMapper.toItemDto(withRequest);
        assertEquals(2L, dto.getId());
        assertEquals(5L, dto.getRequestId());
        assertEquals(true, dto.getAvailable());

        Item withoutRequest = new Item(3L, "Saw", "sharp", false, user(), null);
        assertNull(ItemMapper.toItemDto(withoutRequest).getRequestId());

        Item mapped = ItemMapper.toItem(dto);
        assertEquals("Drill", mapped.getName());
        assertEquals("powerful", mapped.getDescription());
    }

    @Test
    void commentMapperHandlesAuthorAndNulls() {
        assertNull(CommentMapper.toCommentDto(null));

        Comment comment = new Comment(1L, "nice", new Item(), user(), now);
        CommentDto dto = CommentMapper.toCommentDto(comment);
        assertEquals(1L, dto.getId());
        assertEquals("nice", dto.getText());
        assertEquals("Alice", dto.getAuthorName());

        Comment withoutAuthor = new Comment(2L, "ok", new Item(), null, now);
        assertNull(CommentMapper.toCommentDto(withoutAuthor).getAuthorName());
    }

    @Test
    void bookingMapperHandlesValuesAndNulls() {
        assertNull(BookingMapper.toBookingDto(null));
        assertNull(BookingMapper.toBookingResponseDto(null));

        Item item = new Item(9L, "Drill", "d", true, user(), null);
        Booking booking = new Booking(1L, now, now.plusDays(1), item, user(), BookingStatus.APPROVED);
        BookingDto dto = BookingMapper.toBookingDto(booking);
        assertEquals(1L, dto.getId());
        assertEquals(9L, dto.getItemId());
        assertEquals(1L, dto.getBookerId());
        assertEquals("APPROVED", dto.getStatus());

        Booking empty = new Booking(2L, now, now.plusDays(1), null, null, null);
        BookingDto emptyDto = BookingMapper.toBookingDto(empty);
        assertNull(emptyDto.getItemId());
        assertNull(emptyDto.getBookerId());
        assertNull(emptyDto.getStatus());

        BookingResponseDto response = BookingMapper.toBookingResponseDto(booking);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getBooker().getId());
        assertEquals("Drill", response.getItem().getName());

        BookingResponseDto emptyResponse = BookingMapper.toBookingResponseDto(empty);
        assertNull(emptyResponse.getBooker());
        assertNull(emptyResponse.getItem());
    }

    @Test
    void itemRequestMapperHandlesValuesAndNulls() {
        assertNull(ItemRequestMapper.toItemRequestDto(null));
        assertNull(ItemRequestMapper.toItemRequestResponseDto(null, List.of()));

        ItemRequest request = new ItemRequest(5L, "need drill", user(), now);
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        assertEquals(5L, dto.getId());
        assertEquals("need drill", dto.getDescription());
        assertEquals(1L, dto.getRequestorId());

        ItemRequest withoutRequestor = new ItemRequest(6L, "x", null, now);
        assertNull(ItemRequestMapper.toItemRequestDto(withoutRequestor).getRequestorId());

        Item item = new Item(7L, "Drill", "d", true, user(), request);
        ItemRequestResponseDto response = ItemRequestMapper.toItemRequestResponseDto(request, List.of(item));
        assertEquals(1, response.getItems().size());
        assertEquals(7L, response.getItems().get(0).getId());
        assertEquals(1L, response.getItems().get(0).getOwnerId());

        ItemRequestResponseDto empty = ItemRequestMapper.toItemRequestResponseDto(request, null);
        assertTrue(empty.getItems().isEmpty());
    }
}
