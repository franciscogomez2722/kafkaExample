package com.lta.backend.dto;

public record KafkaDispatchResponse(
        String topic,
        int partition,
        String key,
        String status
) {
}
