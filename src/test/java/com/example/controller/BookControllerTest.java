package com.example.controller;

import com.example.model.Author;
import com.example.model.Book;
import com.example.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Book> booksInDB;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost/api/v1/books";
        RestAssured.port = port;

        booksInDB = bookService.getAll().stream().toList();
    }

    @Test
    void testGetAll() throws JsonProcessingException {

        var jsonResponse = when()
                    .get("")
                .then()
                    .statusCode(200)
                    .extract()
                    .asPrettyString();

        Set<Book> booksFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(new HashSet<>(booksInDB), booksFound);
    }

    @Test
    void testGetById() throws JsonProcessingException {

        for (var book : booksInDB) {

            var jsonResponse = when()
                        .get("/" + book.getId())
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            var bookFound = objectMapper.readValue(jsonResponse, Book.class);
            assertEquals(book, bookFound);
        }
    }

    @Test
    void testGetByIdNotFound() {
        when()
            .get("/" + -1L)
        .then()
            .statusCode(404);
    }

    @Test
    void testGetAllAuthorsByBook() throws JsonProcessingException {

        for (var book : booksInDB) {

            var jsonResponse = when()
                        .get("/authors-by-book/" + book.getId())
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            Set<Author> authorsExpected = bookService.getAllAuthorsByBook(book.getId());
            Set<Author> authorsFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            assertEquals(authorsExpected, authorsFound);
        }
    }

    @Test
    void testGetAllBooksReadByAuthorsOfBook() throws JsonProcessingException {

        for (var book : booksInDB) {

            var jsonResponse = when()
                        .get("/read-by-authors-of-book/" + book.getId())
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            Set<Book> booksExpected = bookService.getAllBooksReadByAuthorsOfBook(book.getId());
            Set<Book> booksFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            assertEquals(booksExpected, booksFound);
        }
    }
}