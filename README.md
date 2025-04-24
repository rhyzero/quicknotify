# QuickNotify

**Asynchronous notification micro‑service** – built with Spring Boot, RabbitMQ and MySQL.
Accepts REST calls (`/api/notify`), publishes each message to a queue, processes it in the background,
and stores the delivery log. Ships with Docker Compose and a k6 load‑test script so anyone can run a
full demo with a single command.

## Features

| Layer             | Tech & Purpose                                                             |
| ----------------- | -------------------------------------------------------------------------- |
| **Producer**      | Spring Boot REST API → publishes JSON messages to RabbitMQ (`/api/notify`) |
| **Queue**         | RabbitMQ 3.13 (direct exchange) – durable, supports back‑pressure          |
| **Consumer**      | Spring Boot worker – `@RabbitListener` consumes queue and writes to MySQL  |
| **Database**      | MySQL 8 – `delivery_log` table stores status, retry count, timestamp       |
| **Observability** | Spring Actuator + Prometheus endpoint, RabbitMQ Management UI (port 15672) |
| **Load test**     | k6 script (`loadtest/notify.js`) – stress test from inside Docker          |

---

## Quick start (local)

```bash
# 1 — clone
$ git clone https://github.com/rhyzero/quicknotify.git
$ cd quicknotify

# 2 — launch everything
$ docker compose up --build
```

<table>
<tr><th>Service</th><th>URL</th><th>Notes</th></tr>
<tr><td>Producer API</td><td><code>http://localhost:8080/api/notify</code></td><td>POST JSON notifications</td></tr>
<tr><td>RabbitMQ UI</td><td><code>http://localhost:15672</code></td><td>guest / guest</td></tr>
<tr><td>Consumer health</td><td><code>http://localhost:8081/actuator/health</code></td><td>Spring Actuator</td></tr>
<tr><td>MySQL CLI</td><td><code>mysql ‑h 127.0.0.1 ‑P 3306 ‑uroot ‑p1234 quicknotify</code></td><td>default creds in compose</td></tr>
</table>

**Smoke‑test**

```bash
curl -X POST http://localhost:8080/api/notify \
     -H "Content-Type: application/json" \
     -d '{"type":"EMAIL","recipient":"demo@example.com","body":"Hello!"}'
# → HTTP/1.1 202 Accepted
```

---

## Load testing with k6

```bash
# run the built‑in load‑generator profile
$ docker compose --profile loadtest up --scale k6=1
```

The console streams latency, RPS and error‑rate; adjust RPS in
`loadtest/notify.js`.

---

## Project structure

```
quicknotify/
├─ producer/            # Spring Boot REST → RabbitMQ
│  └─ Dockerfile
├─ consumer/            # RabbitMQ → MySQL worker
│  └─ Dockerfile
├─ loadtest/notify.js   # k6 performance script
├─ docker-compose.yml   # local dev / demo
└─ README.md
```

---

## Integrating into _your_ app

Add the producer container to your stack and POST to it from any language:

```js
await fetch("http://producer:8080/api/notify", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    type: "SMS",
    recipient: "+15551234567",
    body: "Your order shipped!",
  }),
});
```

Everything else (queue durability, retries, DB logging) happens asynchronously.

---

## Deploying to production (cheat‑sheet)

| Component           | Hosted option                           |
| ------------------- | --------------------------------------- |
| RabbitMQ            | CloudAMQP, Amazon MQ, Azure Service Bus |
| MySQL               | PlanetScale, AWS RDS, Neon              |
| Producer & Consumer | Fly.io, Render, ECS Fargate, Cloud Run  |

Store creds as environment variables, enable `/actuator/health` as readiness
probe, and scale the consumer replicas if queue depth grows.

---

## Key configuration flags

```properties
# producer + consumer
rabbitmq.exchange.name=notify.exchange
rabbitmq.queue.name=notify.queue
spring.rabbitmq.host=rabbitmq

# consumer only
spring.datasource.url=jdbc:mysql://mysql:3306/quicknotify
spring.datasource.username=root
spring.datasource.password=1234
```

Override in `application.properties`.

---

## License

MIT — free to use, modify, and embed.
