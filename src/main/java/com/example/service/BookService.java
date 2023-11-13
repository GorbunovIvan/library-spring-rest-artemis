package com.example.service;

import com.example.exceptions.EntityNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Person;
import com.example.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElse(null);
    }

    public Set<Book> getAll() {
        return new HashSet<>(bookRepository.findAll());
    }

    public Set<Author> getAllAuthorsByBook(Long id) {

        var book = getById(id);
        if (book == null) {
            throw new EntityNotFoundException(Book.class, "id", String.valueOf(id));
        }

        return book.getAuthors();
    }

    public Set<Book> getAllBooksReadByAuthorsOfBook(Long bookId) {

        var authors = getAllAuthorsByBook(bookId);

        var authorsAsReaders = authors.stream()
                .map(Author::getPerson)
                .map(Person::getReader)
                .collect(Collectors.toSet());

        return bookRepository.findAllByReadersIn(authorsAsReaders);
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book book) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException(Book.class, "id", String.valueOf(id));
        }
        book.setId(id);
        return bookRepository.save(book);
    }
}
