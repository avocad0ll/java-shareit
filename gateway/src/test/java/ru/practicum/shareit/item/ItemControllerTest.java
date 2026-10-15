package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto item(Long requestId) {
        return new ItemDto(1L, "Drill", "powerful", true, requestId);
    }

    @Test
    void createItem() throws Exception {
        when(itemClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok(item(null)));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item(null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createItemWithoutNameReturnsBadRequest() throws Exception {
        ItemDto invalid = new ItemDto(null, null, "desc", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createItemOnRequestWithRequestId() throws Exception {
        when(itemClient.create(eq(1L), any())).thenReturn(ResponseEntity.ok(item(5L)));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item(5L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(5));
    }

    @Test
    void updateItem() throws Exception {
        when(itemClient.update(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok(item(null)));

        mockMvc.perform(patch("/items/2")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(itemClient.getById(1L, 2L)).thenReturn(ResponseEntity.ok(item(null)));

        mockMvc.perform(get("/items/2").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwner() throws Exception {
        when(itemClient.getAllByOwner(1L)).thenReturn(ResponseEntity.ok(List.of(item(null))));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search() throws Exception {
        when(itemClient.search(eq(1L), eq("drill"))).thenReturn(ResponseEntity.ok(List.of(item(null))));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addComment() throws Exception {
        when(itemClient.addComment(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok(new CommentDto(1L, "nice")));

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"nice\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("nice"));
    }

    @Test
    void addCommentWithoutTextReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
