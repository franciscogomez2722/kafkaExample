package com.lta.backend.resources;

import com.lta.backend.entities.AppointmentEntity;
import com.lta.backend.entities.PatientEntity;
import com.lta.backend.entities.PatientViewEventEntity;
import com.lta.backend.repositories.AppointmentRepository;
import com.lta.backend.repositories.PatientRepository;
import com.lta.backend.repositories.PatientViewEventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/state")
public class ClinicalStateResource {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientViewEventRepository patientViewEventRepository;

    public ClinicalStateResource(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PatientViewEventRepository patientViewEventRepository
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientViewEventRepository = patientViewEventRepository;
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<PatientEntity> getPatient(@PathVariable String patientId) {
        return patientRepository.findById(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<List<AppointmentEntity>> getAppointmentsByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(appointmentRepository.findByPatientId(patientId));
    }

    @GetMapping("/patients/view-events")
    public ResponseEntity<List<PatientViewEventEntity>> getPatientViewEvents(@RequestParam String patientId) {
        return ResponseEntity.ok(patientViewEventRepository.findByPatientIdOrderByCreatedAtDesc(patientId));
    }
}
