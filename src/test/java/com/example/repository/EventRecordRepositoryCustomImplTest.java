package com.example.repository;

import com.example.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
class EventRecordRepositoryCustomImplTest {

    @Autowired
    private EventRecordRepository eventRecordRepository;

    private List<EventRecord> eventRecordsInDB;

    @BeforeEach
    void setUp() {
        eventRecordsInDB = eventRecordRepository.findAll();
    }

    @Test
    void testMergeNewEventWithExistingBook() {
        for (var eventInDB : eventRecordsInDB) {
            var event = new EventRecord(eventInDB);
            var eventMerged = eventRecordRepository.merge(event);
            assertEquals(event, eventMerged);
        }
    }

    @Test
    void testMergeNewEventWithNewBook() {

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

        var eventMerged = eventRecordRepository.merge(event);

        assertNotNull(eventMerged.getId());
        assertEquals(event, eventMerged);
    }

    @Test
    void testMergeExisting() {
        var eventIdDB = eventRecordsInDB.get(0);
        assertThrows(RuntimeException.class, () -> eventRecordRepository.merge(eventIdDB));
    }
}