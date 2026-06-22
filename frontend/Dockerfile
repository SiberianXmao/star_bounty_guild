FROM node:22-alpine AS build

ARG VITE_API_BASE_URL=/api/v1
ARG VITE_AUTH_PROVIDER=local
ARG VITE_KEYCLOAK_URL=http://localhost:8085
ARG VITE_KEYCLOAK_REALM=bounty-guild
ARG VITE_KEYCLOAK_CLIENT_ID=bounty-frontend
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_AUTH_PROVIDER=${VITE_AUTH_PROVIDER}
ENV VITE_KEYCLOAK_URL=${VITE_KEYCLOAK_URL}
ENV VITE_KEYCLOAK_REALM=${VITE_KEYCLOAK_REALM}
ENV VITE_KEYCLOAK_CLIENT_ID=${VITE_KEYCLOAK_CLIENT_ID}

WORKDIR /app

COPY package.json package-lock.json* ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

COPY . .
RUN npm run build

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
