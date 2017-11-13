/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 裁剪返回监听接口
 * 
 * @author keqian
 * 
 */
public interface OnShearBackListener extends EventListener {
	/**
	 * 处理裁剪返回的方法，重写以处理返回动作
	 * 
	 * @param event
	 * @return 处理完成返回True，否则，返回false
	 */
	public boolean shearBack(EventObject event);
}
