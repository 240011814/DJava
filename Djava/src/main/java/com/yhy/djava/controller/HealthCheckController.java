package com.yhy.djava.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author： HouYong Yang
 * @Date： 2024/9/4 9:36
 * @Describe：
 */
@RestController
public class HealthCheckController {

    private final Environment env;

    public HealthCheckController(Environment env) {
        this.env = env;
    }

    @GetMapping(value = "ping")
    public Map check(){
        return  Map.of("version", Optional.ofNullable(  Optional.ofNullable(env.getProperty("GIT_COMMIT")).orElse("")));
    }
}
