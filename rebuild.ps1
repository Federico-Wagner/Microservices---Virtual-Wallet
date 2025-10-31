Write-Host "🧹 Deteniendo y limpiando contenedores antiguos..."
docker compose down -v

Write-Host "🧼 Eliminando imágenes viejas de microservicios..."
docker rmi $(docker images -q auth:latest users:latest accounts:latest transactions:latest gateway:latest) -ErrorAction SilentlyContinue

Write-Host "⚙️ Reconstruyendo imágenes sin usar caché..."
docker compose --env-file test.env -f docker-compose.yml build --no-cache

Write-Host "🚀 Levantando todo el ecosistema..."
docker compose --env-file test.env -f docker-compose.yml up -d

Write-Host "✅ Listo! Microservicios y gateway actualizados y corriendo."
docker ps