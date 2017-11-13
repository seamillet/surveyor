package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 面积几何计算保存监听接口
 * 
 * @author keqian
 * 
 */
public interface OnAreaSaveListener extends EventListener{
	
	/**
	 * 处理几何计算面积保存的方法，重写以处理返回动作
	 * 
	 * @param event
	 * @return 处理完成返回True，否则，返回false
	 */
	public boolean areaSave(EventObject event);
}
