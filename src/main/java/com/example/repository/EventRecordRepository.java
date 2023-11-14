package com.example.repository;

import com.example.model.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Set;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long>, EventRecordRepositoryCustom {

    Set<EventRecord> findAllByQueueName(String queueName);

    Set<EventRecord> findAllBySentTimeBetween(LocalDateTime timeFrom, LocalDateTime timeTo);
}
