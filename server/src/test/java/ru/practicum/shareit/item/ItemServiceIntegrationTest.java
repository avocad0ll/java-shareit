package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private ItemDto itemDto(String name, String description, boolean available, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }

    @Test
    void createItemWithoutRequest() {
        User owner = createUser("Owner", "owner-item@example.com");
        ItemDto created = itemService.create(owner.getId(), itemDto("Drill", "power", true, null));

        assertNotNull(created.getId());
        assertEquals("Drill", created.getName());
        assertNull(created.getRequestId());
    }

    @Test
    void createItemWithRequest() {
        User owner = createUser("OwnerReq", "ownerreq@example.com");
        User requestor = createUser("Req", "req-item@example.com");
        ItemRequest request = new ItemRequest();
        request.setDescription("need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        ItemDto created = itemService.create(owner.getId(), itemDto("Drill", "power", true, request.getId()));
        assertEquals(request.getId(), created.getRequestId());
    }

    @Test
    void createItemWithUnknownRequestFails() {
        User owner = createUser("OwnerBad", "ownerbad@example.com");
        assertThrows(RuntimeException.class,
                () -> itemService.create(owner.getId(), itemDto("X", "y", true, 999999L)));
    }

    @Test
    void updateItemByOwner() {
        User owner = createUser("Up", "up@example.com");
        ItemDto created = itemService.create(owner.getId(), itemDto("Old", "desc", true, null));

        ItemDto update = new ItemDto();
        update.setName("New");
        update.setAvailable(false);
        ItemDto updated = itemService.update(owner.getId(), created.getId(), update);

        assertEquals("New", updated.getName());
        assertEquals("desc", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void getItemById() {
        User owner = createUser("Get", "get@example.com");
        ItemDto created = itemService.create(owner.getId(), itemDto("Thing", "desc", true, null));

        ItemWithBookingsDto dto = itemService.getById(owner.getId(), created.getId());
        assertEquals(created.getId(), dto.getId());
        assertEquals("Thing", dto.getName());
        assertNotNull(dto.getComments());
    }

    @Test
    void getItemsByOwner() {
        User owner = createUser("List", "list@example.com");
        itemService.create(owner.getId(), itemDto("A", "a", true, null));
        itemService.create(owner.getId(), itemDto("B", "b", false, null));

        List<ItemWithBookingsDto> items = itemService.getAllByOwner(owner.getId());
        assertEquals(2, items.size());
    }

    @Test
    void searchReturnsMatchingAvailableItems() {
        User owner = createUser("Search", "search@example.com");
        itemService.create(owner.getId(), itemDto("Hammer", "steel hammer", true, null));
        itemService.create(owner.getId(), itemDto("Saw", "for wood", false, null));

        List<ItemDto> found = itemService.search("hammer");
        assertEquals(1, found.size());
        assertEquals("Hammer", found.get(0).getName());

        assertTrue(itemService.search("").isEmpty());
        assertTrue(itemService.search("   ").isEmpty());
    }

    @Test
    void addCommentAfterFinishedBooking() {
        User owner = createUser("COwner", "cowner@example.com");
        User booker = createUser("CBooker", "cbooker@example.com");
        ItemDto item = itemService.create(owner.getId(), itemDto("Commented", "d", true, null));

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setItem(itemRepository.findById(item.getId()).orElseThrow());
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("great item");
        CommentDto saved = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertNotNull(saved.getId());
        assertEquals("great item", saved.getText());
        assertEquals("CBooker", saved.getAuthorName());
    }

    @Test
    void addCommentWithoutFinishedBookingFails() {
        User owner = createUser("NoBookingOwner", "nbo@example.com");
        User booker = createUser("NoBookingUser", "nbu@example.com");
        ItemDto item = itemService.create(owner.getId(), itemDto("NoBook", "d", true, null));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("too soon");

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), commentDto));
    }

    @Test
    void itemOwnerSeesLastAndNextBookings() {
        User owner = createUser("BkOwner", "bkowner@example.com");
        User booker = createUser("BkBooker", "bkbooker@example.com");
        ItemDto item = itemService.create(owner.getId(), itemDto("Booked", "d", true, null));
        Item persisted = itemRepository.findById(item.getId()).orElseThrow();

        Booking past = new Booking();
        past.setStart(LocalDateTime.now().minusDays(5));
        past.setEnd(LocalDateTime.now().minusDays(1));
        past.setItem(persisted);
        past.setBooker(booker);
        past.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(past);

        Booking future = new Booking();
        future.setStart(LocalDateTime.now().plusDays(1));
        future.setEnd(LocalDateTime.now().plusDays(2));
        future.setItem(persisted);
        future.setBooker(booker);
        future.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(future);

        ItemWithBookingsDto dto = itemService.getById(owner.getId(), item.getId());
        assertNotNull(dto.getLastBooking());
        assertNotNull(dto.getNextBooking());
    }
}
