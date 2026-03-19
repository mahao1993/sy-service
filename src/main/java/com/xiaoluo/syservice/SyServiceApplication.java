package com.xiaoluo.syservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyServiceApplication.class, args);
    }
}
