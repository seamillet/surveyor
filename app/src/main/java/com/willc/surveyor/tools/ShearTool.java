/**
 * 
 */
package com.willc.surveyor.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surveyor.drawlib.mapview.MapView;
import com.surveyor.drawlib.tools.BaseTool;
import com.willc.surveyor.split.GeoSplitManager;

import java.io.IOException;
import java.math.BigDecimal;

import srs.Geometry.IPoint;

/**
 * @author keqian 裁剪工具
 */
public class ShearTool extends BaseTool {
	boolean isDraw = true;
	PointF mDownPt = null;

	public ShearTool(Context context) {
		super.setRate();
		mDownPt = new PointF();
	}

	public void create(MapView buddyControl) {
		this.setBuddyControl(buddyControl);
		this.setEnable(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean flag = false;
		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownPt.set(event.getX(), event.getY());
				isDraw = true;
				break;
			case MotionEvent.ACTION_MOVE:
				double xx = event.getX() - mDownPt.x;
				double yy = event.getY() - mDownPt.y;
				if (Math.sqrt(xx * xx + yy * yy) > 10) {
					isDraw = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isDraw) {
					IPoint point = toWorldPoint(new PointF(event.getX(), event.getY()));
					GeoSplitManager.Instance().addPoint(point);
					GeoSplitManager.Instance().refresh();
					checkSplitable();

					flag = true;
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 将屏幕坐标转换为实际地理坐标
	 * 
	 * @param pf
	 *            屏幕坐标
	 * @return 实际地理坐标
	 */
	private IPoint toWorldPoint(PointF pf) {
		return getBuddyControl().ToWorldPoint(
				new PointF(pf.x * mRate, pf.y * mRate));
	}

	/**
	 * 检查是否可以切割
	 */
	public void checkSplitable() {
		if (GeoSplitManager.Instance().canSplit()) {
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
	private AlertDialog generateAlertDialog(Context context,
			CharSequence title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("否", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// Clear drawline
				// GeoSplitManager.Instance().clearDrawLine();
				try {
					// 默认执行一步点撤销
					GeoSplitManager.Instance().revoke();
					GeoSplitManager.Instance().refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		builder.setPositiveButton("是", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GeoSplitManager.Instance().split();
				GeoSplitManager.Instance().clearDrawLine();

				try {
					GeoSplitManager.Instance().refresh();
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
