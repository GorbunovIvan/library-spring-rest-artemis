package com.example.repository;

import com.example.model.Author;
import com.example.model.Person;
import com.example.model.Reader;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);

    @Query("FROM Author WHERE person = :person")
    @Nonnull
    Optional<Author> findAuthorByPerson(@Param("person") @Nullable Person person);

    @Query("FROM Reader WHERE person = :person")
    @Nonnull
    Optional<Reader> findReaderByPerson(@Param("person") @Nullable Person person);
}
