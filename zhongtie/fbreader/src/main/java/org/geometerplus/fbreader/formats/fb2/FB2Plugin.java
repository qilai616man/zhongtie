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

package org.geometerplus.fbreader.formats.fb2;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.formats.JavaFormatPlugin;
import org.geometerplus.zlibrary.core.encodings.AutoEncodingCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

import java.util.ArrayList;

public class FB2Plugin extends JavaFormatPlugin {
	public FB2Plugin() {
		super("fb2");
	}

	@Override
	public ZLFile realBookFile(ZLFile file) throws BookReadingException {
		final ZLFile realFile = FB2Util.getRealFB2File(file);
		if (realFile == null) {
			throw new BookReadingException("incorrectFb2ZipFile", file);
		}
		return realFile;
	}

	@Override
	public void readMetaInfo(Book book) throws BookReadingException {
		new FB2MetaInfoReader(book).readMetaInfo();
	}

	@Override
	public void readUids(Book book) throws BookReadingException {
		// this method does nothing, we expect it will be never called
	}

	@Override
	public void readModel(BookModel model) throws BookReadingException {
		new FB2Reader(model).readBook();
	}

	@Override
	public ZLImage readCover(ZLFile file) {
		return new FB2CoverReader().readCover(file);
	}

	@Override
	public String readAnnotation(ZLFile file) {
		return new FB2AnnotationReader().readAnnotation(file);
	}

	@Override
	public AutoEncodingCollection supportedEncodings() {
		return new AutoEncodingCollection();
	}

	@Override
	public void detectLanguageAndEncoding(Book book) {
		book.setEncoding("auto");
	}

	@Override
	public ArrayList<String> getMyHtmlFileNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readModel(BookModel model, String aesKey, String stylesheetPath) throws BookReadingException {

		new FB2Reader(model).readBook();
	}

	@Override
	public ZLFile getOpfFile(ZLFile oebFile) throws BookReadingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ZLFile getProductXmlFile(ZLFile oebFile, String parentPath)
			throws BookReadingException {
		// TODO Auto-generated method stub
		return null;
	}
}