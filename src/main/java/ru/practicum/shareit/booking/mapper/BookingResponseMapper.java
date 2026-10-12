package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingResponseMapper {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        if (booking.getBooker() != null) {
            dto.setBooker(new BookingResponseDto.BookerDto(booking.getBooker().getId()));
        }
        if (booking.getItem() != null) {
            dto.setItem(new BookingResponseDto.ItemDto(booking.getItem().getId(), booking.getItem().getName()));
        }
        return dto;
    }
}
