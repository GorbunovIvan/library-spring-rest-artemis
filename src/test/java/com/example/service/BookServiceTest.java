package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Person;
import com.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @SpyBean
    private BookRepository bookRepository;

    private List<Book> booksInDB;
    
    @BeforeEach
    void setUp() {
        booksInDB = bookRepository.findAll();
    }

    @Test
    void testGetByIdExistingBooks() {
        for (var book : booksInDB) {
            assertEquals(book, bookService.getById(book.getId()));
            verify(bookRepository, times(1)).findById(book.getId());
        }
    }

    @Test
    void testGetByIdNonExistingBook() {
        assertNull(bookService.getById(-1L));
        verify(bookRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetAll() {
        assertEquals(new HashSet<>(booksInDB), bookService.getAll());
        verify(bookRepository, times(2)).findAll(); // 2 times because first time is in 'setUp()'
    }

    @Test
    void testGetAllAuthorsByBookExisting() {
        for (var book : booksInDB) {
            assertEquals(book.getAuthors(), bookService.getAllAuthorsByBook(book.getId()));
            verify(bookRepository, times(1)).findById(book.getId());
        }
    }

    @Test
    void testGetAllAuthorsByBookNonExisting() {
        assertThrows(EntityNotFoundException.class, () -> bookService.getAllAuthorsByBook(-1L));
        verify(bookRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetAllBooksReadByAuthorsOfBook() {

        int counter = 1;

        for (var book : booksInDB) {

            var authorsAsReaders = book.getAuthors().stream()
                    .map(Author::getPerson)
                    .map(Person::getReader)
                    .collect(Collectors.toSet());

            var booksByReaders = new HashSet<Book>();
            for (var reader : authorsAsReaders) {
                for (var bookInSearchByReaders : booksInDB) {
                    if (bookInSearchByReaders.getReaders().contains(reader)) {
                        booksByReaders.add(bookInSearchByReaders);
                    }
                }
            }

            assertEquals(booksByReaders, bookService.getAllBooksReadByAuthorsOfBook(book.getId()));

            verify(bookRepository, times(1)).findById(book.getId());
            verify(bookRepository, atLeast(1)).findAllByReadersIn(authorsAsReaders);
            verify(bookRepository, atMost(counter)).findAllByReadersIn(authorsAsReaders);

            counter++;
        }
    }

    @Test
    void testUpdateExisting() {
        for (var book : booksInDB) {
            assertEquals(book, bookService.update(book.getId(), book));
            verify(bookRepository, times(1)).existsById(book.getId());
            verify(bookRepository, times(1)).merge(book);
        }
    }

    @Test
    void testUpdateNonExisting() {
        var newBook = new Book();
        assertThrows(EntityNotFoundException.class, () -> bookService.update(-1L, newBook));
        verify(bookRepository, times(1)).existsById(-1L);
        verify(bookRepository, never()).merge(any(Book.class));
    }
}