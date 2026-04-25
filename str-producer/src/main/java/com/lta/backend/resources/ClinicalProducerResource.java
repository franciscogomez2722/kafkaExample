package com.lta.backend.resources;

import com.lta.backend.dto.AppointmentCommandRequest;
import com.lta.backend.dto.KafkaDispatchResponse;
import com.lta.backend.dto.PatientCommandRequest;
import com.lta.backend.dto.PatientViewCommandRequest;
import com.lta.backend.services.ClinicalEventProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClinicalProducerResource {

    private final ClinicalEventProducerService clinicalEventProducerService;

    public ClinicalProducerResource(ClinicalEventProducerService clinicalEventProducerService) {
        this.clinicalEventProducerService = clinicalEventProducerService;
    }

    @PostMapping("/doctor/patients/register")
    public ResponseEntity<KafkaDispatchResponse> registerPatient(@RequestBody PatientCommandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalEventProducerService.publishPatientRegister(request));
    }

    @PostMapping("/doctor/patients/update")
    public ResponseEntity<KafkaDispatchResponse> updatePatient(@RequestBody PatientCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishPatientUpdate(request));
    }

    @PostMapping("/doctor/patients/delete")
    public ResponseEntity<KafkaDispatchResponse> deletePatient(@RequestBody PatientCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishPatientDelete(request));
    }

    @PostMapping("/doctor/appointments/create")
    public ResponseEntity<KafkaDispatchResponse> createAppointment(@RequestBody AppointmentCommandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalEventProducerService.publishAppointmentCreate(request));
    }

    @PostMapping("/doctor/appointments/cancel")
    public ResponseEntity<KafkaDispatchResponse> cancelAppointment(@RequestBody AppointmentCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishAppointmentCancel(request));
    }

    @PostMapping("/doctor/appointments/reschedule")
    public ResponseEntity<KafkaDispatchResponse> rescheduleAppointment(@RequestBody AppointmentCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishAppointmentReschedule(request));
    }

    @PostMapping("/viewer/patients/status")
    public ResponseEntity<KafkaDispatchResponse> requestPatientStatus(@RequestBody PatientViewCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishPatientStatusRequest(request));
    }

    @PostMapping("/viewer/patients/history")
    public ResponseEntity<KafkaDispatchResponse> requestPatientHistory(@RequestBody PatientViewCommandRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(clinicalEventProducerService.publishPatientHistoryRequest(request));
    }
}
