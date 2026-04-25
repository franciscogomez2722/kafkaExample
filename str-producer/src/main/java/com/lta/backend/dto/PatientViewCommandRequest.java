package com.lta.backend.dto;

public record PatientViewCommandRequest(
        String patientId,
        String note
) {
}
