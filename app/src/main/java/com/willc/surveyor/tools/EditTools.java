package com.willc.surveyor.tools;

import com.willc.surveyor.collect.GeoCollectManager;

public class EditTools {

	/**
	 * 撤销——返回上次操作
	 * 
	 * @throws Exception
	 */
	public static void undo() throws Exception {
		GeoCollectManager.getCollector().undo();
		GeoCollectManager.getCollector().refresh();
	}

	/**
	 * 清除所有节点
	 * 
	 * @throws Exception
	 */
	public static void clear() throws Exception {
		GeoCollectManager.getCollector().clear();
		GeoCollectManager.getCollector().refresh();
	}

	/**
	 * 删除当前选中的节点
	 * 
	 * @throws Exception
	 */
	public static void delpt() throws Exception {
		GeoCollectManager.getCollector().delpt();
		GeoCollectManager.getCollector().refresh();
	}
}
