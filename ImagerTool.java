package com.jasonbobo.tool;

import java.io.FileDescriptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImagerTool {
	public ImagerCache imagerCache;
	public ImagerTool(Context context){
		imagerCache=new ImagerCache(context);
		
	}
	//°ó¶¨Í¼Æ¬
	public void BindImager(ImageView imageView,String url,int width,int height){
		imagerCache.BindBitmap(url, imageView, width, height);
	}
	//Ñ¹ËõÍ¼Æ¬
	public static Bitmap decodesampledBitmapFormFD(FileDescriptor fileDescriptor,int width,int height){
		Bitmap bitmap=null;
		
		final BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		options.inSampleSize=calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds=false;
		bitmap=BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		return bitmap;
	}
	//²âÁ¿Í¼Æ¬
	public static int calculateInSampleSize(BitmapFactory.Options options,int width,int height){
	
		final int this_height=options.outHeight;
		final int this_width=options.outWidth;
		int inSampleSize=1;
		if(this_height>height||this_width>width){
			final int halfHeight=this_height/2;
			final int halfWidth=this_width/2;
			while ((halfHeight/inSampleSize)>=height&&(halfWidth/inSampleSize)>=width) {
					inSampleSize*=2;
			}
		}
		return inSampleSize;
	}
	
}
