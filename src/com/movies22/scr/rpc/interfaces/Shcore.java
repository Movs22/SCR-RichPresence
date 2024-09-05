package com.movies22.scr.rpc.interfaces;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public interface Shcore extends com.sun.jna.Library {
    Shcore INSTANCE = Native.load("Shcore", Shcore.class);

    HRESULT GetDpiForMonitor(HMONITOR hmonitor, int dpiType, DWORDByReference dpiX, DWORDByReference dpiY);

    int MDT_EFFECTIVE_DPI = 0; // For effective DPI
    int MDT_ANGULAR_DPI = 1; // For angular DPI
    int MDT_RAW_DPI = 2; // For raw DPI
}