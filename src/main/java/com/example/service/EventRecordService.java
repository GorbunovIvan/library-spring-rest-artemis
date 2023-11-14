package com.example.service;

import com.example.model.Book;
import com.example.model.EventRecord;
import com.example.repository.EventRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecordService {

    private final EventRecordRepository eventRecordRepository;
    private final ObjectMapper objectMapper;

    public EventRecord getById(Long id) {
        return eventRecordRepository.findById(id)
                .orElse(null);
    }

    public Set<EventRecord> getAllByQueue(String queueName) {
        return eventRecordRepository.findAllByQueueName(queueName);
    }

    public Set<EventRecord> getAllByDateTimeBetween(LocalDateTime timeFrom, LocalDateTime timeTo) {
        return eventRecordRepository.findAllBySentTimeBetween(timeFrom, timeTo);
    }

    public EventRecord create(EventRecord eventRecord) {
        return eventRecordRepository.merge(eventRecord);
    }

    @JmsListener(destination = "*")
    public void receiveMessages(Message<String> message) {

        var headers = message.getHeaders();

        var eventRecord = new EventRecord();
        eventRecord.setMessage(message.getPayload());
        eventRecord.setSentTime(new Timestamp(Objects.requireNonNullElse(headers.getTimestamp(), 0L)).toLocalDateTime());
        eventRecord.setReceivedTime(LocalDateTime.now());
        eventRecord.setQueueName(getQueueNameFromJMSMessageHeaders(headers));

        var messageString = message.getPayload();

        Book book;

        try {
            book = objectMapper.readValue(messageString, Book.class);
        } catch (JsonProcessingException e) {
            log.error("Book from message can not be parsed: {}", messageString);
            throw new RuntimeException(e);
        }

        eventRecord.setBook(book);

        create(eventRecord);
    }

    private String getQueueNameFromJMSMessageHeaders(MessageHeaders headers) {
        var destination = headers.get("jms_destination");
        if (destination == null) {
            return "";
        }
        var destinationName = destination.toString();
        if (destinationName.startsWith("queue://")) {
            destinationName = destinationName.substring("queue://".length());
        }
        return destinationName;
    }
}
