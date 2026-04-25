import React, { useState } from "react";
import { fetchAppointments, fetchPatient, fetchViewEvents, requestHistory, requestStatus } from "./api";

export default function App() {
  const [patientId, setPatientId] = useState("");
  const [status, setStatus] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [viewEvents, setViewEvents] = useState([]);
  const [log, setLog] = useState([]);

  const pushLog = (text) => setLog((prev) => [text, ...prev]);

  const run = async (label, action) => {
    try {
      await action();
      pushLog(`${label}: OK`);
    } catch (error) {
      pushLog(`${label}: ${error.message}`);
    }
  };

  return (
    <div style={{ fontFamily: "sans-serif", margin: 24 }}>
      <h1>Sistema de Visualizacion</h1>

      <input
        placeholder="Patient ID"
        value={patientId}
        onChange={(e) => setPatientId(e.target.value)}
      />

      <div>
        <button
          onClick={() =>
            run("REQUEST_STATUS", async () => {
              await requestStatus({ patientId, note: "status query from frontend" });
              setStatus(await fetchPatient(patientId));
            })
          }
        >
          Consultar estado
        </button>

        <button
          onClick={() =>
            run("REQUEST_HISTORY", async () => {
              await requestHistory({ patientId, note: "history query from frontend" });
              setAppointments(await fetchAppointments(patientId));
              setViewEvents(await fetchViewEvents(patientId));
            })
          }
        >
          Consultar historial
        </button>
      </div>

      <h2>Estado del paciente</h2>
      <pre>{status ? JSON.stringify(status, null, 2) : "Sin datos"}</pre>

      <h2>Citas del paciente</h2>
      <pre>{JSON.stringify(appointments, null, 2)}</pre>

      <h2>Eventos de visualizacion</h2>
      <pre>{JSON.stringify(viewEvents, null, 2)}</pre>

      <h2>Log</h2>
      <ul>
        {log.map((entry, idx) => (
          <li key={idx}>{entry}</li>
        ))}
      </ul>
    </div>
  );
}
