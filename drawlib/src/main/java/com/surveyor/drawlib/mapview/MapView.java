package com.surveyor.drawlib.mapview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.surveyor.drawlib.elements.container.IElementContainer;
import com.surveyor.drawlib.map.ActiveView;
import com.surveyor.drawlib.map.IActiveView;
import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.map.Map;
import com.surveyor.drawlib.map.event.ContentChangedListener;
import com.surveyor.drawlib.tools.ITool;
import com.surveyor.drawlib.tools.ZoomPan;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import srs.Geometry.Envelope;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.Layer.IGPSContainer;
import srs.Layer.TileLayer;
import srs.Layer.wmts.ImageDownLoader;

/**
 * Created by stg on 17/10/14.
 */
public class MapView extends BaseControl implements ContentChangedListener {
    private static final String TAG = MapView.class.getSimpleName();

    private IActiveView mActiveView;
    private ITool mZoomPan = null;
    private ITool mDrawTool = null;

    DisplayMetrics displayMetrics;
    private int densityDpi;
    private int mwidthold = 0;
    private int mheightold = 0;

    private ProgressBar mProgressBar;
    private Handler myHandler;

    public boolean misFirst = true;

    public Bitmap mBitScreen = null;
    private Paint mPaint = new Paint();
    private boolean IsDrawTrack = false;

    IEnvelope menv = null;
    public int MODE = 0;

    /*int mFid;
    String mfieldID;
    IFeatureLayer fLayer = null;
    public String IndexOfCheck = "";*/

    public MapView(Context context) {
        super(context);
        this.init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    @SuppressLint({"HandlerLeak"})
    private void init() {
        this.mActiveView = new ActiveView();
        this.mZoomPan = new ZoomPan();
        this.mZoomPan.setBuddyControl(this);

        this.displayMetrics = this.getResources().getDisplayMetrics();
        this.densityDpi = this.displayMetrics.densityDpi;

        this.mProgressBar = new ProgressBar(this.getContext());
        LayoutParams params = new LayoutParams(-2, -2);
        params.addRule(13);
        this.addView(this.mProgressBar, params);

        this.myHandler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    super.handleMessage(msg);
                    switch(msg.arg1) {
                        case 1:
                        case 2:
                            MapView.this.DrawTrack();
                            ImageDownLoader.cancelTask();
                            ImageDownLoader.StopThread();
                            MapView.this.mProgressBar.setVisibility(View.GONE);
                            Log.i(TAG, "handleMessage, msg=" + msg.arg1);
                            Log.i(TAG, "MapView刷新完成, 进度条消失");
                            break;
                        case 3:
                            Log.i(TAG, "handleMessage, msg=" + msg.arg1+" and drawTrackLayer()");
                            MapView.this.DrawTrackLayer();
                            break;
                        case 4:
                            String e = msg.getData().getString("KEY");
                            Log.i("LEVEL-ROW-COLUMN", "MSG = 4:MapControl.myHandler 绘制瓦片：" + e);
                            if(e != null) {
                                Log.i("LEVEL-ROW-COLUMN", "MapControl.myHandler 绘制瓦片：" + e);
                                MapView.this.DrawTileImage(e, MapView.this.myHandler);
                                MapView.this.DrawTrackLayer();
                            }

                            if(TileLayer.IsDrawnEnd()) {
                                Log.i(TAG, "handleMessage, msg=4 and drawTrackLayer()");
                                MapView.this.DrawTrackLayer();
                                Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + "绘制瓦片已经绘制完成，绘制下一层map.drawLayer");
                                ++Map.INDEXDRAWLAYER;
                                MapView.this.mActiveView.FocusMap().drawLayer(MapView.this.myHandler);
                            }
                            break;
                        case 6:
                            Log.i(TAG, "handleMessage, msg=6 and drawTrackLayer()");
                            MapView.this.DrawTrackLayer();
                            Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + "已经绘制完成，绘制下一层map.drawLayer");
                            ++Map.INDEXDRAWLAYER;
                            MapView.this.mActiveView.FocusMap().drawLayer(MapView.this.myHandler);
                        case 0:
                        case 5:
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "MapView myHandler handleMessage()" + ex.getMessage());
                }
            }
        };
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, String.format("onFinishInflate(). the width is %s, the height is %s", this.getWidth(), this.getHeight()));

        Envelope en = new Envelope(-180.0D, -90.0D, 90.0D, 180.0D);
        this.mActiveView.FocusMap(new Map(en));
        this.mActiveView.getContentChanged().addListener(this);
        System.gc();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;
        Log.i(TAG, String.format("onLayout(). the width is %s, the height is %s", width, height));

        if(this.misFirst && changed && this.mwidthold != width && this.mheightold != height) {
            this.mwidthold = width;
            this.mheightold = height;
            Log.i("MapView.onLayout", "width:" + this.mwidthold + ";height:" + this.mheightold + ";");
            IMap mMap = this.mActiveView.FocusMap();
            mMap.setDeviceExtent(new Envelope(0.0D, 0.0D, (double)this.mwidthold, (double)this.mheightold));
            this.misFirst = false;
            this.Refresh();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, String.format("onMeasure(). the widthMeasureSpec is %s, the heightMeasureSpec is %s", widthMeasureSpec, heightMeasureSpec));
    }

    protected void onDraw(Canvas canvas) {
        /*try {
            super.onDraw(canvas);
        } catch (Exception var4) {
            Log.i("RECYCLE", "MyIamgeView -> onDraw() Canvas:trying to use a recycled bitmap");
        }*/
        super.onDraw(canvas);
        Log.i(TAG, "MapView.onDraw(). 重画屏幕");
        Log.i(TAG, "MapView current scale is " + mActiveView.FocusMap().getScale());
        /*try {
            if(this.mZoomPan != null && ((ZoomPan)this.mZoomPan).isMAGNIFY()) {
                super.onDraw(canvas);
                ((ZoomPan)this.mZoomPan).drawMagnify(canvas);
                return;
            }

            if(this.IsDrawTrack) {
                if(this.mBitScreen == null) {
                    this.mBitScreen = this.mActiveView.FocusMap().ExportMap(false).copy(Bitmap.Config.RGB_565, true);
                    Log.i("mBitScreen", "" + this.mBitScreen);
                }

                canvas.drawBitmap(this.mBitScreen, 0.0F, 0.0F, this.mPaint);
                this.IsDrawTrack = false;
                if(this.mBitScreen != null && !this.mBitScreen.isRecycled()) {
                    this.mBitScreen = null;
                    Log.i("mBitScreen", "" + this.mBitScreen);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "MapView.onDraw() Exception. The msg is " + ex.getMessage());
        }*/
    }

    public boolean onTouch(View v, MotionEvent event) {
        IEnvelope deviceExtent = mActiveView.FocusMap().getDeviceExtent();
        IEnvelope extent = mActiveView.FocusMap().getExtent();

        Log.i(TAG, String.format("MapView.onTouch() --> device extent=[xmin=%s,ymin=%s,xmax=%s,ymax=%s]", deviceExtent.XMin(), deviceExtent.YMin(), deviceExtent.XMax(), deviceExtent.YMax()));
        Log.i(TAG, String.format("MapView.onTouch() --> extent=[xmin=%s,ymin=%s,xmax=%s,ymax=%s]",extent.XMin(), extent.YMin(), extent.XMax(), extent.YMax()));
        if(this.mDrawTool != null && this.mDrawTool.getEnable().booleanValue()) {
            Log.i(TAG, "MapView.onTouch() --> mDrawTool.onTouch()");
            boolean end = this.mDrawTool.onTouch(v, event);
            Log.i(TAG, "MapView.onTouch() --> mDrawTool.onTouch() -- result is " + end);

            if(end) {
                return end;
            }
        }

        if(this.mZoomPan != null) {
            Log.i(TAG, "MapView.onTouch(). mZoomPan!=null. --> mZoomPan.onTouch()");
            return this.mZoomPan.onTouch(v, event);
        } else {
            return true;
        }
    }

    public void Refresh() {
        try {
            /*this.setDrawingCacheEnabled(true);

            try {
                if(!this.misFirst && this.mActiveView.FocusMap().getHasWMTSBUTTOM()) {
                    this.mBitScreen = this.getDrawingCache().copy(Bitmap.Config.RGB_565, false);
                    Log.i("RECYCLE", "通过getDrawingCache获取了控件的截图，并copy后赋值给mBitScreen" + this.mBitScreen);
                }

                this.setDrawingCacheEnabled(false);
            } catch (Exception var3) {
                Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 490" + var3.getMessage());
            }*/
            Log.i(TAG, "Refresh(). MapView全刷新");
            this.mActiveView.FocusMap().Refresh(this.myHandler, null);
//            this.mActiveView.FocusMap().Refresh(null);
//            this.DrawTrack();
            /*if(this.mBitScreen != null && !this.mBitScreen.isRecycled() && this.mActiveView.FocusMap().getHasWMTSBUTTOM()) {
                this.mActiveView.FocusMap().Refresh(this.myHandler, this.mBitScreen);
            } else {
                this.mActiveView.FocusMap().Refresh(this.myHandler, (Bitmap)null);
            }*/
        } catch (InterruptedException var4) {
            Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 507 InterruptedException" + var4.getMessage());
            var4.printStackTrace();
        } catch (Exception var5) {
            Log.e("LEVEL-ROW-COLUMN", "MapControl.Refresh at 510" + var5.getMessage());
            Message message = new Message();
            message.arg1 = 2;
            this.myHandler.sendMessage(message);
        }
    }

    public void PartialRefresh() {
        try {
            Log.i(TAG, "PartialRefresh(). MapView部分刷新");
            this.mActiveView.FocusMap().PartialRefresh();
            this.DrawTrack();
        } catch (Exception ex) {
            Log.e(TAG, "MapView.PartialRefresh() Exception. The msg is " + ex.getMessage());
        }
    }

    public void DrawTileImage(String key, Handler handler) {
        TileLayer.DrawImageFromURL(key, handler);
    }

    public void DrawTrackLayer() {
        BitmapDrawable bd = new BitmapDrawable(this.getResources(), this.mActiveView.FocusMap().ExportMapLayer());
        this.setBackgroundDrawable(bd);
        Log.i(TAG, "图层：" + String.valueOf(Map.INDEXDRAWLAYER) + " 绘制过程中,将部分图层缓存绘于屏幕 MapView.drawTrackLayer");
    }

    public void DrawTrack() {
        Bitmap bmp = this.mActiveView.FocusMap().ExportMap(false).copy(Bitmap.Config.RGB_565, true);
        BitmapDrawable bd = new BitmapDrawable(this.getResources(), bmp);
        this.setBackgroundDrawable(bd);
        bmp = null;
        Log.i(TAG, "地图刷新完成,将画布底图绘于屏幕 MapView.drawTrack");
    }

    public void DrawTrack(Bitmap bit) {
        if(bit != null && bit != this.mActiveView.FocusMap().ExportMap(false)) {
            BitmapDrawable bd = new BitmapDrawable(this.getResources(), bit);
            this.setBackgroundDrawable(bd);
            bit = null;
        }
    }

    public ITool getDrawTool() {
        return this.mDrawTool != null?this.mDrawTool:null;
    }

    public void setDrawTool(ITool value) {
        if(value != null) {
            this.mDrawTool = value;
            this.mDrawTool.setBuddyControl(this);
            this.mDrawTool.setEnable(Boolean.valueOf(true));
        } else {
            this.mDrawTool = null;
        }
    }

    public void ClearDrawTool() {
        this.mDrawTool = null;
    }

    public IMap getMap() {
        return this.mActiveView.FocusMap();
    }

    public void setMap(IMap value) {
        if(value != null && this.mActiveView.FocusMap() != value) {
            try {
                this.mActiveView.FocusMap().dispose();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            this.mActiveView.FocusMap(value);
            this.misFirst = true;

            try {
                ((ZoomPan)this.mZoomPan).dispose();
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            this.mZoomPan = new ZoomPan();
            this.mZoomPan.setBuddyControl(this);
        }
    }

    public Bitmap getBitmap() {
        return this.mActiveView.FocusMap().ExportMap(false);
    }

    public IElementContainer getElementContainer() {
        return this.mActiveView.FocusMap().getElementContainer();
    }

    public IGPSContainer getGPSContainer() {
        return this.mActiveView.FocusMap().getGPSContainer();
    }

    public IActiveView getActiveView() {
        return this.mActiveView;
    }

    public void setActiveView(IActiveView value) {
        if(!this.mActiveView.equals(value)) {
            this.mActiveView = value;
            this.mActiveView.getContentChanged().addListener(this);
        }
    }

    public IPoint ToWorldPoint(PointF point) {
        return this.mActiveView.FocusMap().ToMapPoint(point);
    }

    public PointF FromWorldPoint(IPoint point) {
        return this.mActiveView.FocusMap().FromMapPoint(point);
    }

    public double FromWorldDistance(double worldDistance) {
        return this.mActiveView.FocusMap().FromMapDistance(worldDistance);
    }

    public double ToWorldDistance(double deviceDistance) {
        return this.mActiveView.FocusMap().ToMapDistance(deviceDistance);
    }

    /*public void setdata(IMap map, int fid, IFeatureLayer layer, IEnvelope env) {
        this.fLayer = layer;
        this.mFid = fid;
        this.menv = new Envelope(env.XMin() - env.Width() * 0.1D, env.YMin() - env.Height() * 0.1D, env.XMax() + env.Width() * 0.1D, env.YMax() + env.Height() * 0.1D);
        if(this.mFid > -1) {
            IFeatureClass fClass = this.fLayer.getFeatureClass();

            try {
                map.getSelectionSet().ClearSelection();
                this.getActiveView().FocusMap().getSelectionSet().ClearSelection();
                ArrayList e = new ArrayList();
                e.add(Integer.valueOf(this.mFid));
                fClass.setSelectionSet(e);
                this.setMap(map);
                this.getActiveView().FocusMap().setExtent(this.menv);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

    }

    public void setdata(List<Integer> fids, IFeatureLayer layer, List<IEnvelope> envs) {
        this.fLayer = layer;
        this.menv = this.getAllSelectEnvelope(envs);
        SelectedFeatures s = new SelectedFeatures();
        s.FeatureClass = layer.getFeatureClass();
        s.FIDs = fids;
        layer.getFeatureClass().setSelectionSet(s.FIDs);
        this.getActiveView().FocusMap().getSelectionSet().AddFeatures(s);
        this.getActiveView().FocusMap().setExtent(this.menv);
        this.PartialRefresh();
    }*/

    public void StopDraw() {
        ImageDownLoader.cancelTask();
    }

    public void doEvent(EventObject event) {
        int width = this.getWidth();
        int height = this.getHeight();
        if(width != 0 && height != 0) {
            this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, (double)width, (double)height));
        } else {
            this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, 60.0D, 60.0D));
        }
    }

    public void dispose() throws Exception {
        if(this.mBitScreen != null && !this.mBitScreen.isRecycled()) {
            this.mBitScreen.recycle();
            this.mBitScreen = null;
        }

        this.mBitScreen = null;
        this.myHandler = null;
        this.mProgressBar = null;
        this.mPaint = null;
        this.menv = null;
        /*this.fLayer = null;
        this.mfieldID = null;*/
        this.mActiveView.dispose();
        this.mActiveView = null;
        //this.mTVRules = null;
        ((ZoomPan)this.mZoomPan).dispose();
        this.mZoomPan = null;
        //this.mGPSTool = null;
        this.mDrawTool = null;
    }

    public void EditRefresh() {
    }

    public void Copy(BaseControl targetControl) {
        if(targetControl.getActiveView().FocusMap() == null) {
            targetControl.getActiveView().FocusMap(this.mActiveView.FocusMap());
        }

        if(!targetControl.getActiveView().equals(this.mActiveView)) {
            this.mActiveView = targetControl.getActiveView();
            this.mActiveView.getContentChanged().addListener(this);
        }

        this.mActiveView.FocusMap().setDeviceExtent(new Envelope(0.0D, 0.0D, (double)this.getWidth(), (double)this.getHeight()));
    }

    private IEnvelope getAllSelectEnvelope(List<IEnvelope> envs) {
        this.menv = this.getActiveView().FocusMap().getExtent();
        if(envs != null && envs.size() != 0) {
            Iterator itenvs = envs.iterator();
            if(itenvs.hasNext()) {
                this.menv = (IEnvelope)itenvs.next();
            }

            double minx = this.menv.XMin();
            double miny = this.menv.YMin();
            double maxx = this.menv.XMax();
            double maxy = this.menv.YMax();

            while(itenvs.hasNext()) {
                IEnvelope env = (IEnvelope)itenvs.next();
                if(env.XMin() < minx) {
                    minx = env.XMin();
                }

                if(env.YMin() < miny) {
                    miny = env.YMin();
                }

                if(env.XMax() > maxx) {
                    maxx = env.XMax();
                }

                if(env.YMax() > maxy) {
                    maxy = env.YMax();
                }
            }

            this.menv = new Envelope(minx - (maxx - minx) * 0.1D, miny - (maxy - miny) * 0.1D, maxx + (maxx - minx) * 0.1D, maxy + (maxy - miny) * 0.1D);
            return this.menv;
        } else {
            return this.menv;
        }
    }

}
