package com.ilker.JsonMapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ilker.Model.Earthquake;

import java.sql.Timestamp;
import java.util.Date;

public class EarthquakeDeserializer extends StdDeserializer<Earthquake> {
    public EarthquakeDeserializer() {
        this(null);
    }

    public EarthquakeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Earthquake deserialize(JsonParser parser, DeserializationContext deserializer){
        Earthquake earthquake = new Earthquake();
        ObjectCodec codec = parser.getCodec();
        try {
            JsonNode node = codec.readTree(parser);
            node = node.get("properties");

            JsonNode place = node.get("place");
            String place_s = place.asText();
            String[] country = place_s.split(",");

            earthquake.setPlace(country[0].strip());
            earthquake.setCountry(country[country.length-1].strip());

            JsonNode mag = node.get("mag");
            double mag_s = mag.asDouble();
            earthquake.setMagnitude(mag_s);

            JsonNode time = node.get("time");
            long time_n = time.asLong();
            earthquake.setTimestamp(time_n);

        }catch (Exception e){
            System.out.println(e.getMessage());

        }
        return earthquake;
    }
}
