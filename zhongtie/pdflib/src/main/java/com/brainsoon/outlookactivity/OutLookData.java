package com.brainsoon.outlookactivity;

import java.io.Serializable;
import java.util.ArrayList;

public class OutLookData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2607004663787068172L;
	public static final int TYPE_PDF = 0;
	public static final int TYPE_EPUB = 1;
	public static final String OUT_LINE = "OutlineData";
	private OutlineItem[] mData;
	private int mType;
	private String mBookNameString;
	
	public OutLookData(OutlineItem[] data, int type, String bookName)
	{
		mData = data;
		mType = type;
		mBookNameString = bookName;
	}
	public boolean isDataNull(){
		return (mData == null) ? true : false;
	}
	
	public OutlineItem[] getData(){
		return mData;
	}
	
	public int getBookType() {
		return mType;
	}
	
	public String getBookName() {
		return mBookNameString;
	}
}
