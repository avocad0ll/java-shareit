package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handlesNoSuchElementException() {
        Map<String, String> result = handler.handleNotFoundException(new NoSuchElementException("missing"));
        assertEquals("missing", result.get("error"));
    }

    @Test
    void handlesNotFoundException() {
        Map<String, String> result = handler.handleNotFoundException(new NotFoundException("not found"));
        assertEquals("not found", result.get("error"));
    }

    @Test
    void handlesBadRequestException() {
        Map<String, String> result = handler.handleBadRequestException(new BadRequestException("bad request"));
        assertEquals("bad request", result.get("error"));
    }

    @Test
    void handlesConflictException() {
        Map<String, String> result = handler.handleConflictException(new ConflictException("conflict"));
        assertEquals("conflict", result.get("error"));
    }

    @Test
    void handlesForbiddenException() {
        Map<String, String> result = handler.handleForbiddenException(new ForbiddenException("forbidden"));
        assertEquals("forbidden", result.get("error"));
    }

    @Test
    void handlesOtherExceptions() {
        Map<String, String> result = handler.handleOtherExceptions(new RuntimeException("boom"));
        assertEquals("Internal server error", result.get("error"));
    }
}
