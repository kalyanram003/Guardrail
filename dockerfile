version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: grid07_postgres
    environment:
      POSTGRES_DB: grid07db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5431:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    container_name: grid07_redis
    ports:
      - "6379:6379"
    command: redis-server --save 60 1 --loglevel warning

volumes:
  postgres_data: