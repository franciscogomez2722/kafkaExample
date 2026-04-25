package com.lta.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.lta.backend.entities.PatientEntity;
import com.lta.backend.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PatientEventProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientEventProcessorService.class);

    private final PatientRepository patientRepository;
    private final EventPayloadParserService parserService;

    public PatientEventProcessorService(PatientRepository patientRepository, EventPayloadParserService parserService) {
        this.patientRepository = patientRepository;
        this.parserService = parserService;
    }

    public void processRegister(String rawMessage) {
        JsonNode event = parserService.parse(rawMessage);
        JsonNode payload = parserService.payload(event);

        PatientEntity patient = new PatientEntity();
        patient.setPatientId(payload.path("patientId").asText());
        patient.setFullName(payload.path("fullName").asText("Unknown"));
        patient.setAge(payload.path("age").asInt(0));
        patient.setStatus("REGISTERED");
        patient.setUpdatedAt(Instant.now());

        patientRepository.save(patient);
        LOGGER.info("Patient registered: {}", patient.getPatientId());
    }

    public void processUpdate(String rawMessage) {
        JsonNode event = parserService.parse(rawMessage);
        JsonNode payload = parserService.payload(event);
        String patientId = payload.path("patientId").asText();

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseGet(() -> {
                    PatientEntity fallback = new PatientEntity();
                    fallback.setPatientId(patientId);
                    return fallback;
                });

        patient.setFullName(payload.path("fullName").asText(patient.getFullName()));
        patient.setAge(payload.path("age").asInt(patient.getAge() == null ? 0 : patient.getAge()));
        patient.setStatus("UPDATED");
        patient.setUpdatedAt(Instant.now());

        patientRepository.save(patient);
        LOGGER.info("Patient updated: {}", patientId);
    }

    public void processDelete(String rawMessage) {
        JsonNode event = parserService.parse(rawMessage);
        JsonNode payload = parserService.payload(event);
        String patientId = payload.path("patientId").asText();

        patientRepository.findById(patientId).ifPresent(patient -> {
            patient.setStatus("DELETED");
            patient.setUpdatedAt(Instant.now());
            patientRepository.save(patient);
        });

        LOGGER.info("Patient deleted (soft-delete): {}", patientId);
    }
}
