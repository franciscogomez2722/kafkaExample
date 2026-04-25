const BASE_URL = "http://localhost:8000/api";

async function post(path, payload) {
  const response = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }

  return response.json();
}

export const registerPatient = (payload) => post("/doctor/patients/register", payload);
export const updatePatient = (payload) => post("/doctor/patients/update", payload);
export const deletePatient = (payload) => post("/doctor/patients/delete", payload);
export const createAppointment = (payload) => post("/doctor/appointments/create", payload);
export const cancelAppointment = (payload) => post("/doctor/appointments/cancel", payload);
export const rescheduleAppointment = (payload) => post("/doctor/appointments/reschedule", payload);
