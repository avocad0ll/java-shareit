package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private ItemRequestDto request(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        return dto;
    }

    @Test
    void createRequest() {
        User user = createUser("Req", "req@example.com");
        ItemRequestResponseDto created = itemRequestService.create(user.getId(), request("need a hammer"));

        assertNotNull(created.getId());
        assertEquals("need a hammer", created.getDescription());
        assertNotNull(created.getCreated());
        assertTrue(created.getCreated().isBefore(LocalDateTime.now().plusSeconds(5)));
        assertNotNull(created.getItems());
        assertTrue(created.getItems().isEmpty());
    }

    @Test
    void getOwnRequestsOrderedNewestFirst() {
        User user = createUser("Owner", "owner@example.com");
        itemRequestService.create(user.getId(), request("first"));
        itemRequestService.create(user.getId(), request("second"));

        List<ItemRequestResponseDto> requests = itemRequestService.getOwnRequests(user.getId());
        assertEquals(2, requests.size());
        assertTrue(requests.get(0).getCreated().compareTo(requests.get(1).getCreated()) >= 0);
    }

    @Test
    void getAllRequestsExcludesOwn() {
        User user1 = createUser("A", "a@example.com");
        User user2 = createUser("B", "b@example.com");
        itemRequestService.create(user1.getId(), request("from user1"));

        List<ItemRequestResponseDto> all = itemRequestService.getAllRequests(user2.getId());
        assertTrue(all.stream().anyMatch(r -> "from user1".equals(r.getDescription())));
        assertTrue(all.stream().noneMatch(r -> user1.getId().equals(user2.getId())));
    }

    @Test
    void getRequestByIdReturnsItems() {
        User user = createUser("ById", "byid@example.com");
        ItemRequestResponseDto created = itemRequestService.create(user.getId(), request("find me"));
        ItemRequestResponseDto fetched = itemRequestService.getById(user.getId(), created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals("find me", fetched.getDescription());
        assertNotNull(fetched.getItems());
    }

    @Test
    void getRequestByIdNotFound() {
        User user = createUser("NF", "nf@example.com");
        assertThrows(RuntimeException.class, () -> itemRequestService.getById(user.getId(), 999999L));
    }
}
