package com.bookstore.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        System.out.println("=== .env 파일 로드 확인 ===");
        System.out.println("DB_USERNAME: " + dotenv.get("DB_USERNAME"));
        System.out.println("DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
        System.out.println("========================");

        Map<String, Object> dotenvMap = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            dotenvMap.put(entry.getKey(), entry.getValue());
        });

        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
    }
}