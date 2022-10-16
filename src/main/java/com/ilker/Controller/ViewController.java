package com.ilker.Controller;

import com.ilker.Model.Earthquake;
import com.ilker.Service.UsgsGovService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@Controller
public class ViewController {

    private final UsgsGovService apiService;
    private List<List<String>> country_list = new ArrayList<List<String>>();
    public static Map<String, List<String>> country_map = new TreeMap<>();
    @Autowired
    public ViewController(UsgsGovService apiService) {
        this.apiService = apiService;
    }

    @PostConstruct
    public void getCountries(){
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:static/country_lat.csv");
        }catch (Exception e){
            System.out.println(e.getStackTrace().toString());
        }
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] values = null;
            csvReader.readNext(); //skip column names
            while ((values = csvReader.readNext()) != null) {
                country_list.add(Arrays.asList(values));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        for(final List<String> row : country_list){
            country_map.put(row.get(3), row.subList(0,3)); //set country name as the key
        }
    }
    @GetMapping
    public String getHome(
            Model model,
            @RequestParam(required = false) Optional<Integer> days,
            @RequestParam(required = false) Optional<String> country,
            @RequestParam(required = false) Optional<String> includeregionsaround) {

        model.addAttribute("countries", country_map.keySet());
        country.ifPresent(c -> model.addAttribute("selected_country", c));
        days.ifPresent(i -> model.addAttribute("selected_days", i));

        List<Earthquake> earthquakes;
        if (!country.isPresent() && !days.isPresent()){
            return "home";
        }

        else if (!country.isPresent() || !days.isPresent()){
            model.addAttribute("error_occured", true);
            model.addAttribute("error_msg", "Please choose a valid country and day count value.");
            return "home";
        }

        try {
            earthquakes = apiService.getEarthquakes(days, country, includeregionsaround);
        }catch (Exception e){
            model.addAttribute("error_occured", true);
            model.addAttribute("error_msg", e.getMessage());
            return "home";
        }

        model.addAttribute("error_occured", false);
        model.addAttribute("earthquakes", earthquakes);

        return "home";
    }

}
