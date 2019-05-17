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

package org.geometerplus.android.fbreader;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ZoomButton;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

import java.util.ArrayList;

abstract class ButtonsPopupPanel extends PopupPanel implements
		View.OnClickListener {
	class ActionButton extends ZoomButton {
		final String ActionId;
		final boolean IsCloseButton;

		ActionButton(Context context, String actionId, boolean isCloseButton) {
			super(context);
			ActionId = actionId;
			IsCloseButton = isCloseButton;
		}
	}

	private final ArrayList<ActionButton> myButtons = new ArrayList<ActionButton>();

	ButtonsPopupPanel(FBReaderApp fbReader) {
		super(fbReader);
	}

	protected void addButton(String actionId, boolean isCloseButton, int imageId) {
		final ActionButton button = new ActionButton(myWindow.getContext(),
				actionId, isCloseButton);
		button.setImageResource(imageId);

		myWindow.addView(button);
		button.setOnClickListener(this);
		myButtons.add(button);
	}

	protected void addTextView(String content, int oritation) {
		final float scale = myWindow.getContext().getResources()
				.getDisplayMetrics().density;
		//Log.i("wangjun","density==="+scale);
		int width =myWindow.getContext().getResources().getDisplayMetrics().widthPixels;
		//Log.i("wangjun","width==="+width);
//		int height = (int) (60 * scale + 0.5f);
		TextView text = new TextView(myWindow.getContext());
		text.setText(content);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		if (oritation == 0) {
			int leftMargin = (int) (22 * scale + 0.5f);
			int rightMargin = (int) (52 * scale + 0.5f);
			params.leftMargin = leftMargin;
			params.rightMargin = rightMargin;
		} else {
			int leftMargin = (int) (52 * scale + 0.5f);
			int rightMargin = (int) (22 * scale + 0.5f);
			params.leftMargin = leftMargin;
			params.rightMargin = rightMargin;
		}
		params.gravity=Gravity.CENTER;
		text.setLayoutParams(params);


	//设置文字大小 根据屏幕的密度和宽度进行调整字体适配
		int textSize = (int) (width/(scale*20) + 0.5f);
		text.setTextSize(textSize);
		text.setTextColor(Color.WHITE);
		myWindow.addView(text);
		if (oritation == 0) {
			text.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Application.runAction(ActionCode.FIND_PREVIOUS);
				}
			});
		} else {
			text.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Application.runAction(ActionCode.FIND_NEXT);
				}
			});
		}
	}

	@Override
	protected void update() {
		for (ActionButton button : myButtons) {
			button.setEnabled(Application.isActionEnabled(button.ActionId));
		}
	}

	public void onClick(View view) {
		final ActionButton button = (ActionButton) view;
		Application.runAction(button.ActionId);
		if (button.IsCloseButton) {
			storePosition();
			StartPosition = null;
			Application.hideActivePopup();
		}
	}
}
