package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "people")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "name")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    @NotEmpty
    private String name;

    @OneToOne(mappedBy = "person")
    @JsonIgnoreProperties({"person"})
    private Author author;

    @OneToOne(mappedBy = "person")
    @JsonIgnoreProperties({"person"})
    private Reader reader;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                (author != null ? ", author=" + author.getId() : "") +
                (reader != null ? ", reader=" + reader.getId() : "") +
                '}';
    }
}
