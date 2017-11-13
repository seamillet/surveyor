/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 编辑保存监听接口
 * 
 * @author keqian
 * 
 */
public interface OnEditSaveListener extends EventListener {
	/**
	 * 处理编辑保存的方法，重写以处理保存动作
	 * 
	 * @param event
	 * @return 保存成功返回True，否则，返回false
	 */
	public boolean editSave(EventObject event);
}
