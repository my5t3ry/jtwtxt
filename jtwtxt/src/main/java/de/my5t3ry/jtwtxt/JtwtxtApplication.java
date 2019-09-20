package de.my5t3ry.jtwtxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JtwtxtApplication {

    public static void main(String[] args) {
        SpringApplication.run(JtwtxtApplication.class, args);
    }

}

