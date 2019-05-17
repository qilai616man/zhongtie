package com.ereader.support;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ro")
public class ProductRo {
	private String ro;

	public String getRo() {
		return ro;
	}

	public void setRo(String ro) {
		this.ro = ro;
	}
}
