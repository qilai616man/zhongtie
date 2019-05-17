package com.ereader.support;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class Contents {
	@XStreamImplicit(itemFieldName="content")
	private List<Content> contents;

	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> contents) {
		this.contents = contents;
	}
}
