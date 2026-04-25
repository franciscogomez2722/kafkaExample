package com.lta.backend.repositories;

import com.lta.backend.entities.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {

    List<AppointmentEntity> findByPatientId(String patientId);
}
