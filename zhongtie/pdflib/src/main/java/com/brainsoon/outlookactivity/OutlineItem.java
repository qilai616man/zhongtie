package com.brainsoon.outlookactivity;

import java.io.Serializable;

public class OutlineItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1055291225976136963L;
	public final int    level;
	public final String title;
	public final String    index;

	OutlineItem(int _level, String _title, String _index) {
		level = _level;
		title = _title;
		index  = _index;
	}

}
