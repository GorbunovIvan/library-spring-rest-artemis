package com.example.repository;

import com.example.exception.EntityNotFoundException;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryCustomImplTest {

    @Autowired
    private BookRepository bookRepository;

    private List<Book> booksInDB;

    @BeforeEach
    void setUp() {
        booksInDB = bookRepository.findAll();
    }

    @Test
    void testMergeExisting() {

        for (var bookInDB : booksInDB) {

            var book = new Book(bookInDB);
            book.setId(bookInDB.getId());

            book.setYear(book.getYear() + 1);
            book.setTitle(book.getTitle() + " (updated)");
            book.getReaders().add(new Reader(null, new Person(null, "new reader", null, null)));
            book.getAuthors().add(new Author(null, new Person(null, "new author", null, null)));

            var bookMerged = bookRepository.merge(book);

            assertEquals(book.getId(), bookMerged.getId());
            assertEquals(book, bookMerged);
            assertEquals(book.getReadersAsString(), bookMerged.getReadersAsString());

            var authorsOfBookWithId = getAuthorsWithIdByBook(book);
            var authorsOfBookMergedWithId = getAuthorsWithIdByBook(bookMerged);
            assertEquals(authorsOfBookWithId, authorsOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(authorsOfBookWithId), getIdsFromCollectionOfPersons(authorsOfBookMergedWithId));

            var readersOfBookWithId = getReadersWithIdByBook(book);
            var readersOfBookMergedWithId = getReadersWithIdByBook(bookMerged);
            assertEquals(readersOfBookWithId, readersOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(readersOfBookWithId), getIdsFromCollectionOfPersons(readersOfBookMergedWithId));
        }
    }

    @Test
    void testMergeExistingButWithoutIds() {

        for (var bookInDB : booksInDB) {

            var book = new Book(bookInDB);

            var bookMerged = bookRepository.merge(book);

            assertEquals(bookInDB.getId(), bookMerged.getId());
            assertEquals(book, bookMerged);
            assertEquals(book.getReadersAsString(), bookMerged.getReadersAsString());

            var authorsOfBookWithId = getAuthorsWithIdByBook(book);
            var authorsOfBookMergedWithId = getAuthorsWithIdByBook(bookMerged);
            assertEquals(authorsOfBookWithId, authorsOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(authorsOfBookWithId), getIdsFromCollectionOfPersons(authorsOfBookMergedWithId));

            var readersOfBookWithId = getReadersWithIdByBook(book);
            var readersOfBookMergedWithId = getReadersWithIdByBook(bookMerged);
            assertEquals(readersOfBookWithId, readersOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(readersOfBookWithId), getIdsFromCollectionOfPersons(readersOfBookMergedWithId));
        }
    }

    @Test
    void testMergeExistingButWithoutIdsAndWithNewReaders() {

        for (var bookInDB : booksInDB) {

            var book = new Book(bookInDB);
            book.getReaders().add(new Reader(null, new Person(null, "new reader", null, null)));

            var bookMerged = bookRepository.merge(book);

            assertEquals(bookInDB.getId(), bookMerged.getId());
            assertEquals(book, bookMerged);
            assertEquals(book.getReadersAsString(), bookMerged.getReadersAsString());

            var authorsOfBookWithId = getAuthorsWithIdByBook(book);
            var authorsOfBookMergedWithId = getAuthorsWithIdByBook(bookMerged);
            assertEquals(authorsOfBookWithId, authorsOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(authorsOfBookWithId), getIdsFromCollectionOfPersons(authorsOfBookMergedWithId));

            var readersOfBookWithId = getReadersWithIdByBook(book);
            var readersOfBookMergedWithId = getReadersWithIdByBook(bookMerged);
            assertEquals(readersOfBookWithId, readersOfBookMergedWithId);
            assertEquals(getIdsFromCollectionOfPersons(readersOfBookWithId), getIdsFromCollectionOfPersons(readersOfBookMergedWithId));
        }
    }

    @Test
    void testMergeWithIdThatDoesNotExistAndGetError() {
        var newBook = new Book();
        newBook.setId(-1L);
        assertThrows(EntityNotFoundException.class, () -> bookRepository.merge(newBook));
    }

    @Test
    void testMergeNewWithExistingAuthorsAndReaders() {

        var personsInDB = booksInDB.stream()
                .map(b -> {
                    var list = new ArrayList<IsPerson>();
                    list.addAll(b.getAuthors());
                    list.addAll(b.getReaders());
                    return list;
                })
                .flatMap(List::stream)
                .map(IsPerson::getPerson)
                .distinct()
                .toList();

        var authors = Set.of(
                new Author(null, personsInDB.get(0)),
                new Author(null, personsInDB.get(1))
        );

        var readers = Set.of(
                new Reader(null, personsInDB.get(2))
        );

        var book = new Book();
        book.setTitle("new book");
        book.setYear(9_999);
        book.setAuthors(authors);
        book.setReaders(readers);

        var bookMerged = bookRepository.merge(book);

        assertNotNull(bookMerged.getId());
        assertEquals(book, bookMerged);
        assertEquals(book.getReadersAsString(), bookMerged.getReadersAsString());

        assertEquals(book.getAuthors(), bookMerged.getAuthors());
        assertEquals(getIdsFromCollectionOfPersons(book.getAuthors()), getIdsFromCollectionOfPersons(bookMerged.getAuthors()));

        assertEquals(book.getReaders(), bookMerged.getReaders());
        assertEquals(getIdsFromCollectionOfPersons(book.getReaders()), getIdsFromCollectionOfPersons(bookMerged.getReaders()));
    }

    @Test
    void testMergeNewWithNewAuthorsAndReaders() {

        var authors = Set.of(
                new Author(null, new Person(null, "new person 1", null, null))
        );

        var readers = Set.of(
                new Reader(null, new Person(null, "new person 2", null, null)),
                new Reader(null, new Person(null, "new person 3", null, null))
        );

        var book = new Book();
        book.setTitle("new book");
        book.setYear(9_999);
        book.setAuthors(authors);
        book.setReaders(readers);

        var bookMerged = bookRepository.merge(book);

        assertNotNull(bookMerged.getId());
        assertEquals(book, bookMerged);
        assertEquals(book.getReadersAsString(), bookMerged.getReadersAsString());
    }

    private List<Author> getAuthorsWithIdByBook(Book book) {
        return book.getAuthors().stream()
                .filter(a -> a.getId() != null)
                .sorted(Comparator.comparing(Author::getId))
                .toList();
    }

    private List<Reader> getReadersWithIdByBook(Book book) {
        return book.getReaders().stream()
                .filter(a -> a.getId() != null)
                .sorted(Comparator.comparing(Reader::getId))
                .toList();
    }

    private List<Long> getIdsFromCollectionOfPersons(Collection<? extends IsPerson> collection) {
        return collection.stream()
                .map(IsPerson::getId)
                .sorted()
                .toList();
    }
}