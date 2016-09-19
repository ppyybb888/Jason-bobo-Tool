package com.jasonbobo.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public 	String getMD5(String url){
		String key=null;
		try {
			MessageDigest digest=MessageDigest.getInstance("MD5");//初始化
			digest.update(url.getBytes());//处理数据
			key=bytesToHexString(digest.digest());//digest.digest()返回计算的结果,然后转换为字符串
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			key=String.valueOf(url.hashCode());
		}
		return key;
	}
//转换为字符串
	public String bytesToHexString(byte[] bytes){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<bytes.length;i++){
			String hex=Integer.toHexString(0xFF & bytes[i]);
			if(hex.length()==1){
				sb.append('0');
			}
			sb.append(hex);
		}
		
		return sb.toString();
		
		
	}
}
