# Kubernetes manifests

Cluster-agnostic Kustomize manifests for the Honya fleet: 10 JVM services + frontend, plus the
stateful dependencies (PostgreSQL, RabbitMQ, Keycloak, MinIO).

```
k8s/
  base/                      # environment-independent manifests
    namespace.yaml
    config.yaml              # honya-config ConfigMap (non-secret shared env)
    infra/                   # postgres, rabbitmq, keycloak, minio + init data
    services/<svc>.yaml      # Deployment + Service per JVM service
    frontend.yaml
    kustomization.yaml       # generates honya-postgres-init / honya-keycloak-realm ConfigMaps
  overlays/
    local/
      kustomization.yaml     # base + honya-secrets from secrets.env
      secrets.env.example    # copy to secrets.env (gitignored) and fill in
```

## Deploy (local cluster: kind / minikube / Docker Desktop)

1. Make images available to the cluster. Either let CI publish to GHCR
   (`ghcr.io/nebbilies/honya-<service>:latest`) and ensure the cluster can pull them, or build
   locally and load them (e.g. `kind load docker-image honya-bookstore-<svc>:latest`, adjusting
   image names in an overlay).

2. Provide secrets:
   ```sh
   cp k8s/overlays/local/secrets.env.example k8s/overlays/local/secrets.env
   # edit secrets.env
   ```

3. Apply:
   ```sh
   kubectl apply -k k8s/overlays/local
   ```

4. Reach the fleet. The JWT issuer claim is `http://localhost:8080/realms/honya` (tokens are
   minted via the browser-facing Keycloak), while services fetch JWKs in-cluster via
   `http://keycloak:8080`. So Keycloak must be reachable at `localhost:8080` from where you log
   in. Port-forward:
   ```sh
   kubectl -n honya port-forward svc/keycloak 8080:8080
   kubectl -n honya port-forward svc/gateway  8090:8090
   kubectl -n honya port-forward svc/frontend 3000:3000
   ```

## Notes

- `base/infra/postgres-init/*.sql` and `base/infra/keycloak/honya-realm.json` are copies of the
  compose sources (`platform/postgres-init`, `keycloak/honya-realm.json`); Kustomize cannot read
  files outside its root. Keep them in sync if the originals change.
- Secrets are sourced from a gitignored `secrets.env` via `secretGenerator`; nothing sensitive is
  committed.
- Probes hit `/actuator/health` (the only exposed actuator endpoint).
- The observability stack (collector, Prometheus, Tempo, Loki, Grafana, Alertmanager) is not yet
  ported to k8s; it remains a compose overlay.
- Validated with `kubectl kustomize` rendering only — not yet applied to a live cluster.
