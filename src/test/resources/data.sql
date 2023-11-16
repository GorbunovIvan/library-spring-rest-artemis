INSERT INTO books (title, publication_year) VALUES ('book 1', 1999);
INSERT INTO books (title, publication_year) VALUES ('book 2', 2001);
INSERT INTO books (title, publication_year) VALUES ('book 3', 2013);
INSERT INTO books (title, publication_year) VALUES ('book 4', 1980);

INSERT INTO people (name) VALUES ('Maria');
INSERT INTO people (name) VALUES ('John');
INSERT INTO people (name) VALUES ('Bob');
INSERT INTO people (name) VALUES ('Martin');
INSERT INTO people (name) VALUES ('Anna');
INSERT INTO people (name) VALUES ('Katy');

INSERT INTO authors (person_id) VALUES (1);
INSERT INTO authors (person_id) VALUES (2);
INSERT INTO authors (person_id) VALUES (4);
INSERT INTO authors (person_id) VALUES (3);

INSERT INTO readers (person_id) VALUES (1);
INSERT INTO readers (person_id) VALUES (2);
INSERT INTO readers (person_id) VALUES (5);
INSERT INTO readers (person_id) VALUES (6);

INSERT INTO book_authors (book_id, author_id) VALUES (1, 2);
INSERT INTO book_authors (book_id, author_id) VALUES (1, 1);
INSERT INTO book_authors (book_id, author_id) VALUES (2, 2);
INSERT INTO book_authors (book_id, author_id) VALUES (2, 1);
INSERT INTO book_authors (book_id, author_id) VALUES (3, 3);
INSERT INTO book_authors (book_id, author_id) VALUES (4, 4);

INSERT INTO book_readers (book_id, reader_id) VALUES (1, 1);
INSERT INTO book_readers (book_id, reader_id) VALUES (2, 1);
INSERT INTO book_readers (book_id, reader_id) VALUES (3, 2);
INSERT INTO book_readers (book_id, reader_id) VALUES (4, 3);
INSERT INTO book_readers (book_id, reader_id) VALUES (4, 4);

INSERT INTO event_records (queue_name, book_id, message, received_time, sent_time) VALUES ('DLQ', 1, e'{
  "title": "book 1",
  "year": 1999,
  "authors": [
    {
      "person": {
        "name": "John"
      }
    },
    {
      "person": {
        "name": "Maria"
      }
    }
  ],
  "readers": [
    {
      "person": {
        "name": "Bob"
      }
    }
  ]
}', '2023-11-14 14:32:57.157153', '2023-11-14 14:32:57.156000');
INSERT INTO event_records (queue_name, book_id, message, received_time, sent_time) VALUES ('DLQ', 2, e'{
  "title": "book 2",
  "year": 2001,
  "authors": [
    {
      "person": {
        "name": "John"
      }
    },
    {
      "person": {
        "name": "Maria"
      }
    }
  ],
  "readers": [
    {
      "person": {
        "name": "Bob"
      }
    }
  ]
}', '2023-11-14 15:01:09.619922', '2023-11-14 15:01:09.619000');
INSERT INTO event_records (queue_name, book_id, message, received_time, sent_time) VALUES ('library1', 3, e'{
  "title": "book 3",
  "year": 2013,
  "authors": [
    {
      "person": {
        "name": "Martin"
      }
    }
  ],
  "readers": [
    {
      "person": {
        "name": "John"
      }
    }
  ]
}', '2023-11-15 09:55:36.941061', '2023-11-15 09:55:36.940000');
INSERT INTO event_records (queue_name, book_id, message, received_time, sent_time) VALUES ('library1', 4, e'{
  "title": "book 4",
  "year": 1980,
  "authors": [
    {
      "person": {
        "name": "Bob"
      }
    }
  ],
  "readers": [
    {
      "person": {
        "name": "Anna"
      }
    }
  ]
}', '2023-11-15 09:57:38.178215', '2023-11-15 09:57:38.178000');
INSERT INTO event_records (queue_name, book_id, message, received_time, sent_time) VALUES ('library1', 4, e'{
  "title": "book 4",
  "year": 1980,
  "authors": [
    {
      "person": {
        "name": "Bob"
      }
    }
  ],
  "readers": [
    {
      "person": {
        "name": "Anna"
      }
    },
    {
      "person": {
        "name": "Katy"
      }
    }
  ]
}', '2023-11-15 10:30:20.873726', '2023-11-15 10:30:20.872000');