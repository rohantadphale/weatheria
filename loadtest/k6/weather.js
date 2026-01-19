import http from "k6/http";
import { check, sleep } from "k6";

// Base URL for the API under test; override via BASE_URL env var.
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
// Sample cities to exercise typical cache and geocoding paths.
const CITIES = [
  "London",
  "Paris",
  "New York",
  "Tokyo",
  "Mumbai",
  "Sydney",
  "Toronto",
  "Berlin",
];

export const options = {
  // Ramp up to simulate light -> moderate traffic, then ramp down.
  stages: [
    { duration: "30s", target: 5 },
    { duration: "1m", target: 20 },
    { duration: "30s", target: 0 },
  ],
  // Basic SLO-style thresholds for failures and latency.
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<800"],
  },
};

export default function () {
  // Pick a random city each iteration to vary requests.
  const city = CITIES[Math.floor(Math.random() * CITIES.length)];
  const url = `${BASE_URL}/api/weather?city=${encodeURIComponent(city)}`;
  const res = http.get(url);

  // Accept 200 (found) and 404 (not found) as valid outcomes.
  check(res, {
    "status is 200 or 404": (r) => r.status === 200 || r.status === 404,
  });

  // Small think time between iterations.
  sleep(1);
}
