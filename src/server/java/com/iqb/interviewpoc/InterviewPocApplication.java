package com.iqb.interviewpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InterviewPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewPocApplication.class, args);
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello World!";
    }
}
