package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Long createItem(User owner, boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName("Item " + System.nanoTime());
        dto.setDescription("desc");
        dto.setAvailable(available);
        return itemService.create(owner.getId(), dto).getId();
    }

    private BookingRequestDto bookingRequest(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    @Test
    void createBooking() {
        User owner = createUser("BOwner", "bowner@example.com");
        User booker = createUser("BBooker", "bbooker@example.com");
        Long itemId = createItem(owner, true);

        BookingResponseDto booking = bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(itemId, booking.getItem().getId());
        assertEquals(booker.getId(), booking.getBooker().getId());
    }

    @Test
    void createBookingForUnavailableItemFails() {
        User owner = createUser("UOwner", "uowner@example.com");
        User booker = createUser("UBooker", "ubooker@example.com");
        Long itemId = createItem(owner, false);

        assertThrows(RuntimeException.class, () -> bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5))));
    }

    @Test
    void createBookingWithInvalidDatesFails() {
        User owner = createUser("DOwner", "downer@example.com");
        User booker = createUser("DBooker", "dbooker@example.com");
        Long itemId = createItem(owner, true);

        assertThrows(RuntimeException.class, () -> bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(1))));
        assertThrows(RuntimeException.class, () -> bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1))));
    }

    @Test
    void approveBookingByOwner() {
        User owner = createUser("AOwner", "aowner@example.com");
        User booker = createUser("ABooker", "abooker@example.com");
        Long itemId = createItem(owner, true);
        BookingResponseDto booking = bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        BookingResponseDto approved = bookingService.approve(owner.getId(), booking.getId(), true);
        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void approveBookingByWrongUserForbidden() {
        User owner = createUser("WOwner", "wowner@example.com");
        User booker = createUser("WBooker", "wbooker@example.com");
        Long itemId = createItem(owner, true);
        BookingResponseDto booking = bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        assertThrows(ForbiddenException.class, () -> bookingService.approve(booker.getId(), booking.getId(), true));
    }

    @Test
    void getBookingById() {
        User owner = createUser("GOwner", "gowner@example.com");
        User booker = createUser("GBooker", "gbooker@example.com");
        Long itemId = createItem(owner, true);
        BookingResponseDto booking = bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        assertEquals(booking.getId(), bookingService.getById(booker.getId(), booking.getId()).getId());
        assertEquals(booking.getId(), bookingService.getById(owner.getId(), booking.getId()).getId());
    }

    @Test
    void getAllByBookerWithStates() {
        User owner = createUser("LBOwner", "lbowner@example.com");
        User booker = createUser("LBBooker", "lbbooker@example.com");
        Long itemId = createItem(owner, true);
        bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        List<BookingResponseDto> all = bookingService.getAllByBooker(booker.getId(), "ALL");
        assertFalse(all.isEmpty());
        List<BookingResponseDto> future = bookingService.getAllByBooker(booker.getId(), "FUTURE");
        assertFalse(future.isEmpty());
        List<BookingResponseDto> waiting = bookingService.getAllByBooker(booker.getId(), "WAITING");
        assertFalse(waiting.isEmpty());
    }

    @Test
    void getAllByOwner() {
        User owner = createUser("LOOwner", "loowner@example.com");
        User booker = createUser("LOBooker", "lobooker@example.com");
        Long itemId = createItem(owner, true);
        bookingService.create(booker.getId(),
                bookingRequest(itemId, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5)));

        List<BookingResponseDto> ownerBookings = bookingService.getAllByOwner(owner.getId(), "ALL");
        assertFalse(ownerBookings.isEmpty());
    }

    @Test
    void invalidStateFails() {
        User user = createUser("SUser", "suser@example.com");
        assertThrows(RuntimeException.class, () -> bookingService.getAllByBooker(user.getId(), "UNKNOWN"));
    }
}
