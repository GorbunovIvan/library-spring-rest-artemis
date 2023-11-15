package com.example.controller;

import com.example.model.EventRecord;
import com.example.service.EventRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventRecordController {

    private final EventRecordService eventRecordService;

    private final JmsTemplate jmsTemplate;

    @GetMapping
    public ResponseEntity<Set<EventRecord>> getAll() {
        var eventRecords = eventRecordService.getAll();
        return ResponseEntity.ok(eventRecords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventRecord> getById(@PathVariable Long id) {
        var eventRecord = eventRecordService.getById(id);
        if (eventRecord == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventRecord);
    }

    @GetMapping("/queue/{queueName}")
    public ResponseEntity<Set<EventRecord>> getAllByQueue(@PathVariable String queueName) {
        var events = eventRecordService.getAllByQueue(queueName);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/time")
    // URI must be like - /time?timeFrom=2023-11-15T00:00:00&timeTo=2023-11-15T12:30:00
    public ResponseEntity<Set<EventRecord>> getAllByDateTimeBetween(@RequestParam("timeFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeFrom,
                                                                    @RequestParam("timeTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeTo) {
        var events = eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo);
        return ResponseEntity.ok(events);
    }

    /**
     * Request body should look like this:
     * {
     *   "title": "book 1",
     *   "year": 2001,
     *   "authors": [
     *     {
     *       "person": {
     *         "name": "Bob"
     *       }
     *     },
     *     {
     *       "person": {
     *         "name": "Maria"
     *       }
     *     }
     *   ],
     *   "readers": [
     *     {
     *       "person": {
     *         "name": "John"
     *       }
     *     }
     *   ]
     * }
     */
    @PostMapping("/{queue}")
    public ResponseEntity<?> addEventToQueue(@PathVariable String queue,
                                                @RequestBody String message) {
        jmsTemplate.convertAndSend(queue, message);
        log.info("Message was added to queue");
        return ResponseEntity.ok().build();
    }
}
