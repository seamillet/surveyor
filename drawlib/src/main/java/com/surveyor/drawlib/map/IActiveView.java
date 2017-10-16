package com.surveyor.drawlib.map;

import com.surveyor.drawlib.map.event.ContentChangedManager;
import com.surveyor.drawlib.map.event.FocusMapChangedManager;

import org.dom4j.DocumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by stg on 17/10/14.
 */
public interface IActiveView {
    IMap FocusMap();

    void FocusMap(IMap var1);

    boolean IsRelativePath();

    void IsRelativePath(boolean var1);

    ContentChangedManager getContentChanged();

    FocusMapChangedManager getFocusMapChanged();

    void LoadFromFile(String var1) throws DocumentException;

    void SaveToFile(String var1) throws UnsupportedEncodingException, FileNotFoundException, IOException;

    void dispose();
}
