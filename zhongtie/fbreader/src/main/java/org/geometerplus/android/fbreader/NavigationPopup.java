/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
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

import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.R;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
/**
 * @author zuo
 *
 */
final class NavigationPopup {
	private PopupWindow myWindow;
	private ZLTextWordCursor myStartPosition = null;
	private ZLTextWordCursor myEndPosition = null;
	private final FBReaderApp myFBReader;
	private ImageButton myResetButton;
	private static Drawable imgProgressBackDisable;
	private static Drawable imgProgressBack;
	private static Drawable imgProgressForward;
	
	NavigationPopup(FBReaderApp fbReader) {
		myFBReader = fbReader;
	}

	public void runNavigation(FBReader activity, RelativeLayout root) {
		if(null == imgProgressBackDisable){
			imgProgressBackDisable = activity.getResources().getDrawable(R.drawable.progress_back_disable);
			imgProgressBack = activity.getResources().getDrawable(R.drawable.progress_back);
			imgProgressForward = activity.getResources().getDrawable(R.drawable.progress_forward);
		}
		createControlPanel(activity, root);
		//myStartPosition = new ZLTextWordCursor(myFBReader.getTextView().getStartCursor());
		myWindow.show();
		setupNavigation();
	}

	public void update() {
		if (myWindow != null) {
			setupNavigation();
		}
	}

	public void stopNavigation() {
		if (myWindow == null) {
			return;
		}
		myStartPosition = null;
		myEndPosition = null;
//		if (myStartPosition != null &&
//			!myStartPosition.equals(myFBReader.getTextView().getStartCursor())) {
			//myFBReader.addInvisibleBookmark(myStartPosition);
//		}
		myWindow.hide();
		myWindow = null;
	}
	private int startProgress;
	private int endProgress;
	public void createControlPanel(FBReader activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.BottomFlat);

		final View layout = activity.getLayoutInflater().inflate(R.layout.navigate, myWindow, false);

		final SeekBar slider = (SeekBar)layout.findViewById(R.id.navigation_slider);
		final TextView text = (TextView)layout.findViewById(R.id.navigation_text);
		
		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			private void gotoPage(int page) {
				final ZLTextView view = myFBReader.getTextView();
				if (page == 1) {
					view.gotoHome();
				} else {
					view.gotoPage(page);
				}
				myFBReader.getViewWidget().reset();
				myFBReader.getViewWidget().repaint();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				startProgress = seekBar.getProgress();
				myStartPosition = new ZLTextWordCursor(myFBReader.getTextView().getStartCursor());
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				endProgress = seekBar.getProgress();
				final int page = endProgress + 1;
				final int pagesNumber = seekBar.getMax() + 1;
				Log.d("导航条", "page:=="+page+"totols:=="+pagesNumber);
				gotoPage(page);
				text.setText(makeProgressText(page, pagesNumber));
				myEndPosition = new ZLTextWordCursor(myFBReader.getTextView().getStartCursor());
			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					endProgress = progress;
				}
			}
		});

		myResetButton = (ImageButton)layout.findViewById(R.id.navigation_reset_button);
		myResetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("click", "reset");
				//判断
//				ImageButton btn = (ImageButton)v;
//				if(btn.getDrawable() == imgProgressBack){
//					if (myStartPosition != null) {
//						myFBReader.getTextView().gotoPosition(myStartPosition);
//					}
//				}else{
//					if (myEndPosition != null) {
//						myFBReader.getTextView().gotoPosition(myEndPosition);
//					}
//				}
//				myFBReader.getViewWidget().reset();
//				myFBReader.getViewWidget().repaint();
//				update();
				//改为分享
			}
		});
		final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");
		//myResetButton.setText(buttonResource.getResource("resetPosition").getValue());

		myWindow.addView(layout);
	}

	private void setupNavigation() {
		final SeekBar slider = (SeekBar)myWindow.findViewById(R.id.navigation_slider);
		final TextView text = (TextView)myWindow.findViewById(R.id.navigation_text);

		final ZLTextView textView = myFBReader.getTextView();
		final ZLTextView.PagePosition pagePosition = textView.pagePosition();
		//Log.d("导航======", "max:"+slider.getMax()+"----"+"Total:"+(pagePosition.Total - 1) + "---" + "progress:"+slider.getProgress() + "----Current:" + (pagePosition.Current - 1));

		if (slider.getMax() != pagePosition.Total - 1 || slider.getProgress() != pagePosition.Current - 1) {
			slider.setMax(pagePosition.Total - 1);
			slider.setProgress(pagePosition.Current - 1);
			text.setText(makeProgressText(pagePosition.Current, pagePosition.Total));
		}
//		boolean isActive = myStartPosition != null;
//		myResetButton.setEnabled(isActive);
//		if(isActive){
//			ZLTextWordCursor current = myFBReader.getTextView().getStartCursor();
//			if(null != myEndPosition && myEndPosition.equals(current)){
//				myResetButton.setImageDrawable(imgProgressBack);
//			}else if(null != myStartPosition && myStartPosition.equals(current)){
//				myResetButton.setImageDrawable(imgProgressForward);
//			}
//		}else{
//			myResetButton.setImageDrawable(imgProgressBackDisable);
//		}
	}

	private String makeProgressText(int page, int pagesNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(page);
		builder.append(" / ");
		builder.append(pagesNumber);
//		final TOCTree tocElement = myFBReader.getCurrentTOCElement();
//		if (tocElement != null) {
//			builder.append("  ");
//			builder.append(tocElement.getText());
//		}
		return builder.toString();
	}
}
