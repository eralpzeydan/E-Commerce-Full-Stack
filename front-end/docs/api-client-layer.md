# API Client Layer Contract

## Goal
Keep UI isolated from networking concerns and scale all features with a consistent request pattern.

## Flow
`page -> feature screen -> feature hook -> feature api -> shared/api/request -> shared/api/http-client`

## Shared API Responsibilities
- `shared/config/env.ts`: validated environment access.
- `shared/api/http-client.ts`: central Axios instance and base config.
- `shared/api/interceptors.ts`: auth token injection + centralized `401` behavior.
- `shared/api/endpoints.ts`: endpoint constants.
- `shared/api/request.ts`: typed request helper and normalized error throw.
- `shared/api/api-error.ts`: low-level HTTP error normalization.

## Feature API Responsibilities
- Build domain requests using `apiRequest` + `API_ENDPOINTS`.
- Parse/validate DTOs at feature level (e.g. Zod schemas).
- Avoid UI side effects (no toast/navigation in API files).

## Rules
1. Components do not call Axios directly.
2. Base URL and headers are never hardcoded in features.
3. Token is never manually attached inside components.
4. Domain mapping stays in feature modules, not shared layer.
5. Shared layer normalizes errors; UI decides how to display them.
