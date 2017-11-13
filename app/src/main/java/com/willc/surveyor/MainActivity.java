package com.willc.surveyor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.map.Map;
import com.surveyor.drawlib.mapview.MapView;
import com.willc.surveyor.interoperation.CollectInteroperator;
import com.willc.surveyor.interoperation.event.OnCollectBackListener;
import com.willc.surveyor.interoperation.event.OnCollectSaveListener;
import com.willc.surveyor.interoperation.event.OnEditBackListener;
import com.willc.surveyor.interoperation.event.OnEditSaveListener;
import com.willc.surveyor.interoperation.event.OnShearBackListener;
import com.willc.surveyor.interoperation.event.OnShearSaveListener;

import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import srs.CoordinateSystem.ProjCSType;
import srs.Geometry.Envelope;
import srs.Geometry.IGeometry;
import srs.Layer.FeatureLayer;
import srs.Layer.IFeatureLayer;
import srs.Layer.IRasterLayer;
import srs.Layer.RasterLayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayout actionNew = null;
    private LinearLayout actionEdit = null;
    private LinearLayout actionShear = null;

    private MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //inflate views

        //mGeometrys = new ArrayList<IGeometry>();
        mapView = (MapView) findViewById(R.id.map_view);
        actionNew = (LinearLayout) findViewById(R.id.action_new);
        actionNew.setOnClickListener(this);
        actionEdit = (LinearLayout) findViewById(R.id.action_edit);
        actionEdit.setOnClickListener(this);
        actionShear = (LinearLayout)findViewById(R.id.action_shear);
        actionShear.setOnClickListener(this);

        try {
            mapView.setMap(loadMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_new:
                actionNew();
                break;
            case R.id.action_edit:
                actionEdit();
                break;
            case R.id.action_shear:
                actionShear();
                break;
            default:
                break;
        }
    }

    private void actionNew() {
        CollectInteroperator.init(mapView.getMap(), true);
        CollectInteroperator.CollectEventManager
                .setOnCollectBackListener(new OnCollectBackListener() {
                    @Override
                    public boolean collectBack(EventObject event) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });
        CollectInteroperator.CollectEventManager
                .setOnCollectSaveListener(new OnCollectSaveListener() {
                    @Override
                    public boolean collectSave(EventObject event) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });

        Intent intent = new Intent(this, CollectActivity.class);
        startActivity(intent);
    }

    private void actionEdit() {
        CollectInteroperator.init(mapView.getMap(), false);
        CollectInteroperator.CollectEventManager
                .setOnEditBackListener(new OnEditBackListener() {
                    @Override
                    public boolean editBack(EventObject event) {
                        return true;
                    }
                });
        CollectInteroperator.CollectEventManager
                .setOnEditSaveListener(new OnEditSaveListener() {
                    @Override
                    public boolean editSave(EventObject event) {
                        return true;
                    }
                });
        Intent intent = new Intent(this, CollectActivity.class);
        startActivity(intent);
    }

    private void actionShear() {
        CollectInteroperator.init(mapView.getMap(), null);
        CollectInteroperator.CollectEventManager
                .setOnShearBackListener(new OnShearBackListener() {
                    @Override
                    public boolean shearBack(EventObject event) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });
        CollectInteroperator.CollectEventManager
                .setOnShearSaveListener(new OnShearSaveListener() {
                    @Override
                    public boolean shearSave(EventObject event) {
                        return true;
                    }
                });
        Intent intent = new Intent(this, ShearActivity.class);
        startActivity(intent);
    }

    /**
     * 测试用 加载测试数据
     *
     * @throws Exception
     */
    public IMap loadMap() throws Exception {
        IMap map = new Map(new Envelope(0, 0, 100D, 100D));

        // 加载影像文件数据 /TestData/IMAGE/长葛10村.tif /test/辉县市/IMAGE/01.tif  /storage/emulated/0/FlightTarget/廊坊.tif
        /*final String tifPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/collector/长葛10村.tif";
        Log.i(TAG, tifPath);

        File tifFile = new File(tifPath);
        if (tifFile.exists()) {
            IRasterLayer layer = new RasterLayer(tifPath);
            if (layer != null) {
                this.map.AddLayer(layer);
            }
        }

        // 加载shp矢量文件数据 /TestData/Data/调查村.shp /test/辉县市/TASK/村边界.shp
        final String shpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/collector/Data/调查村.shp";
        Log.d(TAG, shpPath);

        File shpFile = new File(shpPath);
        if (shpFile.exists()) {
            IFeatureLayer layer = new FeatureLayer(shpPath);
            if (layer != null) {
                this.map.AddLayer(layer);
            }
        }
        this.map.setExtent(((IFeatureLayer) map.GetLayer(1)).getFeatureClass().getGeometry(1).Extent());
        this.map.setGeoProjectType(ProjCSType.ProjCS_WGS1984_Albers_BJ);*/


        return map;
    }
}
