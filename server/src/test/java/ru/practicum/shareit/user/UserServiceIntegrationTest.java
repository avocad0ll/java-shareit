package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto newUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    @Test
    void createAndGetUser() {
        UserDto created = userService.create(newUser("Alice", "alice@example.com"));
        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());
        assertEquals("alice@example.com", created.getEmail());

        UserDto fetched = userService.getById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals("Alice", fetched.getName());
    }

    @Test
    void updateUser() {
        UserDto created = userService.create(newUser("Bob", "bob@example.com"));
        UserDto update = new UserDto();
        update.setName("Bobby");
        UserDto updated = userService.update(created.getId(), update);

        assertEquals("Bobby", updated.getName());
        assertEquals("bob@example.com", updated.getEmail());
    }

    @Test
    void getAllUsers() {
        userService.create(newUser("U1", "u1@example.com"));
        userService.create(newUser("U2", "u2@example.com"));
        List<UserDto> users = userService.getAll();
        assertTrue(users.size() >= 2);
    }

    @Test
    void deleteUser() {
        UserDto created = userService.create(newUser("Temp", "temp@example.com"));
        userService.delete(created.getId());
        assertThrows(RuntimeException.class, () -> userService.getById(created.getId()));
    }
}
