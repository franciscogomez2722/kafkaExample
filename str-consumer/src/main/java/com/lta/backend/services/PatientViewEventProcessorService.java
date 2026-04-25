package com.lta.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.lta.backend.entities.AppointmentEntity;
import com.lta.backend.entities.PatientEntity;
import com.lta.backend.entities.PatientViewEventEntity;
import com.lta.backend.repositories.AppointmentRepository;
import com.lta.backend.repositories.PatientRepository;
import com.lta.backend.repositories.PatientViewEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PatientViewEventProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientViewEventProcessorService.class);

    private final EventPayloadParserService parserService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientViewEventRepository patientViewEventRepository;

    public PatientViewEventProcessorService(
            EventPayloadParserService parserService,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PatientViewEventRepository patientViewEventRepository
    ) {
        this.parserService = parserService;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientViewEventRepository = patientViewEventRepository;
    }

    public void processStatusQuery(String rawMessage) {
        JsonNode payload = parserService.payload(parserService.parse(rawMessage));
        String patientId = payload.path("patientId").asText();

        String details = patientRepository.findById(patientId)
                .map(patient -> "PatientStatus{patientId=%s,status=%s,updatedAt=%s}"
                        .formatted(patient.getPatientId(), patient.getStatus(), patient.getUpdatedAt()))
                .orElse("PatientStatus{patientId=%s,status=NOT_FOUND}".formatted(patientId));

        saveViewEvent(patientId, "PATIENT_STATUS_QUERY", details);
        LOGGER.info("Patient status queried: {}", patientId);
    }

    public void processHistoryQuery(String rawMessage) {
        JsonNode payload = parserService.payload(parserService.parse(rawMessage));
        String patientId = payload.path("patientId").asText();

        List<AppointmentEntity> appointments = appointmentRepository.findByPatientId(patientId);
        String details = "PatientHistory{patientId=%s,appointments=%d}".formatted(patientId, appointments.size());

        saveViewEvent(patientId, "PATIENT_HISTORY_QUERY", details);
        LOGGER.info("Patient history queried: {}", patientId);
    }

    private void saveViewEvent(String patientId, String eventType, String details) {
        PatientViewEventEntity event = new PatientViewEventEntity();
        event.setPatientId(patientId);
        event.setEventType(eventType);
        event.setDetails(details);
        event.setCreatedAt(Instant.now());
        patientViewEventRepository.save(event);
    }
}
