# smsrouter

![CI](https://github.com/guykopa/smsrouter/actions/workflows/ci.yml/badge.svg)

Real-time SMS routing engine simulating a Mobile Virtual Network Operator (MVNO) core network component.
Built with Java 21, Spring Boot 3.2, and Apache Kafka.

## Quick start

```bash
git clone https://github.com/guykopa/smsrouter.git && cd smsrouter
cp .env.example .env
docker compose -f docker/docker-compose.yml up
```

## Send an SMS

```bash
curl -s -X POST http://localhost:8080/api/sms/send \
  -H "Content-Type: application/json" \
  -d '{"from":"+33612345678","to":"+447911123456","text":"Hello"}' | jq .
```

Response:
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "status": "DELIVERED",
  "operator": "EE",
  "latencyMs": 32
}
```

Error — unknown prefix:
```bash
curl -s -X POST http://localhost:8080/api/sms/send \
  -H "Content-Type: application/json" \
  -d '{"from":"+33612345678","to":"+99999999999","text":"Hello"}' | jq .
# → 422 { "error": "Unroutable", "phoneNumber": "+99999999999" }
```

## E.164 routing

Routing uses **longest prefix matching** on the destination number:

| Prefix | Primary operator | Fallback |
|--------|-----------------|----------|
| `+44`  | EE              | Vodafone UK |
| `+33`  | Orange FR       | SFR |
| `+49`  | Deutsche Telekom | Vodafone DE |
| `+1`   | AT&T            | T-Mobile US |
| `+39`  | TIM             | Vodafone IT |

The routing table is loaded from `src/main/resources/routing-table.json` at startup.

## Kafka topics

```
POST /api/sms/send
        │
        ▼ SMS_RECEIVED ──────────────► sms.inbound
        │
        ▼ SMS_ROUTED ────────────────► sms.events
        │
        ├── SMS_DELIVERED ───────────► sms.events
        │
        └── SMS_FAILED (unroutable) ─► sms.dlq
```

## Architecture

Event-Driven Hexagonal Architecture — domain never imports Kafka or Spring.

```
REST Controller
      ↓
SendSmsUseCase          (application)
      ↓
SmsRoutingService       (domain)
      ↓ via ports
KafkaSmsPublisher       (adapter/kafka)
InMemoryOperatorRegistry (adapter/registry)
SimulatedSmsDelivery    (adapter/delivery)
```

See `Architecture.md` for the full design rationale.

## Run tests

```bash
./mvnw clean verify
```

- **Unit tests** (15): zero Spring, zero Kafka — fake adapters only
- **Integration tests** (2): embedded Kafka, full Spring context

## Health & metrics

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```