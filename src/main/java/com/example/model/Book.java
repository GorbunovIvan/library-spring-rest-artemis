package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "title")
    @NotNull
    @NotEmpty
    private String title;

    @Column(name = "year")
    @Digits(integer = 4, fraction = 0)
    private Integer year;

    @ManyToMany(targetEntity = Author.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"})
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Fetch(FetchMode.JOIN)
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(targetEntity = Reader.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(
            name = "book_readers",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "reader_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "reader_id"})
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Fetch(FetchMode.JOIN)
    private Set<Reader> readers = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Set<EventRecord> eventRecords = new HashSet<>();

    @EqualsAndHashCode.Include
    @ToString.Include
    public String getAuthorsAsString() {
        return getAuthors().stream()
                .map(Author::getName)
                .collect(Collectors.joining(", "));
    }

    @EqualsAndHashCode.Include
    @ToString.Include
    @JsonIgnore
    public String getReadersAsString() {
        return getReaders().stream()
                .map(Reader::getName)
                .collect(Collectors.joining(", "));
    }
}
