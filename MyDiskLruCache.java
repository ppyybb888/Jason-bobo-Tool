package com.jasonbobo.tool;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import com.jakewharton.disklrucache.DiskLruCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Build;
import android.os.StatFs;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;

public class MyDiskLruCache {
	private long maxSize=1024*1024*100;
	public DiskLruCache diskLruCache;
	public boolean Is_DiskLruCache_Create=false;
	public MyDiskLruCache(Context context){
		File file=getDiskLruCache(context, "Bitmap");
		if(!file.exists()){
			file.mkdirs();
		}
		if(getUsableSpace(file)>maxSize){
			try {
				diskLruCache=DiskLruCache.open(file, 1, 1, maxSize);
				Is_DiskLruCache_Create=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public Bitmap getDiskLruCacheBitmap(String key,int width,int height){
		Bitmap bitmap=null;
		try {
			DiskLruCache.Snapshot snapshot=diskLruCache.get(key);
			if(snapshot!=null){
				FileInputStream fileInputStream=(FileInputStream)snapshot.getInputStream(0);
				FileDescriptor fileDescriptor=fileInputStream.getFD();
				bitmap=ImagerTool.decodesampledBitmapFormFD(fileDescriptor, width, height);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
		
	}
	public File getDiskLruCache(Context context,String Filename){
		boolean externalStorageAvailable=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		final String cachepath;
		if(externalStorageAvailable){
			cachepath=context.getExternalCacheDir().getPath();
		}
		else{
			cachepath=context.getCacheDir().getPath();
		}
		return new File(cachepath+File.separator+Filename);
	}
	
	private long getUsableSpace(File file){
		if(Build.VERSION.SDK_INT>=VERSION_CODES.GINGERBREAD){
			return file.getUsableSpace();
		}
		final StatFs statFs=new StatFs(file.getPath());
		return (long)statFs.getBlockSize()*(long)statFs.getAvailableBlocks();
		
	}
}
