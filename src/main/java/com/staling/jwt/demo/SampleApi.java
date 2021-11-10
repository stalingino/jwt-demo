package com.staling.jwt.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("/api")
@RequestScope
public class SampleApi {

    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello";
    }

    @GetMapping("/sayBye")
    public String sayBye() {
        return "Bye";
    }
}
