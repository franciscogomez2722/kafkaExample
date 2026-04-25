package com.lta.backend.dto;

public record AppointmentCommandRequest(
        String appointmentId,
        String patientId,
        String doctorName,
        String appointmentDateTime
) {
}
