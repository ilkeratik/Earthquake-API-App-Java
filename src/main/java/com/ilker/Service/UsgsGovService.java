package com.ilker.Service;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ilker.JsonMapping.EarthquakeDeserializer;
import com.ilker.Model.Earthquake;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class UsgsGovService {
    private final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/";
    public List<Earthquake> getEarthquakes() throws Exception {
        String REQ_URL = BASE_URL+"query?format=geojson&starttime=2022-01-03&endtime=2022-08-10&minlatitude=39&minlongitude=32&maxlatitude=57&maxlongitude=54";

        final HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(REQ_URL))
                .timeout(Duration.of(10, SECONDS))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        List<Earthquake> list_eq = parseResponseBody(response);
        return list_eq;
    }

    public static List<Earthquake> parseResponseBody(HttpResponse<String> response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("EarthquakeDeserializer");
        module.addDeserializer(Earthquake.class, new EarthquakeDeserializer());
        mapper.registerModule(module);

        List<Earthquake> list_eq = new ArrayList<>();
        JsonNode features_node = mapper.readTree(response.body()).get("features");
        for (final JsonNode objNode : features_node) {
            list_eq.add(mapper.treeToValue(objNode, Earthquake.class));
        }

        return list_eq;
    }
}
