This project was generated using [Spring Initializr](https://start.spring.io/).

---

By default, the application runs on port `8080`.

Spring Boot includes an embedded Tomcat server and can automatically configure database connections based on properties.

Compared to my experience with JavaServer Faces (JSF), this setup is much simpler:
- No separate application server (e.g. GlassFish) is required
- No manual JDBC or XML configuration needed
- The application can be started directly via the `main()` method

If no controller explicitly maps the root path (`/`), Spring Boot will automatically serve a static `index.html` file (from `src/main/resources/static/` or `templates/`) as the [default entry point](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration).


