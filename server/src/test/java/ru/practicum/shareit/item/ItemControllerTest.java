package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto() {
        return new ItemDto(1L, "Drill", "Powerful drill", true, null);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(eq(1L), any())).thenReturn(itemDto());

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(eq(1L), eq(1L), any())).thenReturn(itemDto());

        mockMvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById() throws Exception {
        ItemWithBookingsDto dto = ItemWithBookingsDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();
        when(itemService.getById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByIdNotFoundReturnsNotFound() throws Exception {
        when(itemService.getById(1L, 99L)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/99").header(USER_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

    @Test
    void getAllByOwner() throws Exception {
        ItemWithBookingsDto dto = ItemWithBookingsDto.builder().id(1L).name("Drill").build();
        when(itemService.getAllByOwner(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search() throws Exception {
        when(itemService.search("drill")).thenReturn(List.of(itemDto()));

        mockMvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = new CommentDto(1L, "Nice", "Alice", LocalDateTime.now());
        when(itemService.addComment(eq(1L), eq(1L), any())).thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Nice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Nice"));
    }
}
