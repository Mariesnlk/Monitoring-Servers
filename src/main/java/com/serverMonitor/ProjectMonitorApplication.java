package com.serverMonitor;

import com.serverMonitor.security.encryption.AES;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
//@EnableScheduling
public class ProjectMonitorApplication {

    static {
        ApiContextInitializer.init();
    }

    @SneakyThrows
    public static void main(String[] args) {
//        SpringApplication.run(ProjectMonitorApplication.class, args);
        System.out.println(AES.decrypt("RyUjQ+6N3WcGCTXx9RfWdgXhbu5oK9jBRcvI26Z20ts=", "fifi!fifi!!"));
    }
}
