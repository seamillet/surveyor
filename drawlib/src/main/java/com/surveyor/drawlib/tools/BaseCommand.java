package com.surveyor.drawlib.tools;

import android.graphics.Bitmap;
import android.view.View;

import com.surveyor.drawlib.view.BaseControl;

/**
 * Created by stg on 17/10/15.
 */
public abstract class BaseCommand implements ICommand {
    protected Boolean mEnable;
    protected BaseControl mBuddyControl;

    public BaseCommand() {
    }

    public abstract String getText();

    public abstract Bitmap getBitmap();

    public Boolean getEnable() {
        return this.mEnable;
    }

    public void setEnable(Boolean isEnable) {
        this.mEnable = isEnable;
    }

    public BaseControl getBuddyControl() {
        return this.mBuddyControl;
    }

    public void setBuddyControl(BaseControl basecontrol) {
        this.mBuddyControl = basecontrol;
    }

    public void onClick(View v) {
    }
}
