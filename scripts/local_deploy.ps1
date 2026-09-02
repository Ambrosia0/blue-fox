docker build -t blog/keycloak-kafka ./keycloak/

./gradlew bootBuildImage

docker compose up -d