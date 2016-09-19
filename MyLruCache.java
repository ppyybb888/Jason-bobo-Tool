package com.jasonbobo.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MyLruCache {
public LruCache<String, Bitmap> mLruCache;
	public MyLruCache(Context context){
		int maxMemory=(int)(Runtime.getRuntime().maxMemory());
		int maxSize=maxMemory/8;
		mLruCache=new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key,Bitmap bitmap){
				return bitmap.getRowBytes()*bitmap.getHeight()/1024;
			}
		};
	}
	
	public void addBitmapToMyCache(String key,Bitmap bitmap){
		if(getBitmapFromMyCache(key)==null){
			mLruCache.put(key, bitmap);
		}
	}
	public Bitmap getBitmapFromMyCache(String key){
		return mLruCache.get(key);
	}
}
