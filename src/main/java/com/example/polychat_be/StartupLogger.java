package com.example.polychat_be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var environment = event.getApplicationContext().getEnvironment();
        String port = environment.getProperty("local.server.port",
                environment.getProperty("server.port", "8080"));
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String scheme = environment.getProperty("server.ssl.enabled", Boolean.class, false)
                ? "https" : "http";
        log.info("POLYCHAT_BACKEND_STARTED port={} status=SUCCESS", port);
        log.info("Swagger UI: {}://localhost:{}{}/swagger-ui.html", scheme, port, contextPath);
    }
}
