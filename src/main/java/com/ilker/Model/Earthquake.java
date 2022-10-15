package com.ilker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Earthquake {
    private String country;
    private String place;
    private double magnitude;
    private Date date;
    private String timestamp;
}
