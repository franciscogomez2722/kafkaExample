package com.lta.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lta.backend.dto.AppointmentCommandRequest;
import com.lta.backend.dto.KafkaDispatchResponse;
import com.lta.backend.dto.PatientCommandRequest;
import com.lta.backend.dto.PatientViewCommandRequest;
import com.lta.backend.kafka.KafkaTopics;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ClinicalEventProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalEventProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ClinicalEventProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public KafkaDispatchResponse publishPatientRegister(PatientCommandRequest request) {
        return publish(
                KafkaTopics.PATIENTS_TOPIC,
                0,
                request.patientId(),
                "PATIENT_REGISTER",
                request
        );
    }

    public KafkaDispatchResponse publishPatientUpdate(PatientCommandRequest request) {
        return publish(
                KafkaTopics.PATIENTS_TOPIC,
                1,
                request.patientId(),
                "PATIENT_UPDATE",
                request
        );
    }

    public KafkaDispatchResponse publishPatientDelete(PatientCommandRequest request) {
        return publish(
                KafkaTopics.PATIENTS_TOPIC,
                2,
                request.patientId(),
                "PATIENT_DELETE",
                request
        );
    }

    public KafkaDispatchResponse publishAppointmentCreate(AppointmentCommandRequest request) {
        return publish(
                KafkaTopics.APPOINTMENTS_TOPIC,
                0,
                request.appointmentId(),
                "APPOINTMENT_CREATE",
                request
        );
    }

    public KafkaDispatchResponse publishAppointmentCancel(AppointmentCommandRequest request) {
        return publish(
                KafkaTopics.APPOINTMENTS_TOPIC,
                1,
                request.appointmentId(),
                "APPOINTMENT_CANCEL",
                request
        );
    }

    public KafkaDispatchResponse publishAppointmentReschedule(AppointmentCommandRequest request) {
        return publish(
                KafkaTopics.APPOINTMENTS_TOPIC,
                2,
                request.appointmentId(),
                "APPOINTMENT_RESCHEDULE",
                request
        );
    }

    public KafkaDispatchResponse publishPatientStatusRequest(PatientViewCommandRequest request) {
        return publish(
                KafkaTopics.PATIENT_VIEW_TOPIC,
                0,
                request.patientId(),
                "PATIENT_STATUS_QUERY",
                request
        );
    }

    public KafkaDispatchResponse publishPatientHistoryRequest(PatientViewCommandRequest request) {
        return publish(
                KafkaTopics.PATIENT_VIEW_TOPIC,
                1,
                request.patientId(),
                "PATIENT_HISTORY_QUERY",
                request
        );
    }

    private KafkaDispatchResponse publish(String topic, int partition, String key, String eventType, Object payload) {
        String payloadJson = buildEventPayload(eventType, payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, key, payloadJson);

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex != null) {
                LOGGER.error("Error publishing event {} to topic {} partition {}", eventType, topic, partition, ex);
                return;
            }

            if (result != null && result.getRecordMetadata() != null) {
                LOGGER.info(
                        "Published {} to topic {} partition {} offset {}",
                        eventType,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });

        return new KafkaDispatchResponse(topic, partition, key, "DISPATCHED");
    }

    private String buildEventPayload(String eventType, Object payload) {
        Map<String, Object> eventMap = new LinkedHashMap<>();
        eventMap.put("eventType", eventType);
        eventMap.put("eventTimestamp", Instant.now().toString());
        eventMap.put("payload", payload);

        try {
            return objectMapper.writeValueAsString(eventMap);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize event payload", ex);
        }
    }
}
