This project was generated using [Spring Initializr](https://start.spring.io/).

---

By default, the application runs on port `8080`.

Spring Boot includes an embedded Tomcat server and can automatically configure database connections based on properties.

Compared to my experience with JavaServer Faces (JSF), this setup is much simpler:
- No separate application server (e.g. GlassFish) is required
- No manual JDBC or XML configuration needed
- The application can be started directly via the `main()` method

If no controller explicitly maps the root path (`/`), Spring Boot will automatically serve a static `index.html` file (from `src/main/resources/static/` or `templates/`) as the [default entry point](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration).

---


## Deployment

As of 30.08.2025 the Habit Tracker is deployed as a Docker container on my homeserver.  
The Docker images are automatically built and pushed to [GitHub Container Registry (GHCR)](https://ghcr.io) on each push to `main`.  
See the [GitHub Action yml file](.github/workflows/docker-ghcr.yml) for the implementation.

The deployment is managed via Docker Compose:
- `postgres_db`: PostgreSQL database container
- `habit-tracker`: the Spring Boot application container

You can find a basic [docker compose file here](Docker/docker-compose.yml)

If you want to use the habit-tracker for yourself, make sure to set 
```yml
SPRING_PROFILES_ACTIVE: prod
```

See [Profiles](#profiles) below for more details

## Database & Migrations

Because the database is actively running in production (my homeserver :D), DB changes need to be clean/dont destroy my setup
For this purpose I'm using **Flyway**.

Migration scripts are stored in `src/main/resources/db/migration/`

`V1__init.sql` represents the database schema as of 30.08.2025 (baseline)

Further changes are added incrementally as V2__...sql, V3__...sql, etc.

In production, Hibernate is configured with:

`spring.jpa.hibernate.ddl-auto=validate`

This ensures Hibernate validates the schema but does not attempt to create or update tables.
Flyway is the single source of truth for schema changes.

---

## Profiles
Active profile is controlled via the environment variable: `SPRING_PROFILES_ACTIVE`

dev: for local development, as I often reset the db anyway and dont need Flyway

prod: for deployment on the homeserver (Hibernate only validates, Flyway runs the migrations)


