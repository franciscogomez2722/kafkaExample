package com.lta.backend.listeners;

import com.lta.backend.kafka.KafkaTopics;
import com.lta.backend.services.AppointmentEventProcessorService;
import com.lta.backend.services.PatientEventProcessorService;
import com.lta.backend.services.PatientViewEventProcessorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class ClinicalKafkaListener {

    private final PatientEventProcessorService patientProcessor;
    private final AppointmentEventProcessorService appointmentProcessor;
    private final PatientViewEventProcessorService patientViewProcessor;

    public ClinicalKafkaListener(
            PatientEventProcessorService patientProcessor,
            AppointmentEventProcessorService appointmentProcessor,
            PatientViewEventProcessorService patientViewProcessor
    ) {
        this.patientProcessor = patientProcessor;
        this.appointmentProcessor = appointmentProcessor;
        this.patientViewProcessor = patientViewProcessor;
    }

    @KafkaListener(
            id = "patient-register-listener",
            groupId = "patients-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.PATIENTS_TOPIC, partitions = {"0"})
    )
    public void onPatientRegister(String message) {
        patientProcessor.processRegister(message);
    }

    @KafkaListener(
            id = "patient-update-listener",
            groupId = "patients-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.PATIENTS_TOPIC, partitions = {"1"})
    )
    public void onPatientUpdate(String message) {
        patientProcessor.processUpdate(message);
    }

    @KafkaListener(
            id = "patient-delete-listener",
            groupId = "patients-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.PATIENTS_TOPIC, partitions = {"2"})
    )
    public void onPatientDelete(String message) {
        patientProcessor.processDelete(message);
    }

    @KafkaListener(
            id = "appointment-create-listener",
            groupId = "appointments-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.APPOINTMENTS_TOPIC, partitions = {"0"})
    )
    public void onAppointmentCreate(String message) {
        appointmentProcessor.processCreate(message);
    }

    @KafkaListener(
            id = "appointment-cancel-listener",
            groupId = "appointments-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.APPOINTMENTS_TOPIC, partitions = {"1"})
    )
    public void onAppointmentCancel(String message) {
        appointmentProcessor.processCancel(message);
    }

    @KafkaListener(
            id = "appointment-reschedule-listener",
            groupId = "appointments-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.APPOINTMENTS_TOPIC, partitions = {"2"})
    )
    public void onAppointmentReschedule(String message) {
        appointmentProcessor.processReschedule(message);
    }

    @KafkaListener(
            id = "patient-status-view-listener",
            groupId = "patient-view-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.PATIENT_VIEW_TOPIC, partitions = {"0"})
    )
    public void onPatientStatusRequest(String message) {
        patientViewProcessor.processStatusQuery(message);
    }

    @KafkaListener(
            id = "patient-history-view-listener",
            groupId = "patient-view-group",
            topicPartitions = @TopicPartition(topic = KafkaTopics.PATIENT_VIEW_TOPIC, partitions = {"1"})
    )
    public void onPatientHistoryRequest(String message) {
        patientViewProcessor.processHistoryQuery(message);
    }
}
