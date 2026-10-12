package ru.practicum.shareit.item.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (existingItem.getOwner() == null || !existingItem.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Only owner can update item");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemWithBookingsDto dto = toItemWithBookingsDto(item, comments);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            addBookings(dto, item.getId());
        }
        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwner(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemWithBookingsDto> result = new ArrayList<>();
        for (Item item : items) {
            List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            ItemWithBookingsDto dto = toItemWithBookingsDto(item, comments);
            addBookings(dto, item.getId());
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("Comment text is required");
        }
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("User has not rented this item or booking is not finished");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item, List<CommentDto> comments) {
        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(comments != null ? comments : new ArrayList<>())
                .build();
    }

    private void addBookings(ItemWithBookingsDto dto, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> allBookings = bookingRepository.findByItemId(itemId, Sort.by(Sort.Direction.ASC, "start"));
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : allBookings) {
            if (booking.getStatus() != BookingStatus.APPROVED) {
                continue;
            }
            if (booking.getEnd().isBefore(now)) {
                if (lastBooking == null || booking.getEnd().isAfter(lastBooking.getEnd())) {
                    lastBooking = booking;
                }
            } else if (booking.getStart().isAfter(now)) {
                if (nextBooking == null || booking.getStart().isBefore(nextBooking.getStart())) {
                    nextBooking = booking;
                }
            } else if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                lastBooking = booking;
            }
        }
        if (lastBooking != null) {
            dto.setLastBooking(new ItemWithBookingsDto.BookingShortDto(lastBooking.getId(), lastBooking.getBooker() != null ? lastBooking.getBooker().getId() : null));
        }
        if (nextBooking != null) {
            dto.setNextBooking(new ItemWithBookingsDto.BookingShortDto(nextBooking.getId(), nextBooking.getBooker() != null ? nextBooking.getBooker().getId() : null));
        }
    }
}
