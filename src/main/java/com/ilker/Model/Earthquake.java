package com.ilker.Model;

import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    private String date_str;
    private String time_str;
    public void setTimestamp(Long timestamp){
        date = new Date(new Timestamp(timestamp).getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm"); //hour
        time_str = formatter.format(date);
        formatter = new SimpleDateFormat("yyyy/dd/MM"); //day
        date_str = formatter.format(date);
    }
}
