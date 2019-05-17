package com.ereader.zip;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class openzip {
	static{
		System.loadLibrary("zLib");
	}
	
	public openzip(String file){
		
		mBookInf = openZip(file);
		mfileNameString = file;
	}
	 private   String mfileNameString;
	 private 	final String titleString =  "dc:title";
	 private 	final String creatorString = "dc:creator";
	 private	final String languageString = 	"dc:language";
	 private	final String identifierString = "dc:identifier";
	 private	final String subjectString = "dc:subject";
	 private	final String rightsString = "dc:rights";
	 private	final String publisherString =	"dc:publisher";
	 private	final String dateString = "dc:date";
	 private	final String coverTagString= "cover";
	 private	final String pdfTagString= "pdf";
	 private	final String coverMinitypeString = "media-type";
	 private	final String opfPathString = "OPFPath";
	 
	public	String getTitleString(){
		return getInfo(titleString);
	}
	public String getCreatorString(){
		return getInfo(creatorString);
	}
	public	String getLanguageString(){
		return getInfo(languageString);
	}
	public	String getIdentifierString(){
		return getInfo(identifierString);
	}
	public String getSubjectString(){
		return getInfo(subjectString);
	}
	public	String getRightsString(){
		return getInfo(rightsString);
	}
	public String getPublisherString(){
		return getInfo(publisherString);
	}
	public	String getDateString(){
		return getInfo(dateString);
	}
	public	String getCoverMinitype(){
		return getInfo(coverMinitypeString);
	}
	public InputStream getCover(){
		return getStreamNative(mfileNameString, getHref(coverTagString));
	}
	public InputStream getpdf() throws UnsupportedEncodingException{
		return getStreamNative(mfileNameString,getHref(pdfTagString));
	}
	public InputStream getProduct(){
		String opfString = getOPFPath();
		int index;
		if((index = opfString.lastIndexOf("/")) != -1){
			return getStreamNative(mfileNameString, opfString.substring(0, index + 1) + "/product.xml");
		}
		return getStreamNative(mfileNameString , "product.xml");
	}
	public void  unzipPDF(String to){
		unZipFile(mfileNameString, getHref(pdfTagString), to);
	}
	
	public void  unzipConver(String to){
		unZipFile(mfileNameString, getHref(coverTagString), to);
	}
	private	String getHref(String strTag){
		int index;
		if((index = getInfo(opfPathString).lastIndexOf("/")) != -1){
			if(!(getInfo(strTag) == "")){
				return getInfo(opfPathString).substring(0, index + 1) + getInfo(strTag);
			}
			return "";
		}
		else {
			return getInfo(strTag);	
		}
	}
	
	private	String getOPFPath(){
		return getInfo(opfPathString);
	}
	
	private String getInfo(String info){
		if(mBookInf == null)
			return "";
		for(int i = 0; i < mBookInf.length; i++){
			if(mBookInf[i].mTag.compareTo(info) == 0)
				return mBookInf[i].mValue;
		}
		return "";
	}
	private openzip(){}
	private native info[] openZip(String file);
	private native InputStream getStreamNative(String file,String Href);
	private native void unZipFile(String file, String from,String to);
	private info mBookInf[];
}