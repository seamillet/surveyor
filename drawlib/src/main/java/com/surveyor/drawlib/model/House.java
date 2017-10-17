package com.surveyor.drawlib.model;

import java.util.List;

import srs.CoordinateSystem.ICoordinateSystem;
import srs.Geometry.IEnvelope;

/**
 * Created by stg on 17/9/15.
 */

public class House {
    /**
     * 户主
     */
    private String owner;

    /**
     * 房屋地址
     */
    private String address;

    /**
     * 幢号
     */
    private String number;

    /**
     * 楼层
     */
    private List<Floor> floors;

    public IEnvelope getExtent() {
        return null;
    }

    public ICoordinateSystem getCoordinateSystem() {
        return null;
    }

    //比例尺

    //extent

}
