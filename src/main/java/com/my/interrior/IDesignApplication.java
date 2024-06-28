package com.my.interrior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IDesignApplication {

    public static void main(String[] args){
        SpringApplication.run(IDesignApplication.class, args);
    }
}