package com.jasonbobo.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public 	String getMD5(String url){
		String key=null;
		try {
			MessageDigest digest=MessageDigest.getInstance("MD5");//��ʼ��
			digest.update(url.getBytes());//��������
			key=bytesToHexString(digest.digest());//digest.digest()���ؼ���Ľ��,Ȼ��ת��Ϊ�ַ���
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			key=String.valueOf(url.hashCode());
		}
		return key;
	}
//ת��Ϊ�ַ���
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
