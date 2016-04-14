package com.pvi.ap.reader.external.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

public class pdfReader {

	private static String TAG = "pdfReader-java";
	private Context parent;
	public pdfReader(Context p)
	{
		parent = p;
	}
    static {
    	
        try {
			System.load(Environment.getDataDirectory() + "/data/com.pvi.ap.reader.lib/lib/"+"libAdobeReader.so");
			//System.loadLibrary("AdobeReader");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public byte[] getAssetBytes(String assetName) {
		Log.d(TAG, "getAssetBytes:"+assetName);
		
		Resources res = parent.getResources();
		AssetManager assets = res.getAssets();

		InputStream asset = null;
		try {
			asset = assets.open(assetName);
			if(asset == null) {
				Log.e(TAG, "Cannot locate asset: " + assetName);
				return null;
			}

			byte[] ret = streamToBytes(asset);
			Log.d(TAG, "getAssetBytes( " + assetName +" ) returned " + Integer.toString(ret.length) + " bytes");
			return ret;
		} catch(IOException ex) {
			Log.e(TAG, "getAssetBytes IOException: " + ex);
			return null;
		} finally {
			try {
				if(asset != null) asset.close();
			} catch(IOException ex) {
			}
		}
	}

    private byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			int total = 0;
			while ((len = is.read(buf)) != -1) {
				os.write(buf, 0, len);
				total += len;
			}
			is.close();
			os.close();
		} catch(IOException ex) {
			Log.e(TAG, "streamToBytes IOException: " + ex);
		}

		return os.toByteArray();
    }
    
    public native boolean init();
    public native boolean setViewPortSize(int width, int height);
    public native boolean setCache(boolean enable);
    public native boolean open(String filepath);
    public native boolean close();
    public native byte[] getBitmapStuff(int cacheNum);
    public native int getCurrentPage(int cacheNum);
    public native int getPageCount();
    public native int getNaturalSize( int pageNum );
    public native boolean gotoPage( int pageNum, boolean after );
    public native boolean gotoPrevScreenPage();
    public native boolean gotoNextScreenPage();
    public native boolean setFontSize(double scale);
    // each time 100 items at most
    public native ChapterInfo[] getChapterList(int times);
    public native int getChapterListLen();  
    public native boolean hasTableOfContent();
    public native boolean autoCutMargin(boolean cut);
    public native boolean setFitMode(int fit);
    public native boolean setViewPort(int minx, int miny, int maxx,int maxy);
    public native boolean setPagingMode(int fit);
    public native boolean setScrollOffset(int offset);
    public native int search(String text, boolean isBackward, int start, int range);
    public native boolean docReachHeadOrTail(boolean head);	//reach head or tail


/*    
    public boolean gotoFirstPage()
    {
        return gotoPage(1);
    }

    public boolean gotoNextPage()
    {
        int at = getCurrentPage(1);
        if (at>0) {
            return gotoPage(at+1);
        }
        else {
            return false;
        }
    }

    public boolean gotoPrevPage()
    {
        int at = getCurrentPage(-1);
        if (at>0) {
            return gotoPage(at-1);
        }
        else {
            return false;
        }
    }
    public boolean gotoLastPage()
    {
        return gotoPage(getPageCount());
    }
*/

    /*	TODO: 
        1. structure of bookinfo(title, author, publisher etc...), bookmark   
        2. stored below JNI level or anroid app level
        */
    /*
       getBookInfo();

       setBookmark();

       gotoBookmarkedPage();
       delCurrenBookmark();

       getFrontPage;
       gotoLinkedPage();
       */

}
