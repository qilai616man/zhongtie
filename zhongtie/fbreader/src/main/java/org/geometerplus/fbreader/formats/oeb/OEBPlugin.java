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

package org.geometerplus.fbreader.formats.oeb;

import android.util.Log;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.formats.JavaFormatPlugin;
import org.geometerplus.zlibrary.core.encodings.AutoEncodingCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

import java.util.ArrayList;

public class OEBPlugin extends JavaFormatPlugin {
	
	private ArrayList<String> myHtmlFileNames = new ArrayList<String>();
	
	public OEBPlugin() {
		super("ePub");
	}
	//2013年11月29日 改为public
	@Override
	public ZLFile getOpfFile(ZLFile oebFile) throws BookReadingException {
		if ("opf".equals(oebFile.getExtension())) {
			return oebFile;
		}

		final ZLFile containerInfoFile = ZLFile.createFile(oebFile, "META-INF/container.xml");
		if (containerInfoFile.exists()) {
			final ContainerFileReader reader = new ContainerFileReader();
			reader.readQuietly(containerInfoFile);
			final String opfPath = reader.getRootPath();
			if (opfPath != null) {
				return ZLFile.createFile(oebFile, opfPath);
			}
		}

		for (ZLFile child : oebFile.children()) {
			if (child.getExtension().equals("opf")) {
				return child;
			}
		}
		throw new BookReadingException("opfFileNotFound", oebFile);
	}
	@Override
	public ZLFile getProductXmlFile(ZLFile oebFile,String parentPath) throws BookReadingException {
		ZLFile productXml = ZLFile.createFile(oebFile, parentPath + "/product.xml");
		return productXml;
	}
	@Override
	public void readMetaInfo(Book book) throws BookReadingException {
		new OEBMetaInfoReader(book).readMetaInfo(getOpfFile(book.File));
	}

	@Override
	public void readUids(Book book) throws BookReadingException {
		// this method does nothing, we expect it will be never called
	}
	
	/**
	 * 实例化图书模型数据
	 * @param model
	 * @throws BookReadingException
	 */
	@Override
	public void readModel(BookModel model) throws BookReadingException {
		readModel(model, "", "");
	}
	/**
	 * 实例化图书模型数据，支持解密
	 * @param model
	 * @param aesKey 解密密钥 解密product.xml
	 * @throws BookReadingException
	 */
	@Override
	public void readModel(BookModel model,String aesKey, String stylesheetPath) throws BookReadingException {

		model.Book.File.setCached(true);
		//2013年11月29日 修改
		OEBBookReader reader = new OEBBookReader(model);
		//获取opf
		ZLFile opfFile = getOpfFile(model.Book.File);
		Log.d("内容KEY为", aesKey);
		reader.readBook(opfFile,aesKey);
		myHtmlFileNames = reader.getMyHtmlFileNames();
	}

	//2013年11月29日 加
	@Override
	public ArrayList<String> getMyHtmlFileNames(){
		return myHtmlFileNames;
	} 
	
	@Override
	public ZLImage readCover(ZLFile file) {
		try {
			return new OEBCoverReader().readCover(getOpfFile(file));
		} catch (BookReadingException e) {
			return null;
		}
	}

	@Override
	public String readAnnotation(ZLFile file) {
		try {
			return new OEBAnnotationReader().readAnnotation(getOpfFile(file));
		} catch (BookReadingException e) {
			return null;
		}
	}

	@Override
	public AutoEncodingCollection supportedEncodings() {
		return new AutoEncodingCollection();
	}

	@Override
	public void detectLanguageAndEncoding(Book book) {
		book.setEncoding("auto");
	}
}