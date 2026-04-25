import json
from confluent_kafka import Consumer, KafkaException

TOPICS = [
    "patients-management-topic",
    "appointments-management-topic",
    "patient-view-topic",
]


def deserialize(value):
    if value is None:
        return None
    try:
        return json.loads(value.decode("utf-8"))
    except Exception:
        return {"raw": value.decode("utf-8", errors="replace")}


def main():
    consumer = Consumer(
        {
            "bootstrap.servers": "localhost:9092",
            "group.id": "python-analytics-group",
            "auto.offset.reset": "earliest",
            "enable.auto.commit": True,
        }
    )
    consumer.subscribe(TOPICS)

    print("Python consumer running. Listening to topics:", ", ".join(TOPICS))

    try:
        while True:
            message = consumer.poll(1.0)
            if message is None:
                continue

            if message.error():
                raise KafkaException(message.error())

            payload = deserialize(message.value())
            event_type = payload.get("eventType", "UNKNOWN") if isinstance(payload, dict) else "UNKNOWN"
            print(
                f"topic={message.topic()} partition={message.partition()} offset={message.offset()} "
                f"eventType={event_type} payload={payload}"
            )
    except KeyboardInterrupt:
        print("Stopping Python consumer...")
    finally:
        consumer.close()


if __name__ == "__main__":
    main()
