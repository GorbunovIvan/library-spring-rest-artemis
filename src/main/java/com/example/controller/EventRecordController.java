package com.example.controller;

import com.example.model.EventRecord;
import com.example.service.EventRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Set<EventRecord>> getAllByDateTimeBetween(@RequestParam("timeFrom") LocalDateTime timeFrom,
                                                                    @RequestParam("timeTo") LocalDateTime timeTo) {
        var events = eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{queue}")
    public ResponseEntity<?> addEventToQueue(@PathVariable String queue,
                                                @RequestBody String message) {
        jmsTemplate.convertAndSend(queue, message);
        log.info("Message was added to queue");
        return ResponseEntity.ok().build();
    }
}
