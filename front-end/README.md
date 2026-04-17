# E-Commerce Frontend

Production-style frontend foundation built with Next.js App Router, TypeScript, Tailwind CSS, TanStack Query, Axios, React Hook Form, and Zod.

## Architecture
- `src/app`: route entry points and layout composition.
- `src/features`: domain modules (`auth`, `products`, `cart`, `checkout`, `orders`, `admin`).
- `src/shared`: cross-cutting infrastructure and reusable UI primitives.

Architecture contract: `docs/frontend-architecture-contract.md`
API contract: `docs/api-client-layer.md`

## Scripts
```bash
npm run dev
npm run lint
npm run test
npm run test:run
npm run build
```

## Environment
Create `.env.local`:
```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```
