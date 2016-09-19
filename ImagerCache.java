package com.jasonbobo.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

public class ImagerCache {
	private MyLruCache myLruCache;//�ڴ滺��
	private MyDiskLruCache myDiskLruCache;//���̻���
	private MD5 md5;//MD5����
	private static final int CPU_COUNT=Runtime.getRuntime().availableProcessors();//CPU�ں�����
	private static final int CORE_POOL_SIZE=CPU_COUNT+1;
	private static final int MAXIMUM_POOL_SIZE=CPU_COUNT*2+1;
	private static final long KEEP_ALIVE=10L;
	public ImagerCache(Context context){
		myLruCache=new MyLruCache(context);
		myDiskLruCache=new MyDiskLruCache(context);
		md5=new MD5();
	}
	//�̴߳�������
	private static final ThreadFactory S_THREAD_FACTORY=new ThreadFactory() {
		private final AtomicInteger mAtomicInteger=new AtomicInteger();
		public Thread newThread(Runnable r) {
			// TODO Auto-generated method stub
			
			return new Thread(r,"Imageloader#"+mAtomicInteger.getAndIncrement());
		}
	};
	
	//�̳߳�
	public static final Executor THREAD_POOL=new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS	,new LinkedBlockingDeque<Runnable>(),S_THREAD_FACTORY);
	private Handler mHandler=new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message message){
			LoaderReault reault=(LoaderReault) message.obj;
			ImageView imageView=reault.imageView;
			String url=(String) imageView.getTag(imageView.getId());
			if(url.equals(reault.url)){
				imageView.setImageBitmap(reault.bitmap);
			}
			if(reault.bitmap==null){
				imageView.setImageResource(com.example.imageloader.R.drawable.ic_launcher);
			}
		}
	};
	//��ͼƬ���첽
	public void BindBitmap(final String url,final ImageView imageView,final int width,final int height){
		imageView.setTag(imageView.getId(),url);
		Runnable lRunnable=new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap =getImagerCache(url, width, height);
					LoaderReault reault=new LoaderReault(imageView, url, bitmap);
					mHandler.obtainMessage(1, reault).sendToTarget();
			}
		};
		THREAD_POOL.execute(lRunnable);
	}
	//��ȡ����ͼƬ
	public Bitmap getImagerCache(String url,int width,int height){
		Bitmap bitmap=null;
		String key=md5.getMD5(url);
		bitmap=getLruCache(key);
		if(bitmap!=null){
			return bitmap;
		}else{
			bitmap=getDiskLruCache(key, width, height);
			if(bitmap!=null){
				return bitmap;
			}else{
				bitmap=getNetwork(url, width, height);
			}
		}
		
		
		return bitmap;
	}
	
	//��ȡ�ڴ滺��ͼƬ
	private Bitmap getLruCache(String key){
		Bitmap bitmap=null;
		bitmap=myLruCache.getBitmapFromMyCache(key);
		return bitmap;
	}
	
	//��ȡ���̻���ͼƬ
	private Bitmap getDiskLruCache(String key,int width,int heigth){
		Bitmap bitmap=null;
		bitmap=myDiskLruCache.getDiskLruCacheBitmap(key, width,heigth);
		if(bitmap!=null){
			myLruCache.addBitmapToMyCache(key, bitmap);
		}
		return bitmap;
	}
	//��ȡ����ͼƬ
	private Bitmap getNetwork(String url,int width,int heigth){
		Bitmap bitmap=null;
		String key=md5.getMD5(url);
		try {
			DiskLruCache.Editor editor=myDiskLruCache.diskLruCache.edit(key);
			if(editor!=null){
				OutputStream outputStream=editor.newOutputStream(0);
				if(downloadToStream(url, outputStream)){
					editor.commit();
				}else{
					editor.abort();
				}
				myDiskLruCache.diskLruCache.flush();
				bitmap=getDiskLruCache(key, width, heigth);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	//����ͼƬ
	private boolean downloadToStream(String url,final OutputStream outputStream){
		 HttpURLConnection urlConnection=null;
		 BufferedInputStream in=null;
		 BufferedOutputStream out=null;
		 try {
			final URL urls=new URL(url);
			urlConnection=(HttpURLConnection) urls.openConnection();
			in=new BufferedInputStream(urlConnection.getInputStream());
			out=new BufferedOutputStream(outputStream);
			int b;
			while ((b=in.read())!=-1) {

				out.write(b);
			}
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(urlConnection!=null){
				urlConnection.disconnect();
			}
			try {
				if(out!=null){
				out.flush();
				out.close();}
				if(in!=null){
				in.close();}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		 return false;
	}
	//Messge���ص����ݶ���
	public static class LoaderReault{
		public ImageView imageView;
		public String url;
		public Bitmap bitmap;
		
		public LoaderReault(ImageView imageView,String url,Bitmap bitmap){
			this.imageView=imageView;
			this.url=url;
			this.bitmap=bitmap;
		}
	}
}
