package com.pastebin.pastebinlite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.pastebin.pastebinlite.repository")
public class PastebinliteApplication {

    public static void main(String[] args) {
        SpringApplication.run(PastebinliteApplication.class, args);
    }
}
