# Stage 1: Build Angular app
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --prod

# Stage 2: Serve using nginx
FROM nginx:alpine
COPY --from=build /app/dist/file-processor-ui /usr/share/nginx/html

# Add custom nginx config to handle API proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf
