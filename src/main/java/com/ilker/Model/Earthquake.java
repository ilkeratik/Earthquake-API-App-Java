package com.ilker.Model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ilker.JsonMapping.EarthquakeDeserializer;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
//@JsonDeserialize(using = EarthquakeDeserializer.class)
public class Earthquake {
    private String country;
    private String place;
    private double magnitude;
    private Date date;
    private long timestamp;
}
