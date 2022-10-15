package com.ilker.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class EarthquakeRestController {

    @GetMapping
    public String getApiSpecs(){
        return "List of APIs:\n yes sir";
    }

}
