package com.willc.surveyor;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.map.Map;
import com.surveyor.drawlib.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import srs.CoordinateSystem.ProjCSType;
import srs.Geometry.Envelope;
import srs.Geometry.IGeometry;
import srs.Layer.FeatureLayer;
import srs.Layer.IFeatureLayer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final CharSequence[] items = {"Point", "Line", "Polygon"};
    private List<IGeometry> mGeometrys = null;

    private LinearLayout actionNew = null;
    private LinearLayout actionEdit = null;
    private LinearLayout actionShear = null;

    private MapView mapView = null;
    private IMap map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  //inflate views

        mGeometrys = new ArrayList<IGeometry>();
        mapView = (MapView) findViewById(R.id.map_view);
        actionNew = (LinearLayout) findViewById(R.id.action_new);
        actionEdit = (LinearLayout) findViewById(R.id.action_edit);
        actionShear = (LinearLayout)findViewById(R.id.action_shear);

        try {
            mapView.setMap(loadMap());
            //mapView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试用 加载测试数据
     *
     * @throws Exception
     */
    public IMap loadMap() throws Exception {
        if (this.map == null) {
            this.map = new Map(new Envelope(0, 0, 100D, 100D));

            // 加载影像文件数据 /TestData/IMAGE/长葛10村.tif /test/辉县市/IMAGE/01.tif  /storage/emulated/0/FlightTarget/廊坊.tif
            /*final String tifPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/collector/长葛10村.tif";
            Log.i(TAG, tifPath);

            File tifFile = new File(tifPath);
            if (tifFile.exists()) {
                IRasterLayer layer = new RasterLayer(tifPath);
                if (layer != null) {
                    this.map.AddLayer(layer);
                }
            }*/

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

            this.map.setExtent(((IFeatureLayer) map.GetLayer(0)).getFeatureClass().getGeometry(1).Extent());
            this.map.setGeoProjectType(ProjCSType.ProjCS_WGS1984_Albers_BJ);
        }
        return this.map;
    }


}
