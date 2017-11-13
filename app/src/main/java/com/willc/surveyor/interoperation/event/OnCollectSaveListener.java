/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 采集保存监听接口
 * 
 * @author keqian
 * 
 */
public interface OnCollectSaveListener extends EventListener {
	/**
	 * 处理采集保存的方法，重写以处理保存动作
	 * 
	 * @param event
	 * @return 保存成功返回True，否则，返回false
	 */
	public boolean collectSave(EventObject event);
}
