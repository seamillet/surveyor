/**
 * 
 */
package com.willc.surveyor.interoperation.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * 裁剪保存监听接口
 * 
 * @author keqian
 * 
 */
public interface OnShearSaveListener extends EventListener {
	/**
	 * 处理裁剪保存的方法，重写以处理保存动作
	 * 
	 * @param event
	 * @return 保存成功返回True，否则，返回false
	 */
	public boolean shearSave(EventObject event);
}
