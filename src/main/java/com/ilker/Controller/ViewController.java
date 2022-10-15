package com.ilker.Controller;

import com.ilker.Model.Earthquake;
import com.ilker.Service.UsgsGovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewController {

    private final UsgsGovService apiService;
    @Autowired
    public ViewController(UsgsGovService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public String getHome(Model model) throws Exception {
        List<Earthquake> earthquakes = apiService.getEarthquakes();
        model.addAttribute("earthquakes", earthquakes);
        return "home";
    }
}
