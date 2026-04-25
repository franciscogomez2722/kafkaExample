import React, { useState } from "react";
import {
  registerPatient,
  updatePatient,
  deletePatient,
  createAppointment,
  cancelAppointment,
  rescheduleAppointment
} from "./api";

const initialPatient = { patientId: "", fullName: "", age: 0 };
const initialAppointment = { appointmentId: "", patientId: "", doctorName: "", appointmentDateTime: "" };

export default function App() {
  const [patient, setPatient] = useState(initialPatient);
  const [appointment, setAppointment] = useState(initialAppointment);
  const [log, setLog] = useState([]);

  const pushLog = (label, result) => {
    setLog((prev) => [`${label}: ${JSON.stringify(result)}`, ...prev]);
  };

  const runAction = async (label, fn, payload) => {
    try {
      const result = await fn(payload);
      pushLog(label, result);
    } catch (error) {
      pushLog(label, { error: error.message });
    }
  };

  return (
    <div style={{ fontFamily: "sans-serif", margin: 24 }}>
      <h1>Sistema Doctores</h1>

      <section>
        <h2>Gestion de pacientes</h2>
        <input
          placeholder="Patient ID"
          value={patient.patientId}
          onChange={(e) => setPatient({ ...patient, patientId: e.target.value })}
        />
        <input
          placeholder="Full Name"
          value={patient.fullName}
          onChange={(e) => setPatient({ ...patient, fullName: e.target.value })}
        />
        <input
          placeholder="Age"
          type="number"
          value={patient.age}
          onChange={(e) => setPatient({ ...patient, age: Number(e.target.value) })}
        />
        <div>
          <button onClick={() => runAction("PATIENT_REGISTER", registerPatient, patient)}>Registrar</button>
          <button onClick={() => runAction("PATIENT_UPDATE", updatePatient, patient)}>Actualizar</button>
          <button onClick={() => runAction("PATIENT_DELETE", deletePatient, patient)}>Eliminar</button>
        </div>
      </section>

      <section>
        <h2>Gestion de citas</h2>
        <input
          placeholder="Appointment ID"
          value={appointment.appointmentId}
          onChange={(e) => setAppointment({ ...appointment, appointmentId: e.target.value })}
        />
        <input
          placeholder="Patient ID"
          value={appointment.patientId}
          onChange={(e) => setAppointment({ ...appointment, patientId: e.target.value })}
        />
        <input
          placeholder="Doctor Name"
          value={appointment.doctorName}
          onChange={(e) => setAppointment({ ...appointment, doctorName: e.target.value })}
        />
        <input
          placeholder="DateTime"
          value={appointment.appointmentDateTime}
          onChange={(e) => setAppointment({ ...appointment, appointmentDateTime: e.target.value })}
        />
        <div>
          <button onClick={() => runAction("APPOINTMENT_CREATE", createAppointment, appointment)}>Crear</button>
          <button onClick={() => runAction("APPOINTMENT_CANCEL", cancelAppointment, appointment)}>Cancelar</button>
          <button onClick={() => runAction("APPOINTMENT_RESCHEDULE", rescheduleAppointment, appointment)}>
            Reprogramar
          </button>
        </div>
      </section>

      <section>
        <h2>Eventos enviados</h2>
        <ul>
          {log.map((entry, idx) => (
            <li key={idx}>{entry}</li>
          ))}
        </ul>
      </section>
    </div>
  );
}
