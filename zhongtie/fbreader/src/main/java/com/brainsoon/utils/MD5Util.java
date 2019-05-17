package com.brainsoon.utils;

import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Util.java Description： Company：北京博云易讯科技有限公司
 * CreateDate：2011-11-25上午09:30:31
 * 
 * @author：纪佳琪
 */

@SuppressLint("DefaultLocale")
public class MD5Util {

	public static byte[] encrypt(String msg) {
		try {
			// 根据MD5算法生成MessageDigest对象
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] srcBytes = msg.getBytes();
			// 使用srcBytes更新摘要
			md5.update(srcBytes);
			// 完成哈希计算,得到result
			byte[] resultBytes = md5.digest();
			return resultBytes;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encryptRetStr(String msg) {
		try {
			// 根据MD5算法生成MessageDigest对象
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] srcBytes = msg.getBytes();
			// 使用srcBytes更新摘要
			md5.update(srcBytes);
			// 完成哈希计算,得到result
			byte[] resultBytes = md5.digest();
			return parseByte2HexStr(resultBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(new String(encrypt("hsylgwk-20120101"),"GBK"));
	}
}