package com.lta.backend.dto;

public record PatientCommandRequest(
        String patientId,
        String fullName,
        Integer age
) {
}
