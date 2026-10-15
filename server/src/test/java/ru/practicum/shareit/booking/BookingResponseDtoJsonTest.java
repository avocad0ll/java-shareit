package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void serializeBookingResponse() throws Exception {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2030, 5, 1, 12, 0));
        dto.setEnd(LocalDateTime.of(2030, 5, 2, 12, 0));
        dto.setStatus(BookingStatus.APPROVED);
        dto.setBooker(new BookingResponseDto.BookerDto(10L));
        dto.setItem(new BookingResponseDto.ItemDto(2L, "Drill"));

        String content = json.write(dto).getJson();
        assertTrue(content.contains("\"status\":\"APPROVED\""));
        assertTrue(content.contains("\"booker\":{\"id\":10}"));
        assertTrue(content.contains("\"item\":{\"id\":2,\"name\":\"Drill\"}"));
        assertTrue(content.contains("2030-05-01T12:00:00"));
    }

    @Test
    void deserializeBookingResponse() throws Exception {
        String content = "{\"id\":1,\"start\":\"2030-05-01T12:00:00\",\"end\":\"2030-05-02T12:00:00\","
                + "\"status\":\"WAITING\",\"booker\":{\"id\":10},\"item\":{\"id\":2,\"name\":\"Drill\"}}";
        BookingResponseDto dto = json.parseObject(content);
        assertEquals(1L, dto.getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
        assertEquals(10L, dto.getBooker().getId());
        assertEquals("Drill", dto.getItem().getName());
    }
}
