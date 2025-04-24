import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
  stages: [
    { duration: "30s", target: 100 }, // go to 100 req/s
    { duration: "1m", target: 100 }, // hold
    { duration: "30s", target: 300 }, // go to 300 req/s
    { duration: "1m", target: 300 }, // hold
    { duration: "10s", target: 0 }, // slow down
  ],
  thresholds: {
    http_req_failed: ["rate<0.01"], // <1 % errors
    http_req_duration: ["p(95)<200"], // 95 % < 200 ms
  },
};

let counter = 0;
function nextEmail() {
  counter += 1;
  return `user${counter}@example.com`;
}

export default () => {
  const url = "http://producer:8080/api/notify";

  const payload = JSON.stringify({
    type: "EMAIL",
    recipient: nextEmail(),
    body: "Benchmark message " + counter,
  });

  const res = http.post(url, payload, {
    headers: { "Content-Type": "application/json" },
    timeout: "2s",
  });

  check(res, { 202: (r) => r.status === 202 });
  sleep(0.01); // tiny pause so k6 doesn't loop
};
