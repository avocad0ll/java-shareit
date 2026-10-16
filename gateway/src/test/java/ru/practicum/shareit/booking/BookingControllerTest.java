package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking() throws Exception {
        BookItemRequestDto request = new BookItemRequestDto(1L,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        when(bookingClient.bookItem(eq(1L), any())).thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createBookingWithPastStartReturnsBadRequest() throws Exception {
        BookItemRequestDto request = new BookItemRequestDto(1L,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getBookings() throws Exception {
        when(bookingClient.getBookings(eq(1L), any(), any(), any())).thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsWithUnknownStateReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingClient.getOwnerBookings(eq(1L), any())).thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingClient.approve(eq(1L), eq(2L), eq(true))).thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingClient.getBooking(1L, 2L)).thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(get("/bookings/2").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}
