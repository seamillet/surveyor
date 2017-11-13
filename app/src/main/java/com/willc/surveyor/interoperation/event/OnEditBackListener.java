/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 编辑返回监听接口
 * 
 * @author keqian
 * 
 */
public interface OnEditBackListener extends EventListener {
	/**
	 * 处理编辑返回的方法，重写以处理返回动作
	 * 
	 * @param event
	 * @return 处理完成返回True，否则，返回false
	 */
	public boolean editBack(EventObject event);
}
