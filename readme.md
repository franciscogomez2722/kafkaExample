Nota este readme fue redactado con ayuda de IA
# Sistema de microservicios con Kafka

Este proyecto implementa una arquitectura distribuida con mensajeria desacoplada usando Apache Kafka, cumpliendo con:

- 3 topicos principales
- Particiones por tipo de operacion
- Producer y Consumer en Spring Boot
- Procesamiento asincrono
- Persistencia de datos en base de datos (H2 en el consumer)
- Consumer adicional en Python (punto extra)

## 1. Topicos y particiones

- patients-management-topic
  - Particion 0: Registro de pacientes
  - Particion 1: Actualizacion de pacientes
  - Particion 2: Eliminacion de pacientes
- appointments-management-topic
  - Particion 0: Creacion de citas
  - Particion 1: Cancelacion de citas
  - Particion 2: Reprogramacion de citas
- patient-view-topic
  - Particion 0: Consulta de estado
  - Particion 1: Consulta de historial

Los topicos se crean automaticamente con docker-compose mediante el servicio kafka-init.

## 2. Levantar infraestructura Kafka

En la raiz del repositorio:

```bash
docker-compose up -d
```

Verificar:

```bash
docker ps
```

Kafdrop:

- http://localhost:19000

## 3. Ejecutar microservicios Spring Boot

En una terminal:

```bash
cd str-producer
mvnw.cmd spring-boot:run
```

En otra terminal:

```bash
cd str-consumer
mvnw.cmd spring-boot:run
```

Puertos:

- Producer: 8000
- Consumer: 8100

## 3.1 Ejecutar frontends React

Frontend doctores:

```bash
cd frontend-doctor
npm install
npm run dev
```

Frontend visualizacion:

```bash
cd frontend-visualization
npm install
npm run dev
```

Consola H2 del consumer:

- http://localhost:8100/h2-console
- JDBC URL: jdbc:h2:mem:clinicaldb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
- user: sa
- password: vacio

## 4. API producer (backend para frontends React)

Base URL: http://localhost:8000/api

### 4.1 Sistema doctores

- POST /doctor/patients/register
- POST /doctor/patients/update
- POST /doctor/patients/delete
- POST /doctor/appointments/create
- POST /doctor/appointments/cancel
- POST /doctor/appointments/reschedule

### 4.2 Sistema visualizacion

- POST /viewer/patients/status
- POST /viewer/patients/history

Ejemplos:

```bash
curl -X POST http://localhost:8000/api/doctor/patients/register \
  -H "Content-Type: application/json" \
  -d "{\"patientId\":\"P-100\",\"fullName\":\"Ana Lopez\",\"age\":29}"

curl -X POST http://localhost:8000/api/doctor/appointments/create \
  -H "Content-Type: application/json" \
  -d "{\"appointmentId\":\"A-900\",\"patientId\":\"P-100\",\"doctorName\":\"Dr. Ruiz\",\"appointmentDateTime\":\"2026-04-22T09:00:00\"}"

curl -X POST http://localhost:8000/api/viewer/patients/status \
  -H "Content-Type: application/json" \
  -d "{\"patientId\":\"P-100\",\"note\":\"Consulta desde panel\"}"
```

## 5. API de consulta de estado (consumer)

Base URL: http://localhost:8100/api/state

- GET /patients/{patientId}
- GET /patients/{patientId}/appointments
- GET /patients/view-events?patientId={patientId}

Ejemplos:

```bash
curl http://localhost:8100/api/state/patients/P-100
curl http://localhost:8100/api/state/patients/P-100/appointments
curl "http://localhost:8100/api/state/patients/view-events?patientId=P-100"
```

## 6. Consumer Python (punto extra)

Instalacion:

```bash
cd python-consumer
pip install -r requirements.txt
```

Ejecucion:

```bash
python consumer.py
```

Este consumer escucha los 3 topicos y muestra eventos recibidos.

## 7. Diagrama de infraestructura

El diagrama esta en:

- docs/infraestructura.md

Incluye:

- Frontend React (2 apps)
- Backend Producer y Consumer (Spring Boot)
- Kafka con topicos y particiones
- Base de datos
- Consumer Python
- Flujo de datos desacoplado
