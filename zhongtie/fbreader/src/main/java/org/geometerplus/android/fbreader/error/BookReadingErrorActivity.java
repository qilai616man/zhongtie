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

package org.geometerplus.android.fbreader.error;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.geometerplus.zlibrary.core.resources.ZLResource;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.error.ErrorKeys;
import org.geometerplus.zlibrary.ui.android.error.ErrorUtil;

/**
 * 
 * 阅读捕获错误
 * @author zuo
 *
 */
public class BookReadingErrorActivity extends Activity implements ErrorKeys {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.error_book_reading);

		final ZLResource resource = ZLResource.resource("error").getResource("bookReading");
		setTitle(resource.getResource("title").getValue());

		final TextView textView = (TextView)findViewById(R.id.error_book_reading_text);
		textView.setText(getIntent().getStringExtra(MESSAGE));

		final View buttonView = findViewById(R.id.error_book_reading_buttons);
		final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");
//		2013年12月18日 屏蔽
//		final Button okButton = (Button)buttonView.findViewById(R.id.ok_button);
//		okButton.setText(buttonResource.getResource("sendReport").getValue());
//		okButton.setOnClickListener(new Button.OnClickListener() {
//			public void onClick(View v) {
//				final Intent sendIntent = new Intent(Intent.ACTION_SEND);
//				sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "issues@fbreader.org" });
//				sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(STACKTRACE));
//				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "FBReader " + new ErrorUtil(BookReadingErrorActivity.this).getVersionName() + " book reading issue report");
//				sendIntent.setType("message/rfc822");
//				startActivity(sendIntent);
//				finish();
//			}
//		});

		final Button cancelButton = (Button)buttonView.findViewById(R.id.cancel_button);
		//2013年12月18日 直接改为确定 (buttonResource.getResource("cancel").getValue())
		cancelButton.setText("确定");
		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
