package com.willc.surveyor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.surveyor.drawlib.mapview.MapView;
import com.willc.surveyor.interoperation.CollectInteroperator;
import com.willc.surveyor.split.GeoSplitManager;
import com.willc.surveyor.tools.ShearTool;

import java.io.IOException;
import java.util.EventObject;

/**
 * 要素采集和节点新建Activity
 */
public class ShearActivity extends Activity {

	// UI references.
	/**
	 * 返回
	 */
	private LinearLayout actionBack = null;
	/**
	 * 地块撤销
	 */
	private LinearLayout actionUndo = null;
	/**
	 * 保存
	 */
	private LinearLayout actionSave = null;
	/**
	 * GPS采点
	 */
	private LinearLayout actionGps = null;
	/**
	 * 切割线点撤销
	 */
	private LinearLayout actionRevoke = null;

	private TextView txtTitle = null;
	private MapView mapView = null;

	// 剪切工具
	private ShearTool shearTool = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shear);

		// Init and Set MapControl
		mapView = (MapView) findViewById(R.id.map_collect);
		mapView.setMap(CollectInteroperator.getMap());
		//mapView.Refresh();

		// Init the geometry will be sheared
		GeoSplitManager.Instance().setMapcontrol(mapView);
		//GeoSplitManager.Instance().initGeometry(CollectInteroperator.getShearGeometries());
		GeoSplitManager.Instance().initGeometry(CollectInteroperator.getCollectedGeometries());

		// Set title
		txtTitle = (TextView) findViewById(R.id.title);
		txtTitle.setText(R.string.title_activity_shear);

		// Init Shear tool and Set it to BuddyControl
		if (shearTool == null) {
			shearTool = new ShearTool(this);
		}
		shearTool.create(mapView);
		mapView.setDrawTool(shearTool);

		// Initial action controls
		actionBack = (LinearLayout) findViewById(R.id.action_back);
		actionUndo = (LinearLayout) findViewById(R.id.action_undo);
		actionSave = (LinearLayout) findViewById(R.id.action_save);
		actionGps = (LinearLayout) findViewById(R.id.action_gps);
		actionRevoke = (LinearLayout) findViewById(R.id.action_revoke);
		// Set click event to them
		bindEventToActions();
	}

	private void bindEventToActions() {
		actionBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});
		actionUndo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (GeoSplitManager.Instance().canUndo()) {
						undo();
					}
				} catch (Exception e) {
					showToast(e.getMessage());
				}
			}
		});
		actionSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ShearActivity.this);
				builder.setTitle("提示");
				builder.setMessage("保存后无法撤销！！！\n请确认切割无误然后点击“保存”");
				builder.setNegativeButton("取消保存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean isSuccess = CollectInteroperator.CollectEventManager
								.fireShearSave(new EventObject(actionSave));
						if (isSuccess) {
							// 回收资源
							try {
								dispose();
							} catch (IOException e) {
								showToast(e.getMessage());
							}
							// 关闭activity
							finish();
						}
					}
				});
				builder.create().show();

			}
		});
		actionGps.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// GPS采集点
//					GPSUtil.addPointForSplitting(((Map)mapView.getMap()).getGeoProjectType());
//					shearTool.checkSplitable();
//					shearTool.setValue();
				} catch (Exception e) {
					showToast(e.getMessage());
				}
			}
		});
		actionRevoke.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// 切割线点撤销
					GeoSplitManager.Instance().revoke();
				} catch (Exception e) {
					showToast(e.getMessage());
				}
			}
		});
	}

	private void back() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ShearActivity.this);
		builder.setTitle("是否放弃采集?");
		builder.setMessage("是否确定要放弃已采集的要素?");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = getIntent();
				// 触发返回处理事件
				boolean isSuccess = CollectInteroperator.CollectEventManager
						.fireShearBack(new EventObject(ShearActivity.this));
				if (isSuccess) {
					// 回收资源
					try {
						dispose();
					} catch (IOException e) {
						showToast(e.getMessage());
					}
					  ShearActivity.this.setResult(606, intent);
					// 关闭activity
					finish();
				}
			}
		});
		builder.create().show();
	}

	private void undo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ShearActivity.this);
		builder.setTitle("提示");
		builder.setMessage("如果执行撤销，将撤销最近一次的切割操作，请确定是否执行？");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					GeoSplitManager.Instance().undo();
					GeoSplitManager.Instance().clearDrawLine();
				} catch (Exception e) {
					showToast(e.getMessage());
				}
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 要素采集资源释放
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	private void dispose() throws IOException {
		mapView.setDrawTool(null);
		shearTool = null;
		GeoSplitManager.Instance().dispose();
		CollectInteroperator.dispose();
		System.gc();
	}

	@SuppressLint("ShowToast")
	private void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT);
	}
}
