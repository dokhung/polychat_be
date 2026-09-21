package com.example.polychat_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PolychatBeApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PolychatBeApplication.class);
        application.setLogStartupInfo(false);
        application.run(args);
    }

}
