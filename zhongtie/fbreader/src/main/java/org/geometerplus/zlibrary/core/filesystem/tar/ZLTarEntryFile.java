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

package org.geometerplus.zlibrary.core.filesystem.tar;

import java.util.*;
import java.io.*;

import org.geometerplus.zlibrary.core.filesystem.*;

import com.ereader.aesdecrypt.AesDecrypt;

import android.util.Log;

public final class ZLTarEntryFile extends ZLArchiveEntryFile {
	public static List<ZLFile> archiveEntries(ZLFile archive) {
		try {
			InputStream stream = archive.getInputStream();
			if (stream != null) {
				LinkedList<ZLFile> entries = new LinkedList<ZLFile>();
				ZLTarHeader header = new ZLTarHeader();
				while (header.read(stream)) {
					if (header.IsRegularFile) {
						entries.add(new ZLTarEntryFile(archive, header.Name));
					}
					final int lenToSkip = (header.Size + 0x1ff) & -0x200;
					if (lenToSkip < 0) {
						break;
					}
					if (stream.skip(lenToSkip) != lenToSkip) {
						break;
					}
					header.erase();
				}
				stream.close();
				return entries;
			}
		} catch (IOException e) {
		}
		return Collections.emptyList();
	}

	public ZLTarEntryFile(ZLFile parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean exists() {
		// TODO: optimize
		return myParent.exists() && archiveEntries(myParent).contains(this);
	}

	@Override
	public long size() {
		throw new RuntimeException("Not implemented yet.");
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Log.d("======读取流文件======", myName);
		InputStream in = new ZLTarInputStream(myParent.getInputStream(), myName);
		String contentKey = getAesKey();
		if(null !=contentKey && !contentKey.equals("") && getNeedDecryption()){//判断是否需要解密,如果密钥为空,则不解密
			Log.d("开始流文件解密,密钥为", contentKey);
			in =  AesDecrypt.aesDecrypt(getAesKey(), in);
		}
		return in;
	}
}
