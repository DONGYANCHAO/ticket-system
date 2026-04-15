param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("build", "up", "up-dev", "down", "logs", "logs-be", "logs-fe", "restart", "ps", "clean", "prune", "test-be", "test-fe", "build-be", "build-fe")]
    [string]$Command
)

switch ($Command) {
    "build" { docker compose build }
    "up" { docker compose up -d }
    "up-dev" { $env:SPRING_PROFILES_ACTIVE = "dev"; docker compose up -d }
    "down" { docker compose down }
    "logs" { docker compose logs -f }
    "logs-be" { docker compose logs -f backend }
    "logs-fe" { docker compose logs -f frontend }
    "restart" { docker compose restart }
    "ps" { docker compose ps }
    "clean" { docker compose down -v --rmi all }
    "prune" { docker system prune -af }
    "test-be" { Set-Location backend; mvn test; Set-Location .. }
    "test-fe" { Set-Location frontend; npm run test; Set-Location .. }
    "build-be" { docker compose build backend }
    "build-fe" { docker compose build frontend }
    default { Write-Host "Unknown command: $Command" }
}
