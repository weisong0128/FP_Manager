package com.fiberhome.fp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class FpApplication {


    public static void main(String[] args) {
        SpringApplication.run(FpApplication.class, args);
    }

}
