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
import com.willc.surveyor.collect.GeoCollectManager;
import com.willc.surveyor.interoperation.CollectInteroperator;
import com.willc.surveyor.tools.DrawingTool;
import com.willc.surveyor.tools.EditTools;

import java.io.IOException;
import java.util.EventObject;

/**
 * 要素采集和节点编辑Activity
 */
public class CollectActivity extends Activity {
    private MapView mapView = null;
    // The tool of collecting points Manually
    private DrawingTool drawingTool = null;

    // UI references.
    private LinearLayout actionBack = null;
    private LinearLayout actionSave = null;
    private LinearLayout actionGPS = null;
    private LinearLayout actionUndo = null;
    private LinearLayout actionDelpt = null;
    private LinearLayout actionClear = null;
    private TextView txtTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collect);

        mapView = (MapView) findViewById(R.id.map_collect);
        txtTitle = (TextView) findViewById(R.id.title);
        txtTitle.setText("量房采集");
        // Initial action controls
        actionBack = (LinearLayout) findViewById(R.id.action_back);
        actionSave = (LinearLayout) findViewById(R.id.action_save);
        actionGPS = (LinearLayout) findViewById(R.id.action_gps);
        actionUndo = (LinearLayout) findViewById(R.id.action_undo);
        actionDelpt = (LinearLayout) findViewById(R.id.action_delpt);
        actionClear = (LinearLayout) findViewById(R.id.action_clear);
        // Set click event to them
        bindEventToActions();

        try {
            mapView.setMap(CollectInteroperator.getMap());
            GeoCollectManager.setMapControl(mapView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Edit settings
        if (!CollectInteroperator.isNew()) {
            try {
                GeoCollectManager.getCollector().setEditGeometry(CollectInteroperator.getEditGeometry());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Init DrawingTool tool and Set it to BuddyControl
        if (drawingTool == null) {
            drawingTool = new DrawingTool(this);
        }
        drawingTool.OnCreate(mapView);
        mapView.setDrawTool(drawingTool);
    }

    private void bindEventToActions() {
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        actionSave.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {
				Intent intent = getIntent();
				if (GeoCollectManager.getCollector().isGeometryValid(CollectActivity.this)) {
					boolean isSuccess = false;
					if (CollectInteroperator.isNew()) {
						isSuccess = CollectInteroperator.CollectEventManager.fireCollectSave(new EventObject(v));
					}
					if (isSuccess) {
						// 释放资源
						try {
							dispose();
						} catch (IOException e) {
							showToast(e.getMessage());
						}
						finish();
					}
				}
			}
        });
        actionGPS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //GPSUtil.addPointForCollecting( mapView.getMap().getGeoProjectType());
                    //drawingTool.setValues();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
        actionUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditTools.undo();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
        actionDelpt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditTools.delpt();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
        actionClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditTools.clear();
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        });
    }

    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CollectActivity.this);
        builder.setTitle("是否放弃采集?");
        builder.setMessage("是否确定要放弃已采集的要素?");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	 Intent intent = getIntent();
                // 触发返回处理事件
                boolean isSuccess = false;
                if (CollectInteroperator.isNew()) {
                    isSuccess = CollectInteroperator.CollectEventManager
                            .fireCollectBack(new EventObject(
                                    CollectActivity.this));
                }else if(intent.getBooleanExtra("obtainArea", false)){
                	isSuccess = CollectInteroperator.CollectEventManager
                            .fireAreaBack(new EventObject(
                                    CollectActivity.this));
                } else {
                    isSuccess = CollectInteroperator.CollectEventManager
                            .fireEditBack(new EventObject(CollectActivity.this));
                }
                if (isSuccess) {
                    // 回收资源
                    try {
                        dispose();
                    } catch (IOException e) {
                        showToast(e.getMessage());
                    }
                    if(intent.getBooleanExtra("obtainArea", false))
                    {
                        CollectActivity.this.setResult(602, intent);
                    }else{
                    	CollectActivity.this.setResult(604, intent);
                    }
                    // 关闭activity
                    finish();
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
     */
    private void dispose() throws IOException {
        mapView.setDrawTool(null);
        drawingTool = null;
        GeoCollectManager.dispose();
        CollectInteroperator.dispose();
        System.gc();
    }

    @SuppressLint("ShowToast")
    private void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    }
}
