package com.cdgeekcamp.redas.api.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan({
        "com.cdgeekcamp.redas.lib.core",
        "com.cdgeekcamp.redas.api.core"
})
public class RedasApiCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedasApiCoreApplication.class);
    }
}
