package com.movies22.scr.rpc.utils;

import java.awt.image.BufferedImage;

import com.movies22.scr.rpc.Main;
import com.movies22.scr.rpc.interfaces.GDI32Extra;
import com.movies22.scr.rpc.interfaces.Shcore;
import com.movies22.scr.rpc.interfaces.User32Extra;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinUser.MONITORINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;

public class CaptureUtils {
	
	private static int DEFAULT_DPI = 96; // 96 - windows | 72 - osX/macOs | If you add MacOS support make sure to change this value
	
	private static int CURRENT_DPI = -1;
	
	private static DWORD SRCCOPY = new DWORD(0x00CC0020);
	
	public static BufferedImage Capture(HWND hWnd) {

        HDC hdcWindow = User32.INSTANCE.GetDC(hWnd);
        HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        RECT bounds = new RECT();
        User32Extra.INSTANCE.GetClientRect(hWnd, bounds);
        
        HMONITOR hMonitor = User32.INSTANCE.MonitorFromWindow(hWnd, User32.MONITOR_DEFAULTTONEAREST);

        DWORDByReference dpiX = new DWORDByReference(new DWORD(0));
        DWORDByReference dpiY = new DWORDByReference(new DWORD(0));
        HRESULT result = Shcore.INSTANCE.GetDpiForMonitor(hMonitor, Shcore.MDT_EFFECTIVE_DPI, dpiX, dpiY);

        double scale = 1.0;
        
        if (result.equals(new HRESULT(0))) {
        	int dpi = dpiX.getValue().intValue();
        	if(CURRENT_DPI == -1 || CURRENT_DPI != dpi) {
        		Main.logger.info("Monitor info: DPI=" + dpiX.getValue() + ";SCALE=" + ((double) dpi/DEFAULT_DPI));
        	}
        	scale = ((double) dpi/DEFAULT_DPI);
        	CURRENT_DPI = dpi;
        } else {
            System.out.println("Failed to get DPI: " + result);
        }
        
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        if(width * height == 0) return null;
        
        HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        BITMAPINFO bmi = new BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
        
        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return ImageUtils.resize(image, width/scale, height/scale);
    }
	
	public static boolean isFullScreen(HWND window)
	{
		HMONITOR monitor = User32.INSTANCE.MonitorFromWindow(window, User32.MONITOR_DEFAULTTONEAREST);

	    // Get the rectangle for the monitor
	    MONITORINFO monitorInfo = new MONITORINFO();
	    User32.INSTANCE.GetMonitorInfo(monitor, monitorInfo);
	    RECT monitorRectangle = monitorInfo.rcMonitor;
	    
	    RECT windowRectangle = new RECT();
	    User32.INSTANCE.GetWindowRect(window, windowRectangle);
	    Main.logger.info(monitorRectangle.toString());
	    return windowRectangle.toString().equals(monitorRectangle.toString());
	}
}


