FROM node:20-alpine AS deps

WORKDIR /app

COPY package.json package-lock.json ./
COPY frontend/package.json ./frontend/package.json
RUN npm ci

FROM node:20-alpine AS dev

WORKDIR /app

COPY --from=deps /app ./

WORKDIR /app/frontend

EXPOSE 3000

CMD ["npm", "run", "dev", "--", "--hostname", "0.0.0.0", "--port", "3000"]

FROM node:20-alpine AS build

WORKDIR /app

COPY --from=deps /app ./
COPY frontend ./frontend

ARG NEXT_PUBLIC_API_URL
ARG NEXT_PUBLIC_API_MOCKING=false
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_MOCKING=$NEXT_PUBLIC_API_MOCKING

WORKDIR /app/frontend
RUN npm run build

FROM node:20-alpine AS prod

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

COPY --from=build /app/frontend/.next/standalone ./
COPY --from=build /app/frontend/.next/static ./frontend/.next/static
COPY --from=build /app/frontend/public ./frontend/public

WORKDIR /app/frontend

EXPOSE 3000

CMD ["node", "server.js"]
