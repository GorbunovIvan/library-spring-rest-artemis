package com.example.repository;

import com.example.model.Book;
import com.example.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    Set<Book> findAllByReadersIn(Set<Reader> readers);
}
