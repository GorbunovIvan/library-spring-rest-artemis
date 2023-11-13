package com.example.repository;

import com.example.model.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Set;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {

    Set<EventRecord> findAllByQueueName(String queueName);

    Set<EventRecord> findAllByDateTimeBetween(LocalDateTime timeFrom, LocalDateTime timeTo);
}
