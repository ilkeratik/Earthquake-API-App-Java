package com.ilker.Service;

import com.ilker.Model.Earthquake;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsgsGovServices {

    public List<Earthquake> getEarthquakes(){
        return new ArrayList<Earthquake>();
    }
}
