/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 采集返回监听接口
 * 
 * @author keqian
 * 
 */
public interface OnCollectBackListener extends EventListener {
	/**
	 * 处理采集返回的方法，重写以处理返回动作
	 * 
	 * @param event
	 * @return 处理完成返回True，否则，返回false
	 */
	public boolean collectBack(EventObject event);
}
