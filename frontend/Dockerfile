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
