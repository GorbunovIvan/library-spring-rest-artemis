package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_records")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class EventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "queue_name")
    @NotNull
    @NotEmpty
    private String queueName;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "received_time")
    private LocalDateTime receivedTime;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "message", length = 9_999)
    private String message;
}
