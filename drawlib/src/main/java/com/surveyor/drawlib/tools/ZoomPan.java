package com.surveyor.drawlib.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.surveyor.drawlib.map.IActiveView;
import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.mapview.BaseControl;
import com.surveyor.drawlib.mapview.MapView;

import srs.Geometry.Envelope;
import srs.Geometry.IEnvelope;
import srs.Geometry.IPoint;
import srs.tools.SETTINGSMAGNIFY;

/**
 * Created by stg on 17/10/15.
 */
public class ZoomPan extends BaseTool implements ITool{
    private PointF pointOld1;
    private PointF pointOld2;
    private IPoint pointMidMap1;
    private IPoint pointOldMap1;
    private IPoint pointOldMap2;
    private PointF pointMid1Pic;
    private double dis1;
    private Bitmap mBitmapCurrent;
    private IMap map;
    private long mStartTime;
    private long mEndTime;
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static final int NEVER = 999;
    public Bitmap mBitmapShaderView = null;
    public ShapeDrawable mDrawableShape = null;
    private BitmapShader mShader = null;
    private long mDownTime = -1L;

    public void dispose() throws Exception {
        this.pointOld1 = null;
        this.pointOld2 = null;
        this.pointMid1Pic = null;
        if(this.pointMidMap1 != null) {
            this.pointMidMap1.dispose();
            this.pointMidMap1 = null;
        }

        if(this.pointOldMap1 != null) {
            this.pointOldMap1.dispose();
            this.pointOldMap1 = null;
        }

        if(this.pointOldMap2 != null) {
            this.pointOldMap2.dispose();
            this.pointOldMap2 = null;
        }

        this.map = null;
        this.mDrawableShape = null;
        this.mShader = null;
        if(this.mBitmapCurrent != null) {
            this.mBitmapCurrent.recycle();
            this.mBitmapCurrent = null;
        }

        if(this.mBitmapShaderView != null) {
            this.mBitmapShaderView.recycle();
            this.mBitmapShaderView = null;
        }

    }

    public boolean isMAGNIFY() {
        return false;
    }

    public ZoomPan() {
        super.setRate();
    }

    public String getText() {
        return "放大";
    }

    public Bitmap getBitmap() {
        return null;
    }

    public BaseControl getBuddyControl() {
        return this.mBuddyControl;
    }

    public void DrawAgain() {
        this.mBitmapCurrent = null;
    }

    public void setBuddyControl(BaseControl basecontrol) {
        this.mBuddyControl = basecontrol;
        if(this.mBuddyControl != null) {
            super.mEnable = Boolean.valueOf(true);
        } else {
            super.mEnable = Boolean.valueOf(false);
        }

    }

    public boolean onTouch(View v, MotionEvent event) {
        try {
            PointF p2Pic;
            switch(event.getAction()) {
                case 0:
                    if(this.mBitmapCurrent != null && !this.mBitmapCurrent.isRecycled()) {
                        v.setBackgroundDrawable(null);
                        Log.i("RECYCLE", "RECYCLE mBitmapCurrent（MapControl的BackGround）" + this.mBitmapCurrent);
                        this.mBitmapCurrent.recycle();
                        this.mBitmapCurrent = null;
                    }

                    ((MapView)this.mBuddyControl).StopDraw();
                    if(event.getPointerCount() == 1) {
                        if(this.map == null) {
                            IActiveView g1 = this.mBuddyControl.getActiveView();
                            this.map = g1.FocusMap();
                        }

                        this.pointOld1 = new PointF(event.getX() * this.mRate, event.getY() * this.mRate);
                        this.pointOldMap1 = this.map.ToMapPoint(this.pointOld1);
                        ((MapView)this.mBuddyControl).MODE = 0;
                        this.mStartTime = System.currentTimeMillis();
                        this.mDownTime = this.mStartTime;
                    }
                    break;
                case 1:
                    this.mEndTime = System.currentTimeMillis();
                    if(this.mEndTime - this.mDownTime >= 60L) {
                        if(this.map == null) {
                            this.map = this.mBuddyControl.getActiveView().FocusMap();
                        }

                        if(((MapView)this.mBuddyControl).MODE == 1) {
                            IPoint currentPointMap2 = this.map.ToMapPoint(new PointF(event.getX() * this.mRate, event.getY() * this.mRate));
                            IEnvelope e1 = (IEnvelope)this.map.getExtent().Clone();
                            e1.Move(this.pointOldMap1.X() - currentPointMap2.X(), this.pointOldMap1.Y() - currentPointMap2.Y());
                            this.map.setExtent(e1);
                            this.mBuddyControl.Refresh();
                            ((MapView)this.mBuddyControl).MODE = 0;
                        }
                    }
                    break;
                case 2:
                    if(this.mBitmapCurrent == null) {
                        this.mBitmapCurrent = Bitmap.createBitmap(this.map.ExportMap(false).getWidth(), this.map.ExportMap(false).getHeight(), Bitmap.Config.RGB_565);
                        Log.i("RECYCLE", "Create mBitmapCurrent" + this.mBitmapCurrent);
                    }

                    if(this.map == null) {
                        this.map = this.mBuddyControl.getActiveView().FocusMap();
                    }

                    Canvas g = new Canvas(this.mBitmapCurrent);
                    this.mEndTime = System.currentTimeMillis();
                    if(((MapView)this.mBuddyControl).MODE == 0 && event.getPointerCount() == 1) {
                        ((MapView)this.mBuddyControl).MODE = 1;
                    } else if(this.mEndTime - this.mStartTime > 10L) {
                        Bitmap currentPointMap1;
                        if(((MapView)this.mBuddyControl).MODE == 2) {
                            currentPointMap1 = this.map.ExportMap(true);
                            if(currentPointMap1 != null && !currentPointMap1.isRecycled()) {
                                Log.i("MotionEvent", "MotionEvent.ACTION_MOVE 重置 缩放 时的缓存画面");
                            }

                            p2Pic = new PointF(event.getX(0) * this.mRate, event.getY(0) * this.mRate);
                            PointF dis22 = new PointF(event.getX(1) * this.mRate, event.getY(1) * this.mRate);
                            double dis21 = Math.sqrt((double)((p2Pic.x - dis22.x) * (p2Pic.x - dis22.x) + (p2Pic.y - dis22.y) * (p2Pic.y - dis22.y)));
                            double ratePic1 = this.dis1 / dis21;
                            Point pRT1 = new Point(currentPointMap1.getWidth() / 2, currentPointMap1.getHeight() / 2);
                            int Left1 = (int)((double)((float)pRT1.x - this.pointMid1Pic.x) * ((dis21 - this.dis1) / this.dis1));
                            int yMove = (int)((double)((float)pRT1.y - this.pointMid1Pic.y) * ((dis21 - this.dis1) / this.dis1));
                            Point R1 = new Point(pRT1.x + Left1, pRT1.y + yMove);
                            float wPic = (float)((double)currentPointMap1.getWidth() / ratePic1);
                            float T1 = (float)((double)currentPointMap1.getHeight() / ratePic1);
                            float xdrawLPic = (float)R1.x - wPic / 2.0F;
                            float Low1 = (float)R1.y - T1 / 2.0F;
                            g.drawColor(-1);
                            g.drawBitmap(currentPointMap1, new Rect(0, 0, currentPointMap1.getWidth(), currentPointMap1.getHeight()), new RectF(xdrawLPic, Low1, xdrawLPic + wPic, Low1 + T1), new Paint());
                            BitmapDrawable bd = new BitmapDrawable(v.getContext().getResources(), this.mBitmapCurrent);
                            ((MapView)v).setBackgroundDrawable(bd);
                        }

                        if(((MapView)this.mBuddyControl).MODE == 1) {
                            currentPointMap1 = this.map.ExportMap(true);
                            if(currentPointMap1 != null && !currentPointMap1.isRecycled()) {
                                Log.i("MotionEvent", "MotionEvent.ACTION_MOVE 重置 平移 时的缓存画面");
                            }

                            g.drawColor(-1);
                            g.drawBitmap(this.map.ExportMap(false), event.getX() * this.mRate - this.pointOld1.x, event.getY() * this.mRate - this.pointOld1.y, (Paint)null);
                            BitmapDrawable p2Pic1 = new BitmapDrawable(v.getContext().getResources(), this.mBitmapCurrent);
                            ((MapView)v).setBackgroundDrawable(p2Pic1);
                        }
                    }
                    break;
                case 6:
                case 262:
                    if(this.map == null) {
                        this.map = this.mBuddyControl.getActiveView().FocusMap();
                    }

                    if(((MapView)this.mBuddyControl).MODE == 2) {
                        PointF currentPointMap = new PointF(event.getX(0) * this.mRate, event.getY(0) * this.mRate);
                        p2Pic = new PointF(event.getX(1) * this.mRate, event.getY(1) * this.mRate);
                        double dis2 = Math.sqrt((double)((currentPointMap.x - p2Pic.x) * (currentPointMap.x - p2Pic.x) + (currentPointMap.y - p2Pic.y) * (currentPointMap.y - p2Pic.y)));
                        double ratePic = this.dis1 / dis2;
                        IPoint pLL = this.map.getExtent().LowerLeft();
                        IPoint pRT = this.map.getExtent().UpperRight();
                        double Left = ratePic * (pLL.X() - this.pointMidMap1.X()) + this.pointMidMap1.X();
                        double R = ratePic * (pRT.X() - this.pointMidMap1.X()) + this.pointMidMap1.X();
                        double T = ratePic * (pRT.Y() - this.pointMidMap1.Y()) + this.pointMidMap1.Y();
                        double Low = ratePic * (pLL.Y() - this.pointMidMap1.Y()) + this.pointMidMap1.Y();
                        Envelope e = new Envelope(Left, Low, R, T);
                        this.map.setExtent(e);
                        this.mBuddyControl.Refresh();
                        ((MapView)this.mBuddyControl).MODE = 999;
                    }
                    break;
                case 261:
                    if(event.getPointerCount() == 2) {
                        Log.i("MotionEvent", "MotionEvent.ACTION_POINTER_2_DOWN");
                        if(this.map == null) {
                            this.map = this.mBuddyControl.getActiveView().FocusMap();
                        }

                        this.pointOld1 = new PointF(event.getX(0) * this.mRate, event.getY(0) * this.mRate);
                        this.pointOld2 = new PointF(event.getX(1) * this.mRate, event.getY(1) * this.mRate);
                        this.dis1 = Math.sqrt((double)((this.pointOld1.x - this.pointOld2.x) * (this.pointOld1.x - this.pointOld2.x) + (this.pointOld1.y - this.pointOld2.y) * (this.pointOld1.y - this.pointOld2.y)));
                        this.pointMid1Pic = new PointF((this.pointOld1.x + this.pointOld2.x) / 2.0F, (this.pointOld1.y + this.pointOld2.y) / 2.0F);
                        this.pointOldMap1 = this.map.ToMapPoint(new PointF(event.getX(0) * this.mRate, event.getY(0) * this.mRate));
                        this.pointOldMap2 = this.map.ToMapPoint(new PointF(event.getX(1) * this.mRate, event.getY(1) * this.mRate));
                        this.pointMidMap1 = new srs.Geometry.Point((this.pointOldMap1.X() + this.pointOldMap2.X()) / 2.0D, (this.pointOldMap1.Y() + this.pointOldMap2.Y()) / 2.0D);
                        ((MapView)this.mBuddyControl).MODE = 2;
                        this.mStartTime = System.currentTimeMillis();
                    }
            }
        } catch (Exception var21) {
            Toast.makeText(this.mBuddyControl.getContext(), var21.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    public void drawMagnify(Canvas canvas) {
        this.mDrawableShape.draw(canvas);
        if(SETTINGSMAGNIFY.getFDJDSZ() != null) {
            float x = (float)(SETTINGSMAGNIFY.X - 2 * SETTINGSMAGNIFY.getRADIUS());
            x = x < 0.0F?0.0F:x;
            float y = (float)(SETTINGSMAGNIFY.Y - 2 * SETTINGSMAGNIFY.getRADIUS());
            y = y < 0.0F?0.0F:y;
            canvas.drawBitmap(SETTINGSMAGNIFY.getFDJDSZ(), x, y, (Paint)null);
        }

    }

    @Override
    public void SaveResault() {

    }
}
