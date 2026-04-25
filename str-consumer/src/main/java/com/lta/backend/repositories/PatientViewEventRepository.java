package com.lta.backend.repositories;

import com.lta.backend.entities.PatientViewEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientViewEventRepository extends JpaRepository<PatientViewEventEntity, Long> {

    List<PatientViewEventEntity> findByPatientIdOrderByCreatedAtDesc(String patientId);
}
