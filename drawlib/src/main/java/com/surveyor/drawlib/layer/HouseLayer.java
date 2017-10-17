package com.surveyor.drawlib.layer;

import com.surveyor.drawlib.HouseLoader;
import com.surveyor.drawlib.model.House;

import java.io.File;

import srs.Layer.Layer;
import srs.Layer.ILayer;
import srs.Layer.RendererArgs;
import srs.Rendering.IFeatureRenderer;
import srs.Rendering.IRenderer;
import srs.Utility.sRSException;

/**
 * Created by willc on 17-10-16.
 */

public class HouseLayer extends Layer implements ILayer {

    private House mHouse;

    public HouseLayer(String fileName) {
        super.setSource(fileName);
        this.mHouse = loadHouse(fileName);
        this.mEnvelope = this.mHouse.getExtent();
        this.mCoordinateSystem = this.mHouse.getCoordinateSystem();
    }

    public void setRenderer(IRenderer value) throws sRSException {
        if(value instanceof IFeatureRenderer) {
            this.mRenderer = (IFeatureRenderer)value;
            this.OnLayerRendererChanged(new RendererArgs(value));
        } else {
            throw new sRSException("1040");
        }
    }

    private House loadHouse(String fileName) {
        House house = HouseLoader.load(fileName);
        if (house == null) {
            mUseAble = false;
            this.setVisible(false);
        } else {
            mUseAble = true;
            this.setVisible(true);
        }
        return house;
    }


}
