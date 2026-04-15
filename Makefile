.PHONY: help build up down logs clean restart ps prune

help:
	@echo "Ticket System Docker Compose Commands:"
	@echo "  make build      - Build all images"
	@echo "  make up         - Start all services in background"
	@echo "  make up-dev     - Start all services with dev profile"
	@echo "  make down       - Stop and remove all containers"
	@echo "  make logs       - View all service logs"
	@echo "  make logs-be    - View backend logs"
	@echo "  make logs-fe    - View frontend logs"
	@echo "  make restart    - Restart all services"
	@echo "  make ps         - List all running containers"
	@echo "  make clean      - Clean all containers, images and volumes"
	@echo "  make prune      - Prune docker system"
	@echo "  make test-be    - Run backend tests"
	@echo "  make test-fe    - Run frontend tests"
	@echo "  make build-be   - Build backend only"
	@echo "  make build-fe   - Build frontend only"

build:
	docker compose build

build-be:
	docker compose build backend

build-fe:
	docker compose build frontend

up:
	docker compose up -d

up-dev:
	SPRING_PROFILES_ACTIVE=dev docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

logs-be:
	docker compose logs -f backend

logs-fe:
	docker compose logs -f frontend

restart:
	docker compose restart

ps:
	docker compose ps

clean:
	docker compose down -v --rmi all

prune:
	docker system prune -af

test-be:
	cd backend && mvn test

test-fe:
	cd frontend && npm run test
