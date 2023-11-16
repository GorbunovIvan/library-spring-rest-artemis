package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.artemis.enabled", havingValue = "true")
public class EventRecordConsumer {

    private final EventRecordService eventRecordService;

    @JmsListener(destination = "*")
    public void onMessage(Message<String> message) {
        eventRecordService.receiveMessages(message);
    }
}
