package com.surveyor.drawlib;

import com.surveyor.drawlib.model.House;

import java.io.File;

/**
 * Created by stg on 17/10/14.
 */

public class HouseLoader {
    public static House load(String fileName) {
        if ((new File(fileName)).exists() && fileName.substring(fileName.indexOf(".") + 1).toUpperCase().equalsIgnoreCase("sur")) {

        }
        return null;
    }
}
