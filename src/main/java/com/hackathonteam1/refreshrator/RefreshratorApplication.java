package com.hackathonteam1.refreshrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class RefreshratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefreshratorApplication.class, args);
    }

}
