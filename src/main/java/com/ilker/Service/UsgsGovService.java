package com.ilker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ilker.Controller.ViewController;
import com.ilker.JsonMapping.EarthquakeDeserializer;
import com.ilker.Model.Earthquake;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class UsgsGovService{
    private static final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/";
    private ObjectMapper mapper = new ObjectMapper();
    private SimpleModule module = new SimpleModule("EarthquakeDeserializer");

    public UsgsGovService(){
        module.addDeserializer(Earthquake.class, new EarthquakeDeserializer());
        mapper.registerModule(module);
    }

    public List<Earthquake> getEarthquakes(Optional<Integer> days,
                                           Optional<String> country,
                                           Optional<String> includeregionsaround) throws Exception {
        String REQ_URL = buildUrl(days, country);
        System.out.println(REQ_URL);
        final HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(REQ_URL))
                .timeout(Duration.of(10, SECONDS))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if(response.statusCode() != 200){
            throw new Exception("There is an error:\n" + response.body());
        }
        int count = mapper.readTree(response.body()).at("/metadata/count").asInt();
        if(count == 0){
            throw new Exception("No Earthquakes were recorded in "+country.get()+" in the past "+days.get()+" days.");
        }

        System.out.println(count);
        List<Earthquake> list_eq = parseResponseBody(response);

        if(!includeregionsaround.isPresent()){
            List<Earthquake> list_filtered = new ArrayList<>();
            for(final Earthquake eq:list_eq){
                if(eq.getCountry().toUpperCase().contains(country.get().toUpperCase())){
                    list_filtered.add(eq);
                }
            }
            return list_filtered;
        }

        return list_eq;
    }

    public static String buildUrl(Optional<Integer> days, Optional<String> country){
        String str = BASE_URL+"query?format=geojson&orderby=time";

        if(days.isPresent()){
            str += processDateInterval(days.get());
        }
        if(country.isPresent()){
            str += processCountryLocation(country.get());
        }
        return str;
    }

    public static String processDateInterval(Integer days){
        final long DAY_IN_MS = 1000 * 60 * 60 * 24;
        final Date date = new Date(System.currentTimeMillis() - (days * DAY_IN_MS));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final String strDate = "&starttime="+formatter.format(date);;
        return strDate;
    }

    public static String processCountryLocation(String country){
        final List<String> details = ViewController.country_map.get(country);
        final String latitude = details.get(1); //latitude
        final String longitude = details.get(2); //longitude
        final String max_radius_km = "10"; //TO-DO
        final String loc_query = "&latitude="+latitude+
                "&longitude="+longitude+
                "&maxradius="+max_radius_km;
        return loc_query;
    }
    public List<Earthquake> parseResponseBody(HttpResponse<String> response) throws Exception {
        List<Earthquake> list_eq = new ArrayList<>();
        JsonNode features_node = mapper.readTree(response.body()).get("features");
        for (final JsonNode objNode : features_node) {
            list_eq.add(mapper.treeToValue(objNode, Earthquake.class));
        }

        return list_eq;
    }
}
