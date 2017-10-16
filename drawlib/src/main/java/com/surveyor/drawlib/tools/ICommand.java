package com.surveyor.drawlib.tools;

import android.graphics.Bitmap;
import android.view.View;

import com.surveyor.drawlib.view.BaseControl;

/**
 * Created by stg on 17/10/15.
 */
public interface ICommand extends View.OnClickListener {
    String getText();

    Bitmap getBitmap();

    Boolean getEnable();

    void setEnable(Boolean var1);

    BaseControl getBuddyControl();

    void setBuddyControl(BaseControl var1);

    void onClick(View var1);
}
