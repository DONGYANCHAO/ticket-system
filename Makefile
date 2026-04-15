.PHONY: help build up down restart logs ps clean dev test prod

COMPOSE := docker-compose
COMPOSE_FILE := docker-compose.yml

help:
	@echo "Ticket System - Docker Compose Commands"
	@echo ""
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@echo "  build        Build all containers"
	@echo "  up           Start all services"
	@echo "  down         Stop and remove all containers"
	@echo "  restart      Restart all services"
	@echo "  logs         View logs (all services)"
	@echo "  logs-backend View backend logs"
	@echo "  logs-frontend View frontend logs"
	@echo "  ps           List running containers"
	@echo "  clean        Remove containers, volumes and images"
	@echo "  dev          Start development environment"
	@echo "  test         Start test environment"
	@echo "  prod         Start production environment"
	@echo "  rebuild      Force rebuild and restart"
	@echo "  shell-backend Open shell in backend container"
	@echo "  shell-mysql  Open MySQL shell"
	@echo "  shell-redis  Open Redis CLI"
	@echo ""

build:
	$(COMPOSE) -f $(COMPOSE_FILE) build

up:
	$(COMPOSE) -f $(COMPOSE_FILE) up -d

down:
	$(COMPOSE) -f $(COMPOSE_FILE) down

restart:
	$(COMPOSE) -f $(COMPOSE_FILE) restart

logs:
	$(COMPOSE) -f $(COMPOSE_FILE) logs -f

logs-backend:
	$(COMPOSE) -f $(COMPOSE_FILE) logs -f backend

logs-frontend:
	$(COMPOSE) -f $(COMPOSE_FILE) logs -f frontend

ps:
	$(COMPOSE) -f $(COMPOSE_FILE) ps

clean:
	$(COMPOSE) -f $(COMPOSE_FILE) down -v --rmi local
	docker system prune -f

dev:
	$(COMPOSE) -f $(COMPOSE_FILE) --env-file .env.development up -d --build

test:
	$(COMPOSE) -f $(COMPOSE_FILE) --env-file .env.test up -d --build

prod:
	$(COMPOSE) -f $(COMPOSE_FILE) --env-file .env.production up -d --build

rebuild:
	$(COMPOSE) -f $(COMPOSE_FILE) build --no-cache
	$(COMPOSE) -f $(COMPOSE_FILE) up -d

shell-backend:
	$(COMPOSE) -f $(COMPOSE_FILE) exec backend sh

shell-mysql:
	$(COMPOSE) -f $(COMPOSE_FILE) exec mysql mysql -uroot -p$(MYSQL_ROOT_PASSWORD)

shell-redis:
	$(COMPOSE) -f $(COMPOSE_FILE) exec redis redis-cli

status:
	$(COMPOSE) -f $(COMPOSE_FILE) ps
	@echo ""
	@echo "Health Status:"
	@docker inspect --format='{{.Name}}: {{.State.Health.Status}}' ticket-mysql ticket-redis ticket-backend ticket-frontend 2>/dev/null || true
