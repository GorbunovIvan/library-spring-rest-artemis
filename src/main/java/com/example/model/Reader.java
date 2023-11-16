package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "readers")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "person")
@ToString
public class Reader implements IsPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "person_id", unique = true)
    @NotNull
    @JsonIgnoreProperties(value = {"author"}, allowSetters = true)
    private Person person;

    @JsonIgnore
    public Person getOrCreatePerson() {
        return person != null ? person : new Person();
    }

    public String getName() {
        return getOrCreatePerson().getName();
    }

    public void setName(String name) {
        getOrCreatePerson().setName(name);
    }
}
