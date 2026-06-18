# platform/

Operational assets for the microservices migration (compose overrides, later k8s
manifests + observability config).

## Run the stack with the API Gateway in front

The base `docker-compose.yml` runs the monolith directly. This override adds the
gateway and repoints the frontend at it, so traffic flows:

```
frontend (:3000) -> gateway (:8090) -> backend monolith (:8081)
```

From the repo root:

```bash
docker compose -f docker-compose.yml -f platform/docker-compose.microservices.yml up --build
```

What the override changes:

- adds `gateway` (Spring Cloud Gateway) on `:8090`, routing `/api/**` and the
  swagger paths to `backend:8081`.
- rebuilds `frontend` with `NEXT_PUBLIC_API_URL=http://localhost:8090/api` (the
  `NEXT_PUBLIC_*` value is baked at build time, so a rebuild is required) and sets
  the server-side `API_INTERNAL_URL=http://gateway:8090/api`.

The monolith is unchanged behind the gateway. To go back to talking to the
monolith directly, run the base compose file alone.

## Notes

- CORS is handled at the gateway edge (origin `http://localhost:3000`). The
  monolith's own CORS still exists but only fires on browser-origin requests,
  which it no longer receives once the gateway fronts it.
- Edge JWT validation at the gateway is deferred for now (the monolith remains the
  auth authority and still validates every request).
