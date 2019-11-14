package com.cdgeekcamp.redas.api.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.cdgeekcamp.redas.db.model")
@EntityScan("com.cdgeekcamp.redas.db.model")
@ComponentScan({
        "com.cdgeekcamp.redas.lib.core",
        "com.cdgeekcamp.redas.api.core"
})
public class RedasApiCoreApplication extends SpringBootServletInitializer {
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RedasApiCoreApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(RedasApiCoreApplication.class);
    }
}
