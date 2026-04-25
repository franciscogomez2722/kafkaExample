package com.lta.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EventPayloadParserService {

    private final ObjectMapper objectMapper;

    public EventPayloadParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode parse(String rawMessage) {
        try {
            return objectMapper.readTree(rawMessage);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JSON payload", ex);
        }
    }

    public JsonNode payload(JsonNode event) {
        return event.path("payload");
    }

    public String eventType(JsonNode event) {
        return event.path("eventType").asText("UNKNOWN");
    }
}
