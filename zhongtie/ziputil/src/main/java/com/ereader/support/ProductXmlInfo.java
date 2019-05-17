package com.ereader.support;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("product")
public class ProductXmlInfo {
	
	private String productId;
	
	private Mac mac;

	private Contents contents;

	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Mac getMac() {
		return mac;
	}
	public void setMac(Mac mac) {
		this.mac = mac;
	}
	public Contents getContents() {
		return contents;
	}
	public void setContents(Contents contents) {
		this.contents = contents;
	}
}
