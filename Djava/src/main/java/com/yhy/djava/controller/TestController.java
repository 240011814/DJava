package com.yhy.djava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author： HouYong Yang
 * @Date： 2024/9/3 16:17
 * @Describe：
 */
@RestController
public class TestController {

    @GetMapping(value = "test")
    public String test(){
        return "test";
    }
}
