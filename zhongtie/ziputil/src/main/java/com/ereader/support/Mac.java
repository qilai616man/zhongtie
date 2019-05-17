package com.ereader.support;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.Set;

public class Mac {
	
	@XStreamImplicit(itemFieldName="value")
	private Set<String> value;

	public Set<String> getValue() {
		return value;
	}

	public void setValue(Set<String> value) {
		this.value = value;
	}
}
