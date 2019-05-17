package com.ereader.aesdecrypt;

import java.io.InputStream;

public class AesDecrypt {

	static{
		System.loadLibrary("aesdecryto");
	}
	
	static public native InputStream aesDecrypt(String key, InputStream in);
}
