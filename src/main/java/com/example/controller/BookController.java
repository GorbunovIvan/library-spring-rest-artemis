package com.example.controller;

import com.example.model.Author;
import com.example.model.Book;
import com.example.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Set<Book>> getAll() {
        var books = bookService.getAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable long id) {
        var book = bookService.getById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/authors-by-book/{bookId}")
    public ResponseEntity<Set<Author>> getAllAuthorsByBook(@PathVariable Long bookId) {
        var authors = bookService.getAllAuthorsByBook(bookId);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/read-by-authors-of-book/{bookId}")
    public ResponseEntity<Set<Book>> getAllBooksReadByAuthorsOfBook(@PathVariable Long bookId) {
        var books = bookService.getAllBooksReadByAuthorsOfBook(bookId);
        return ResponseEntity.ok(books);
    }
}
