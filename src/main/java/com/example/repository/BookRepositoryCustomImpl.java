package com.example.repository;

import com.example.exception.EntityNotFoundException;
import com.example.model.Book;
import com.example.model.IsPerson;
import com.example.model.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final PersonRepository personRepository;

    @Override
    @Transactional
    public Book merge(Book book) {

        Book bookInDB;

        if (book.getId() != null) {
            bookInDB = entityManager.find(Book.class, book.getId());
            if (bookInDB == null) {
                log.error("Book with id={} does not exist", book.getId());
                throw new EntityNotFoundException(Book.class, "id", String.valueOf(book.getId()));
            }
        }

        mergeCollectionWithPersons(book::getAuthors, book::setAuthors, personRepository::findAuthorByPerson);
        mergeCollectionWithPersons(book::getReaders, book::setReaders, personRepository::findReaderByPerson);

        if (book.getId() != null) {
            return entityManager.merge(book);
        }

        bookInDB = entityManager.createQuery("FROM Book " +
                        "WHERE title = :title " +
                        "AND year = :year", Book.class)
                .setParameter("title", book.getTitle())
                .setParameter("year", book.getYear())
                .getResultList().stream()
                .filter(bookFound -> bookFound.getAuthorsAsString().equals(book.getAuthorsAsString()))
                .findAny()
                .orElse(null);

        if (bookInDB != null) {

            if (bookInDB.getReadersAsString().equals(book.getReadersAsString())) {
                log.warn("Book (id={}) already exists", bookInDB.getId());
                return bookInDB;
            }

            log.info("Book (id={}) already exists, updating it", bookInDB.getId());
            bookInDB.setReaders(book.getReaders());
            return entityManager.merge(bookInDB);
        }

        return entityManager.merge(book);
    }

    private <T extends IsPerson> void mergeCollectionWithPersons(Supplier<Set<T>> getter, Consumer<Set<T>> setter,
                                                                 Function<Person, Optional<T>> finderByPersonInDB) {

        var collectionPersisted = new HashSet<T>();

        for (var entity : getter.get()) {

            final var person = entity.getPerson();
            var personPersistedOpt = personRepository.findByName(person.getName());
            var personPersisted = personPersistedOpt.orElseGet(() -> personRepository.save(person));

            entity.setPerson(personPersisted);

            var entityPersistedOpt = finderByPersonInDB.apply(personPersisted);
            var entityPersisted = entityPersistedOpt.orElseGet(() -> entityManager.merge(entity));

            collectionPersisted.add(entityPersisted);
        }

        setter.accept(collectionPersisted);
    }
}
