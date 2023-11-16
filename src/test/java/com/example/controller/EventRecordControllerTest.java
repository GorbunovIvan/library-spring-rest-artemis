package com.example.controller;

import com.example.model.EventRecord;
import com.example.service.EventRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventRecordControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EventRecordService eventRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<EventRecord> eventRecordsInDB;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost/api/v1/events";
        RestAssured.port = port;

        eventRecordsInDB = eventRecordService.getAll().stream().toList();
    }

    @Test
    void testGetAll() throws JsonProcessingException {

        var jsonResponse = when()
                    .get("")
                .then()
                    .statusCode(200)
                    .extract()
                    .asPrettyString();

        Set<EventRecord> eventsFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(new HashSet<>(eventRecordsInDB), eventsFound);
    }

    @Test
    void testGetById() throws JsonProcessingException {

        for (var event : eventRecordsInDB) {

            var jsonResponse = when()
                        .get("/" + event.getId())
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            var eventFound = objectMapper.readValue(jsonResponse, EventRecord.class);
            assertEquals(event, eventFound);
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
    void testGetAllByQueue() throws JsonProcessingException {

        var queues = eventRecordsInDB.stream().map(EventRecord::getQueueName).distinct().toList();

        for (var queue : queues) {

            var jsonResponse = when()
                        .get("/queue/" + queue)
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            Set<EventRecord> eventsExpected = eventRecordService.getAllByQueue(queue);
            Set<EventRecord> eventsFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            assertEquals(eventsExpected, eventsFound);
        }
    }

    @Test
    void testGetAllByDateTimeBetween() throws JsonProcessingException {

        var sentTimes = eventRecordsInDB.stream().map(EventRecord::getSentTime).distinct().toList();

        for (var sentTime : sentTimes) {

            var timeFrom = sentTime;
            var timeTo = sentTime.plusMinutes(1L);

            var jsonResponse = when()
                        .get("/time?timeFrom=" + timeFrom + "&timeTo=" + timeTo)
                    .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

            Set<EventRecord> eventsExpected = eventRecordService.getAllByDateTimeBetween(timeFrom, timeTo);
            Set<EventRecord> eventsFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            assertEquals(eventsExpected, eventsFound);
        }
    }

    @Test
    void testGetAllByDateTimeBetween_All() throws JsonProcessingException {

        var timeFrom = LocalDateTime.of(0, 1, 1, 0, 0);
        var timeTo = LocalDateTime.of(9_999, 1, 1, 0, 0);

        var jsonResponse = when()
                    .get("/time?timeFrom=" + timeFrom + "&timeTo=" + timeTo)
                .then()
                    .statusCode(200)
                    .extract()
                    .asPrettyString();

        Set<EventRecord> eventsFound = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(new HashSet<>(eventRecordsInDB), eventsFound);
    }

    @Test
    @EnabledIfSystemProperty(named = "spring.artemis.enabled", matches = "true")
    void testAddEventToQueue() {

        for (var event : eventRecordsInDB) {
            given()
                .contentType("application/json")
                .body(event.getMessage())
            .when()
                .post("/queue-test")
            .then()
                .statusCode(200);
        }
    }
}