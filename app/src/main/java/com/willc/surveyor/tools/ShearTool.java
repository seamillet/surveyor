/**
 * 
 */
package com.willc.surveyor.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.surveyor.drawlib.map.IMap;
import com.surveyor.drawlib.mapview.MapView;
import com.surveyor.drawlib.tools.BaseTool;
import com.willc.surveyor.collect.GeoCollectManager;
import com.willc.surveyor.split.GeoSplitManager;

import java.io.IOException;

import srs.Geometry.IPoint;

/**
 * @author keqian 裁剪工具
 */
public class ShearTool extends BaseTool {
	private static final String TAG = ShearTool.class.getSimpleName();

	IMap mMap = null;
	Bitmap mBitmapCurrentBack = null;

	/**
	 * onTouch()中, ACTION_DOWN时的点对象
	 */
	private PointF mDownPt = null;

	private boolean isDraw = true;
	private boolean mIsNearVertexs = false;
	private boolean mCanDrawLine = false;


	public ShearTool(Context context) {
		super.setRate();
		mDownPt = new PointF();
	}

	public void create(MapView buddyControl) {
		this.setBuddyControl(buddyControl);
		this.setEnable(true);

		if (mMap == null) {
			mMap = ((MapView) getBuddyControl()).getMap();
		}
		mBitmapCurrentBack = mMap.ExportMap(false).copy(Bitmap.Config.RGB_565, true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean flag = false;
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownPt.set(event.getX(), event.getY());

				final IPoint point = toWorldPoint(event.getX(), event.getY());
				Log.e(TAG, String.format("action_down world point[x=%s,y=%s]", point.X(), point.Y()));

				final int sizeOfPoints = GeoSplitManager.instance().getPointsSize();
				Log.e(TAG, String.format("size of points is %s", sizeOfPoints));
				if (sizeOfPoints == 0) {
					//判断是否在顶点附近
					if (GeoSplitManager.instance().isNearVertexs(point.X(), point.Y(), -1)) {
						Log.e(TAG, "action_down point near vertex points");
						GeoSplitManager.instance().addPoint(point);
						mIsNearVertexs = true;
						flag = true;
					}

				} else if (sizeOfPoints >= 2) {
					if (GeoSplitManager.instance().isNearLastPoint(point.X(), point.Y(), -1)) {
						Log.e(TAG, "action_down point near last point");
						mCanDrawLine = true;
						flag = true;
					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mIsNearVertexs) {
					drawLine(event);
					flag = true;
				} else if (mCanDrawLine) {
					drawLine(event);
					flag = true;
				}

				/*double xx = event.getX() - mDownPt.x;
				double yy = event.getY() - mDownPt.y;
				if (Math.sqrt(xx * xx + yy * yy) > 10) {
					isDraw = false;
				}*/
				break;
			case MotionEvent.ACTION_UP:
				Log.e(TAG, String.format("action_up screen point[x=%s,y=%s]", event.getX(), event.getY()));
				if (mIsNearVertexs) {
					GeoSplitManager.instance().addPoint(toWorldPoint(event.getX(), event.getY()));
					mIsNearVertexs = false;
					flag = true;
				} else if (mCanDrawLine) {
					GeoSplitManager.instance().addPoint(toWorldPoint(event.getX(), event.getY()));
					mCanDrawLine = false;
					flag = true;
				}
				GeoSplitManager.instance().refresh();
				checkSplitable();

				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 将屏幕坐标转换为实际地理坐标
	 * 
	 * @param x 屏幕x坐标
	 * @param y 屏幕y坐标
	 *
	 * @return 实际地理坐标
	 */
	private IPoint toWorldPoint(float x, float y) {
		return getBuddyControl().ToWorldPoint(new PointF(x * mRate, y * mRate));
	}

	private void drawLine(MotionEvent event) throws Exception {
		// Prepare
		mBitmapCurrentBack = mMap.ExportMap(false).copy(Bitmap.Config.RGB_565, true);
		Canvas canvas = new Canvas(mBitmapCurrentBack);

		// shearing line
		GeoSplitManager.instance().drawLineOnCanvas(canvas, event.getX(), event.getY());
		BitmapDrawable bg = new BitmapDrawable(getBuddyControl().getContext().getResources(), mBitmapCurrentBack);
		getBuddyControl().setBackgroundDrawable(bg);
	}

	/**
	 * 检查是否可以切割
	 */
	public void checkSplitable() {
		if (GeoSplitManager.instance().canSplit()) {
			generateAlertDialog(this.getBuddyControl().getContext(), "符合切割要求", "是否要进行切割?").show();
		}
	}

	/**
	 * 生成提示对话框
	 * 
	 * @param title
	 *            提示标题
	 * @param message
	 *            提示内容
	 * @return AlertDialog对象
	 */
	private AlertDialog generateAlertDialog(Context context, CharSequence title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("否", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// Clear drawline
				// GeoSplitManager.instance().clearDrawLine();
				try {
					// 默认执行一步点撤销
					GeoSplitManager.instance().revoke();
					GeoSplitManager.instance().refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		builder.setPositiveButton("是", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GeoSplitManager.instance().split();
				GeoSplitManager.instance().clearDrawLine();

				try {
					GeoSplitManager.instance().refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return builder.create();
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bitmap getBitmap() {
		// TODO Auto-generated method stub
		return null;
	}

}
