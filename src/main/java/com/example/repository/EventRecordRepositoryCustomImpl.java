package com.example.repository;

import com.example.model.EventRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EventRecordRepositoryCustomImpl implements EventRecordRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public EventRecord merge(EventRecord eventRecord) {

        var book = eventRecord.getBook();
        book = bookRepository.merge(book);

        eventRecord.setBook(book);

        return entityManager.merge(eventRecord);
    }
}
