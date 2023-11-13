package com.example.service;

import com.example.model.EventRecord;
import com.example.repository.EventRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventRecordService {

    private final EventRecordRepository eventRecordRepository;

    public EventRecord getById(Long id) {
        return eventRecordRepository.findById(id)
                .orElse(null);
    }

    public Set<EventRecord> getAllByQueue(String queueName) {
        return eventRecordRepository.findAllByQueueName(queueName);
    }

    public Set<EventRecord> getAllByDateTimeBetween(LocalDateTime timeFrom, LocalDateTime timeTo) {
        return eventRecordRepository.findAllByDateTimeBetween(timeFrom, timeTo);
    }

    public EventRecord create(EventRecord eventRecord) {
        return eventRecordRepository.save(eventRecord);
    }
}
