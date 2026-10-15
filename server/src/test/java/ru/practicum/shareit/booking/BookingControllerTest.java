package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingResponseDto response() {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setStatus(BookingStatus.APPROVED);
        dto.setBooker(new BookingResponseDto.BookerDto(2L));
        dto.setItem(new BookingResponseDto.ItemDto(3L, "Drill"));
        return dto;
    }

    @Test
    void createBooking() throws Exception {
        BookingRequestDto request = new BookingRequestDto(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingService.create(eq(2L), any())).thenReturn(response());

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approve(1L, 1L, true)).thenReturn(response());

        mockMvc.perform(patch("/bookings/1").param("approved", "true").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveByWrongUserReturnsForbidden() throws Exception {
        when(bookingService.approve(2L, 1L, true)).thenThrow(new ForbiddenException("Not the owner"));

        mockMvc.perform(patch("/bookings/1").param("approved", "true").header(USER_HEADER, 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Not the owner"));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(1L, 1L)).thenReturn(response());

        mockMvc.perform(get("/bookings/1").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(1L, "ALL")).thenReturn(List.of(response()));

        mockMvc.perform(get("/bookings").param("state", "ALL").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllByBookerWithoutState() throws Exception {
        when(bookingService.getAllByBooker(1L, null)).thenReturn(List.of(response()));

        mockMvc.perform(get("/bookings").header(USER_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(1L, "WAITING")).thenReturn(List.of(response()));

        mockMvc.perform(get("/bookings/owner").param("state", "WAITING").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
