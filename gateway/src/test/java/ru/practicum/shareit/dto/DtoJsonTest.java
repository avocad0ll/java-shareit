package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class DtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemJson;

    @Autowired
    private JacksonTester<BookItemRequestDto> bookingJson;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void itemDtoSerializesRequestId() throws Exception {
        ItemDto dto = new ItemDto(1L, "Drill", "powerful", true, 7L);
        String json = itemJson.write(dto).getJson();
        assertTrue(json.contains("\"requestId\":7"));
        assertTrue(json.contains("\"name\":\"Drill\""));
    }

    @Test
    void itemDtoDeserializes() throws Exception {
        String content = "{\"name\":\"Saw\",\"description\":\"sharp\",\"available\":false,\"requestId\":3}";
        ItemDto dto = itemJson.parseObject(content);
        assertEquals("Saw", dto.getName());
        assertEquals(3L, dto.getRequestId());
        assertFalse(dto.getAvailable());
    }

    @Test
    void itemDtoValidationFlagsBlankFields() {
        ItemDto dto = new ItemDto();
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }

    @Test
    void itemDtoValidationPassesWhenValid() {
        ItemDto dto = new ItemDto(1L, "Drill", "powerful", true, null);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void userDtoEmailValidation() {
        UserDto invalid = new UserDto(null, "Bob", "not-an-email");
        assertFalse(validator.validate(invalid).isEmpty());

        UserDto valid = new UserDto(null, "Bob", "bob@example.com");
        assertTrue(validator.validate(valid).isEmpty());
    }

    @Test
    void requestDtoRequiresDescription() {
        ItemRequestDto invalid = new ItemRequestDto(null, "");
        assertFalse(validator.validate(invalid).isEmpty());

        ItemRequestDto valid = new ItemRequestDto(null, "need a drill");
        assertTrue(validator.validate(valid).isEmpty());
    }

    @Test
    void bookingDtoSerializesDates() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 2, 10, 0);
        BookItemRequestDto dto = new BookItemRequestDto(5L, start, end);

        String json = bookingJson.write(dto).getJson();
        assertTrue(json.contains("2030-01-01T10:00:00"));
        assertTrue(json.contains("\"itemId\":5"));
    }

    @Test
    void bookingDtoValidationRejectsPastDates() {
        BookItemRequestDto dto = new BookItemRequestDto(5L,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));
        assertFalse(validator.validate(dto).isEmpty());
    }
}
