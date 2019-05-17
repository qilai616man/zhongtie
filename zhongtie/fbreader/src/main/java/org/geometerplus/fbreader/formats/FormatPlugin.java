/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.formats;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.encodings.EncodingCollection;
import org.geometerplus.zlibrary.core.image.ZLImage;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;

public abstract class FormatPlugin {
	private final String myFileType;

	protected FormatPlugin(String fileType) {
		myFileType = fileType;
	}

	public final String supportedFileType() {
		return myFileType;
	}

	public ZLFile realBookFile(ZLFile file) throws BookReadingException {
		return file;
	}
	public abstract void readMetaInfo(Book book) throws BookReadingException;
	public abstract void readUids(Book book) throws BookReadingException;
	public abstract void readModel(BookModel model) throws BookReadingException;
	/**
	 * 2013年12月12日 zuohl add 实例化图书模型数据
	 * @param aesKey 解密密钥 用于解密product.xml
	 * @throws BookReadingException
	 */
	public abstract void readModel(BookModel model,String aesKey, String stylesheetPath) throws BookReadingException;
	public abstract void detectLanguageAndEncoding(Book book) throws BookReadingException;
	public abstract ZLImage readCover(ZLFile file);
	public abstract String readAnnotation(ZLFile file);
	public abstract ArrayList<String> getMyHtmlFileNames();
	/**
	 * 2013年12月19日 add,对外透出此接口
	 * @param oebFile
	 * @return
	 * @throws BookReadingException
	 */
	public abstract ZLFile getOpfFile(ZLFile oebFile) throws BookReadingException;
	/**
	 * 2013年12月19日 zuohl add 获取解密密钥文件
	 * @param oebFile
	 * @param parentPath
	 * @return
	 * @throws BookReadingException
	 */
	public abstract ZLFile getProductXmlFile(ZLFile oebFile,String parentPath) throws BookReadingException;
	
	public enum Type {
		ANY,
		JAVA,
		NATIVE,
		EXTERNAL,
		NONE
	};
	public abstract Type type();

	public abstract EncodingCollection supportedEncodings();
}
