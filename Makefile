# Flyway Dockerイメージのバージョン
FLYWAY_VERSION=10.15.0

# db-migrateコマンド
db-migrate:
	docker run --rm -v $(PWD)/db/migrations:/flyway/sql \
		--network=host \
		flyway/flyway:$(FLYWAY_VERSION) migrate \
			-url=jdbc:mysql://localhost:3306/$(DB_DATABASE) \
			-user=$(DB_USER) \
			-password=$(DB_PASSWORD)

# db-resetコマンド
db-reset:
	docker compose exec db mysql -u$(DB_USER) -p${DB_PASSWORD} -e "drop database if exists ${DB_DATABASE};"
	docker compose exec db mysql -u$(DB_USER) -p${DB_PASSWORD} -e "create database ${DB_DATABASE} default character set utf8mb4;"
	$(MAKE) db-migrate