const PRODUCER_BASE_URL = "http://localhost:8000/api";
const CONSUMER_BASE_URL = "http://localhost:8100/api/state";

async function post(path, payload) {
  const response = await fetch(`${PRODUCER_BASE_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }

  return response.json();
}

async function get(path) {
  const response = await fetch(`${CONSUMER_BASE_URL}${path}`);
  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }
  return response.json();
}

export const requestStatus = (payload) => post("/viewer/patients/status", payload);
export const requestHistory = (payload) => post("/viewer/patients/history", payload);
export const fetchPatient = (patientId) => get(`/patients/${patientId}`);
export const fetchAppointments = (patientId) => get(`/patients/${patientId}/appointments`);
export const fetchViewEvents = (patientId) => get(`/patients/view-events?patientId=${patientId}`);
