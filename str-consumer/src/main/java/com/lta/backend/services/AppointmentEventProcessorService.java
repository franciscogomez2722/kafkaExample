package com.lta.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.lta.backend.entities.AppointmentEntity;
import com.lta.backend.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppointmentEventProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentEventProcessorService.class);

    private final AppointmentRepository appointmentRepository;
    private final EventPayloadParserService parserService;

    public AppointmentEventProcessorService(
            AppointmentRepository appointmentRepository,
            EventPayloadParserService parserService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.parserService = parserService;
    }

    public void processCreate(String rawMessage) {
        JsonNode payload = parserService.payload(parserService.parse(rawMessage));

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setAppointmentId(payload.path("appointmentId").asText());
        appointment.setPatientId(payload.path("patientId").asText());
        appointment.setDoctorName(payload.path("doctorName").asText());
        appointment.setAppointmentDateTime(payload.path("appointmentDateTime").asText());
        appointment.setStatus("CREATED");
        appointment.setUpdatedAt(Instant.now());

        appointmentRepository.save(appointment);
        LOGGER.info("Appointment created: {}", appointment.getAppointmentId());
    }

    public void processCancel(String rawMessage) {
        JsonNode payload = parserService.payload(parserService.parse(rawMessage));
        String appointmentId = payload.path("appointmentId").asText();

        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.setStatus("CANCELLED");
            appointment.setUpdatedAt(Instant.now());
            appointmentRepository.save(appointment);
        });

        LOGGER.info("Appointment cancelled: {}", appointmentId);
    }

    public void processReschedule(String rawMessage) {
        JsonNode payload = parserService.payload(parserService.parse(rawMessage));
        String appointmentId = payload.path("appointmentId").asText();

        appointmentRepository.findById(appointmentId).ifPresent(appointment -> {
            appointment.setAppointmentDateTime(payload.path("appointmentDateTime").asText(appointment.getAppointmentDateTime()));
            appointment.setDoctorName(payload.path("doctorName").asText(appointment.getDoctorName()));
            appointment.setStatus("RESCHEDULED");
            appointment.setUpdatedAt(Instant.now());
            appointmentRepository.save(appointment);
        });

        LOGGER.info("Appointment rescheduled: {}", appointmentId);
    }
}
