package com.example.service;

import com.example.model.*;
import com.example.repository.EventRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class EventRecordServiceTest {

    @Autowired
    private EventRecordService eventRecordService;

    @SpyBean
    private EventRecordRepository eventRecordRepository;

    private List<EventRecord> eventRecordsInDB;

    @BeforeEach
    void setUp() {
        eventRecordsInDB = eventRecordRepository.findAll();
    }

    @Test
    void testGetByIdExistingEvents() {
        for (var event : eventRecordsInDB) {
            assertEquals(event, eventRecordService.getById(event.getId()));
            verify(eventRecordRepository, times(1)).findById(event.getId());
        }
    }

    @Test
    void testGetByIdNonExistingBook() {
        assertNull(eventRecordService.getById(-1L));
        verify(eventRecordRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetAll() {
        assertEquals(new HashSet<>(eventRecordsInDB), eventRecordService.getAll());
        verify(eventRecordRepository, times(2)).findAll(); // 2 times because first time is in 'setUp()'
    }

    @Test
    void testGetAllByQueue() {

        var queues = eventRecordsInDB.stream().map(EventRecord::getQueueName).distinct().collect(Collectors.toList());
        queues.add("none");

        for (var queue : queues) {
            var eventsByQueue = eventRecordsInDB.stream().filter(e -> e.getQueueName().equals(queue)).collect(Collectors.toSet());
            assertEquals(eventsByQueue, eventRecordService.getAllByQueue(queue));
            verify(eventRecordRepository, times(1)).findAllByQueueName(queue);
        }
    }

    @Test
    void testGetAllByDateTimeBetween() {

        var sentTimes = eventRecordsInDB.stream().map(EventRecord::getSentTime).distinct().toList();

        for (var sentTime : sentTimes) {

            var timeFrom = sentTime;
            var timeTo = sentTime.plusMinutes(1L);

            var eventsByTime = eventRecordsInDB.stream()
                    .filter(e -> !e.getSentTime().isBefore(timeFrom)
                                 && !e.getSentTime().isAfter(timeTo))
                    .collect(Collectors.toSet());

            assertEquals(eventsByTime, eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo));
            verify(eventRecordRepository, times(1)).findAllBySentTimeBetweenOrderBySentTime(timeFrom, timeTo);
        }
    }

    @Test
    void testGetAllByDateTimeBetween_All() {

        var timeFrom = LocalDateTime.of(0, 1, 1, 0, 0);
        var timeTo = LocalDateTime.of(9_999, 1, 1, 0, 0);

        assertEquals(new HashSet<>(eventRecordsInDB), eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo));
        verify(eventRecordRepository, times(1)).findAllBySentTimeBetweenOrderBySentTime(timeFrom, timeTo);
    }

    @Test
    void testGetAllByDateTimeBetween_None() {

        var timeFrom = LocalDateTime.MIN;
        var timeTo = LocalDateTime.MIN.plusSeconds(1L);

        assertTrue(eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo).isEmpty());
        verify(eventRecordRepository, times(1)).findAllBySentTimeBetweenOrderBySentTime(timeFrom, timeTo);
    }

    @Test
    void testCreate() {

        var newBook = new Book();
        newBook.setTitle("new book");
        newBook.setYear(9_999);
        newBook.setAuthors(Set.of(new Author(null, new Person(null, "new person 1", null, null))));
        newBook.setReaders(Set.of(new Reader(null, new Person(null, "new person 2", null, null))));

        var event = new EventRecord();
        event.setQueueName("new-queue");
        event.setSentTime(LocalDateTime.now().minusMinutes(1L));
        event.setReceivedTime(LocalDateTime.now());
        event.setBook(newBook);
        event.setMessage("book in json format (not relevant here)");

        var eventCreated = eventRecordService.create(event);
        assertEquals(event, eventCreated);
        verify(eventRecordRepository, times(1)).merge(event);
    }

    @Test
    void testReceiveMessages() {

        var messages = eventRecordsInDB.stream()
                .map(event -> {
                    var headers = new HashMap<String, Object>();
                    headers.put("timestamp", event.getSentTime().toEpochSecond(ZoneOffset.UTC));
                    headers.put("jms_destination", event.getQueueName());
                    return new GenericMessage<>(event.getMessage(), headers);
                })
                .toList();

        for (var message : messages) {
            eventRecordService.receiveMessages(message);
        }

        verify(eventRecordRepository, times(messages.size())).merge(any(EventRecord.class));
    }

    @Test
    void testReceiveMessagesEmptyPayload() {

        var headers = new HashMap<String, Object>();
        headers.put("timestamp", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        headers.put("jms_destination", "queue://queue-test");

        var message = new GenericMessage<>("none", headers);

        assertThrows(RuntimeException.class, () -> eventRecordService.receiveMessages(message));
        verify(eventRecordRepository, never()).merge(any(EventRecord.class));
    }
}