package com.guigu.serviceoa;

import com.guigu.common.handle.GlobalExceptionHandler;
import com.guigu.springsecurity.config.WebSecurityConfig;
import com.guigu.springsecurity.custom.CustomMd5PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

@SpringBootApplication
@ComponentScan("com.guigu")
public class ServiceOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOaApplication.class, args);
    }

}
