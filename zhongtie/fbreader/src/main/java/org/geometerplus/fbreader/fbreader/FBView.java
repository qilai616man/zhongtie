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

package org.geometerplus.fbreader.fbreader;

import java.util.ArrayList;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.image.ImageViewActivity;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.FBHyperlinkType;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.options.PageTurningOptions;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.filesystem.ZLResourceFile;
import org.geometerplus.zlibrary.core.image.ZLFileImage;
import org.geometerplus.zlibrary.core.image.ZLImageData;
import org.geometerplus.zlibrary.core.image.ZLImageManager;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.core.view.ZLPaintContext;
import org.geometerplus.zlibrary.text.model.ZLTextModel;
import org.geometerplus.zlibrary.text.view.ZLTextHighlighting;
import org.geometerplus.zlibrary.text.view.ZLTextHyperlink;
import org.geometerplus.zlibrary.text.view.ZLTextHyperlinkRegionSoul;
import org.geometerplus.zlibrary.text.view.ZLTextImageElement;
import org.geometerplus.zlibrary.text.view.ZLTextImageRegionSoul;
import org.geometerplus.zlibrary.text.view.ZLTextRegion;
import org.geometerplus.zlibrary.text.view.ZLTextSelectionCursor;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordRegionSoul;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.brainsoon.utils.BookUtils;

public final class FBView extends ZLTextView {
	private FBReaderApp myReader;

	FBView(FBReaderApp reader) {
		super(reader);
		myReader = reader;
	}

	public void setModel(ZLTextModel model) {
		super.setModel(model);
		if (myFooter != null) {
			myFooter.resetTOCMarks();
		}
	}

	private int myStartY;
	private boolean myIsBrightnessAdjustmentInProgress;
	private int myStartBrightness;

	private TapZoneMap myZoneMap;

	private TapZoneMap getZoneMap() {
		final PageTurningOptions prefs = myReader.PageTurningOptions;
		String id = prefs.TapZoneMap.getValue();
		if ("".equals(id)) {
			id = prefs.Horizontal.getValue() ? "right_to_left" : "up";
		}
		if (myZoneMap == null || !id.equals(myZoneMap.Name)) {
			myZoneMap = TapZoneMap.zoneMap(id);
		}
		return myZoneMap;
	}
	/**
	 * 2018.03.21  ymd  将单击图片事件取消，移动到长按点击事件中
	 * @return
	 */
	public boolean onFingerSingleTap(int x, int y) {
		if (super.onFingerSingleTap(x, y)) {
			return true;
		}

		final ZLTextRegion region = findRegion(x, y, MAX_SELECTION_DISTANCE,
				ZLTextRegion.HyperlinkFilter);
		if (region != null) {
			selectRegion(region);
			myReader.getViewWidget().reset();
			myReader.getViewWidget().repaint();
			myReader.runAction(ActionCode.PROCESS_HYPERLINK);
			return true;
		}

		final ZLTextHighlighting highlighting = findHighlighting(x, y,
				MAX_SELECTION_DISTANCE);
		if (highlighting instanceof BookmarkHighlighting) {
			myReader.runAction(ActionCode.SELECTION_BOOKMARK,
					((BookmarkHighlighting) highlighting).Bookmark);
			return true;
		}

		if (BookUtils.isSelected) {
			BookUtils.isSelected = false;
			ZLApplication.Instance().runAction(ActionCode.SELECTION_CLEAR);
			ZLApplication.Instance().runAction(ActionCode.SELECTION_HIDE_PANEL);
			return true;
		}

		// 单击图片事件
//		final ZLTextRegion openImgRegion = findRegion(x, y,
//				MAX_SELECTION_DISTANCE, ZLTextRegion.AnyRegionFilter);
//
//		if (openImgRegion != null) {
//			final ZLTextRegion.Soul soul = openImgRegion.getSoul();
//			if (soul instanceof ZLTextImageRegionSoul) {
//				if (myReader.ImageTappingActionOption.getValue() == FBReaderApp.ImageTappingAction.openImageView) {
//					ZLTextImageElement imageElement = ((ZLTextImageRegionSoul) soul).ImageElement;
//					String url = imageElement.URL;
//					Uri uri = Uri.parse(url);
//					Context mFBContext = BookUtils.mFBContext;
//					Intent intent = new Intent(mFBContext,
//							ImageViewActivity.class);
//					intent.setData(uri);
//					mFBContext.startActivity(intent);
//					return true;
//				}
//			}
//		}


		myReader.runAction(
				getZoneMap()
						.getActionByCoordinates(
								x,
								y,
								getContextWidth(),
								getContextHeight(),
								isDoubleTapSupported() ? TapZoneMap.Tap.singleNotDoubleTap
										: TapZoneMap.Tap.singleTap), x, y);

		return true;
	}

	@Override
	public boolean isDoubleTapSupported() {
		return myReader.EnableDoubleTapOption.getValue();
	}

	@Override
	public boolean onFingerDoubleTap(int x, int y) {
		if (super.onFingerDoubleTap(x, y)) {
			return true;
		}
		myReader.runAction(
				getZoneMap().getActionByCoordinates(x, y, getContextWidth(),
						getContextHeight(), TapZoneMap.Tap.doubleTap), x, y);
		return true;
	}

	public boolean onFingerPress(int x, int y) {
		if (super.onFingerPress(x, y)) {
			return true;
		}

		final ZLTextSelectionCursor cursor = findSelectionCursor(x, y,
				MAX_SELECTION_DISTANCE);
		if (cursor != ZLTextSelectionCursor.None) {
			myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
			moveSelectionCursorTo(cursor, x, y);
			return true;
		}

		if (myReader.AllowScreenBrightnessAdjustmentOption.getValue()
				&& x < getContextWidth() / 10) {
			myIsBrightnessAdjustmentInProgress = true;
			myStartY = y;
			myStartBrightness = ZLibrary.Instance().getScreenBrightness();
			return true;
		}

		startManualScrolling(x, y);
		return true;
	}

	private boolean isFlickScrollingEnabled() {
		final PageTurningOptions.FingerScrollingType fingerScrolling = myReader.PageTurningOptions.FingerScrolling
				.getValue();
		return fingerScrolling == PageTurningOptions.FingerScrollingType.byFlick
				|| fingerScrolling == PageTurningOptions.FingerScrollingType.byTapAndFlick;
	}

	private void startManualScrolling(int x, int y) {
		if (!isFlickScrollingEnabled()) {
			return;
		}

		final boolean horizontal = myReader.PageTurningOptions.Horizontal
				.getValue();
		final Direction direction = horizontal ? Direction.rightToLeft
				: Direction.up;
		myReader.getViewWidget().startManualScrolling(x, y, direction);
	}

	public boolean onFingerMove(int x, int y) {
		if (super.onFingerMove(x, y)) {
			return true;
		}

		final ZLTextSelectionCursor cursor = getSelectionCursorInMovement();
		if (cursor != ZLTextSelectionCursor.None) {
			moveSelectionCursorTo(cursor, x, y);
			return true;
		}

		if (BookUtils.isSelected) {
			BookUtils.isSelected = false;
			ZLApplication.Instance().runAction(ActionCode.SELECTION_CLEAR);
			ZLApplication.Instance().runAction(ActionCode.SELECTION_HIDE_PANEL);
		}

		synchronized (this) {
			if (myIsBrightnessAdjustmentInProgress) {
				if (x >= getContextWidth() / 5) {
					myIsBrightnessAdjustmentInProgress = false;
					startManualScrolling(x, y);
				} else {
					final int delta = (myStartBrightness + 30) * (myStartY - y)
							/ getContextHeight();
					ZLibrary.Instance().setScreenBrightness(
							myStartBrightness + delta);
					return true;
				}
			}

			if (isFlickScrollingEnabled()) {
				myReader.getViewWidget().scrollManuallyTo(x, y);
			}
		}
		return true;
	}

	public boolean onFingerRelease(int x, int y) {
		if (super.onFingerRelease(x, y)) {
			return true;
		}

		final ZLTextSelectionCursor cursor = getSelectionCursorInMovement();
		if (cursor != ZLTextSelectionCursor.None) {
			releaseSelectionCursor();
			return true;
		}

		if (myIsBrightnessAdjustmentInProgress) {
			myIsBrightnessAdjustmentInProgress = false;
			return true;
		}

		if (isFlickScrollingEnabled()) {
			myReader.getViewWidget().startAnimatedScrolling(x, y,
					myReader.PageTurningOptions.AnimationSpeed.getValue());
			return true;
		}

		return true;
	}
	/**
	 * 2018.03.21  ymd  将长按图片打开添加到方法中
	 * @return
	 */
	public boolean onFingerLongPress(int x, int y) {
		Log.i("longPress", "FBView--longPress");
		if (super.onFingerLongPress(x, y)) {
			return true;
		}
        final ZLTextRegion openImgRegion = findRegion(x, y,
                MAX_SELECTION_DISTANCE, ZLTextRegion.AnyRegionFilter);

        if (openImgRegion != null) {
            final ZLTextRegion.Soul soul = openImgRegion.getSoul();
            if (soul instanceof ZLTextImageRegionSoul) {
                if (myReader.ImageTappingActionOption.getValue() == FBReaderApp.ImageTappingAction.openImageView) {
                    ZLTextImageElement imageElement = ((ZLTextImageRegionSoul) soul).ImageElement;
                    String url = imageElement.URL;
                    Uri uri = Uri.parse(url);
                    Context mFBContext = BookUtils.mFBContext;
                    Intent intent = new Intent(mFBContext,
                            ImageViewActivity.class);
                    intent.setData(uri);
                    mFBContext.startActivity(intent);
                    return true;
                }
            }
        }
		final ZLTextRegion region = findRegion(x, y, MAX_SELECTION_DISTANCE,
				ZLTextRegion.AnyRegionFilter);
		if (region != null) {
			final ZLTextRegion.Soul soul = region.getSoul();
			boolean doSelectRegion = false;
			if (soul instanceof ZLTextWordRegionSoul) {
				switch (myReader.WordTappingActionOption.getValue()) {
				case startSelecting:
					// 选中文本
					myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
					initSelection(x, y);
					final ZLTextSelectionCursor cursor = findSelectionCursor(x,
							y);
					if (cursor != ZLTextSelectionCursor.None) {
						moveSelectionCursorTo(cursor, x, y);
					}
					return true;
				case selectSingleWord:
				case openDictionary:
					doSelectRegion = true;
					break;
				}
			} else if (soul instanceof ZLTextImageRegionSoul) {
				// 添加内容
				if (myReader.ImageTappingActionOption.getValue() == FBReaderApp.ImageTappingAction.openImageView) {
					ZLTextImageElement imageElement = ((ZLTextImageRegionSoul) soul).ImageElement;
					String url = imageElement.URL;
					Uri uri = Uri.parse(url);
					// Context mFBContext = BookUtils.mFBContext;
					// Intent intent = new Intent(mFBContext,
					// ImageViewActivity.class);
					// intent.setData(uri);
					// mFBContext.startActivity(intent);

					Bitmap myBitmap = null;
					if (ZLFileImage.SCHEME.equals(uri.getScheme())) {
						final ZLFileImage image = ZLFileImage.byUrlPath(uri
								.getPath());
						if (image != null) {
							try {
								final ZLImageData imageData = ZLImageManager
										.Instance().getImageData(image);
								myBitmap = ((ZLAndroidImageData) imageData)
										.getFullSizeBitmap();
								if (myBitmap!=null){
//									BookUtils.shareBitmap=myBitmap;
//									myReader.runAction(ActionCode.IMAGESHARE);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				// 原有内容
				doSelectRegion = myReader.ImageTappingActionOption.getValue() != FBReaderApp.ImageTappingAction.doNothing;
			} else if (soul instanceof ZLTextHyperlinkRegionSoul) {
				doSelectRegion = true;
			}

			if (doSelectRegion) {
				selectRegion(region);
				myReader.getViewWidget().reset();
				myReader.getViewWidget().repaint();
				return true;
			}
		}

		return false;
	}

	public boolean onFingerMoveAfterLongPress(int x, int y) {
		if (super.onFingerMoveAfterLongPress(x, y)) {
			return true;
		}

		final ZLTextSelectionCursor cursor = getSelectionCursorInMovement();
		if (cursor != ZLTextSelectionCursor.None) {
			moveSelectionCursorTo(cursor, x, y);
			return true;
		}

		ZLTextRegion region = getSelectedRegion();
		if (region != null) {
			ZLTextRegion.Soul soul = region.getSoul();
			if (soul instanceof ZLTextHyperlinkRegionSoul
					|| soul instanceof ZLTextWordRegionSoul) {
				if (myReader.WordTappingActionOption.getValue() != FBReaderApp.WordTappingAction.doNothing) {
					region = findRegion(x, y, MAX_SELECTION_DISTANCE,
							ZLTextRegion.AnyRegionFilter);
					if (region != null) {
						soul = region.getSoul();
						if (soul instanceof ZLTextHyperlinkRegionSoul
								|| soul instanceof ZLTextWordRegionSoul) {
							selectRegion(region);
							myReader.getViewWidget().reset();
							myReader.getViewWidget().repaint();
						}
					}
				}
			}
		}
		return true;
	}

	public boolean onFingerReleaseAfterLongPress(int x, int y) {
		if (super.onFingerReleaseAfterLongPress(x, y)) {
			return true;
		}

		final ZLTextSelectionCursor cursor = getSelectionCursorInMovement();
		if (cursor != ZLTextSelectionCursor.None) {
			releaseSelectionCursor();
			return true;
		}

		final ZLTextRegion region = getSelectedRegion();
		if (region != null) {
			final ZLTextRegion.Soul soul = region.getSoul();

			boolean doRunAction = false;
			if (soul instanceof ZLTextWordRegionSoul) {
				doRunAction = myReader.WordTappingActionOption.getValue() == FBReaderApp.WordTappingAction.openDictionary;
			} else if (soul instanceof ZLTextImageRegionSoul) {
				doRunAction = myReader.ImageTappingActionOption.getValue() == FBReaderApp.ImageTappingAction.openImageView;
			}

			if (doRunAction) {
				myReader.runAction(ActionCode.PROCESS_HYPERLINK);
				return true;
			}
		}

		return false;
	}

	public boolean onTrackballRotated(int diffX, int diffY) {
		if (diffX == 0 && diffY == 0) {
			return true;
		}

		final Direction direction = (diffY != 0) ? (diffY > 0 ? Direction.down
				: Direction.up) : (diffX > 0 ? Direction.leftToRight
				: Direction.rightToLeft);

		new MoveCursorAction(myReader, direction).run();
		return true;
	}

	@Override
	public ImageFitting getImageFitting() {
		return myReader.FitImagesToScreenOption.getValue();
	}

	@Override
	public int getLeftMargin() {
		return myReader.LeftMarginOption.getValue();
	}

	@Override
	public int getRightMargin() {
		return myReader.RightMarginOption.getValue();
	}

	@Override
	public int getTopMargin() {
		return myReader.TopMarginOption.getValue();
	}

	@Override
	public int getBottomMargin() {
		return myReader.BottomMarginOption.getValue();
	}

	@Override
	public int getSpaceBetweenColumns() {
		return myReader.SpaceBetweenColumnsOption.getValue();
	}

	@Override
	public boolean twoColumnView() {
		return getContextHeight() <= getContextWidth()
				&& myReader.TwoColumnViewOption.getValue();
	}

	@Override
	public ZLFile getWallpaperFile() {
		final String filePath = myReader.getColorProfile().WallpaperOption
				.getValue();
		if ("".equals(filePath)) {
			return null;
		}

		final ZLFile file = ZLFile.createFileByPath(filePath);
		if (file == null || !file.exists()) {
			return null;
		}
		return file;
	}

	@Override
	public ZLPaintContext.WallpaperMode getWallpaperMode() {
		return getWallpaperFile() instanceof ZLResourceFile ? ZLPaintContext.WallpaperMode.TILE_MIRROR
				: ZLPaintContext.WallpaperMode.TILE;
	}

	@Override
	public ZLColor getBackgroundColor() {
		return myReader.getColorProfile().BackgroundOption.getValue();
	}

	@Override
	public ZLColor getSelectionBackgroundColor() {
		return myReader.getColorProfile().SelectionBackgroundOption.getValue();
	}

	@Override
	public ZLColor getSelectionForegroundColor() {
		return myReader.getColorProfile().SelectionForegroundOption.getValue();
	}

	@Override
	public ZLColor getTextColor(ZLTextHyperlink hyperlink) {
		final ColorProfile profile = myReader.getColorProfile();
		switch (hyperlink.Type) {
		default:
		case FBHyperlinkType.NONE:
			return profile.RegularTextOption.getValue();
		case FBHyperlinkType.INTERNAL:
			return myReader.Collection.isHyperlinkVisited(myReader.Model.Book,
					hyperlink.Id) ? profile.VisitedHyperlinkTextOption
					.getValue() : profile.HyperlinkTextOption.getValue();
		case FBHyperlinkType.EXTERNAL:
			return profile.HyperlinkTextOption.getValue();
		}
	}

	// 文字高亮
	@Override
	public ZLColor getHighlightingBackgroundColor() {
		return myReader.getColorProfile().HighlightingOption.getValue();
		// return new ZLColor(255, 0, 0);
	}

	private class Footer implements FooterArea {
		private Runnable UpdateTask = new Runnable() {
			public void run() {
				myReader.getViewWidget().repaint();
			}
		};

		private ArrayList<TOCTree> myTOCMarks;

		public int getHeight() {
			return myReader.FooterHeightOption.getValue();
		}

		public synchronized void resetTOCMarks() {
			myTOCMarks = null;
		}

		private final int MAX_TOC_MARKS_NUMBER = 100;

		private synchronized void updateTOCMarks(BookModel model) {
			myTOCMarks = new ArrayList<TOCTree>();
			TOCTree toc = model.TOCTree;
			if (toc == null) {
				return;
			}
			int maxLevel = Integer.MAX_VALUE;
			if (toc.getSize() >= MAX_TOC_MARKS_NUMBER) {
				final int[] sizes = new int[10];
				for (TOCTree tocItem : toc) {
					if (tocItem.Level < 10) {
						++sizes[tocItem.Level];
					}
				}
				for (int i = 1; i < sizes.length; ++i) {
					sizes[i] += sizes[i - 1];
				}
				for (maxLevel = sizes.length - 1; maxLevel >= 0; --maxLevel) {
					if (sizes[maxLevel] < MAX_TOC_MARKS_NUMBER) {
						break;
					}
				}
			}
			for (TOCTree tocItem : toc.allSubtrees(maxLevel)) {
				myTOCMarks.add(tocItem);
			}
		}

		public synchronized void paint(ZLPaintContext context) {
			final ZLFile wallpaper = getWallpaperFile();
			if (wallpaper != null) {
				context.clear(wallpaper, getWallpaperMode());
			} else {
				context.clear(getBackgroundColor());
			}

			final FBReaderApp reader = myReader;
			if (reader == null) {
				return;
			}
			final BookModel model = reader.Model;
			if (model == null) {
				return;
			}

//			FBReader.updateInfo();
			// final ZLColor bgColor = getBackgroundColor();
			// TODO: separate color option for footer color
			final ZLColor fgColor = getTextColor(ZLTextHyperlink.NO_LINK);
			final ZLColor fillColor = reader.getColorProfile().FooterFillOption
					.getValue();

			final int left = getLeftMargin();
			final int right = context.getWidth() - getRightMargin();
			final int height = getHeight();
			final int lineWidth = height <= 10 ? 1 : 2;
			final int delta = height <= 10 ? 0 : 1;
			context.setFont(reader.FooterOptions.Font.getValue(),
					height <= 10 ? height + 3 : height + 1, height > 10, false,
					false, false);

			final PagePosition pagePosition = FBView.this.pagePosition();

			final StringBuilder info = new StringBuilder();
			if (reader.FooterOptions.ShowProgress.getValue()) {
				info.append(pagePosition.Current);
				info.append("/");
				info.append(pagePosition.Total);
			}
			// if (reader.FooterOptions.ShowBattery.getValue()) {
			// if (info.length() > 0) {
			// info.append(" ");
			// }
			// info.append(reader.getBatteryLevel());
			// info.append("%");
			// }
			// if (reader.FooterOptions.ShowClock.getValue()) {
			// if (info.length() > 0) {
			// info.append(" ");
			// }
			// info.append(ZLibrary.Instance().getCurrentTimeString());
			// }

			// 系统自带进度？
			// final String infoString = info.toString();
			//
			// final int infoWidth = context.getStringWidth(infoString);
			//
			// // draw info text
			// context.setTextColor(fgColor);
			// context.drawString(right - infoWidth, height - delta,
			// infoString);
			//
			// // draw gauge
			// final int gaugeRight = right - (infoWidth == 0 ? 0 : infoWidth +
			// 10);
			// myGaugeWidth = gaugeRight - left - 2 * lineWidth;
			//
			// context.setLineColor(fgColor);
			// context.setLineWidth(lineWidth);
			// context.drawLine(left, lineWidth, left, height - lineWidth);
			// context.drawLine(left, height - lineWidth, gaugeRight, height -
			// lineWidth);
			// context.drawLine(gaugeRight, height - lineWidth, gaugeRight,
			// lineWidth);
			// context.drawLine(gaugeRight, lineWidth, left, lineWidth);
			//
			// final int gaugeInternalRight =
			// left + lineWidth + (int)(1.0 * myGaugeWidth *
			// pagePosition.Current / pagePosition.Total);
			//
			// context.setFillColor(fillColor);
			// context.fillRectangle(left + 1, height - 2 * lineWidth,
			// gaugeInternalRight, lineWidth + 1);
			//
			// if (reader.FooterOptions.ShowTOCMarks.getValue()) {
			// if (myTOCMarks == null) {
			// updateTOCMarks(model);
			// }
			// final int fullLength = sizeOfFullText();
			// for (TOCTree tocItem : myTOCMarks) {
			// TOCTree.Reference reference = tocItem.getReference();
			// if (reference != null) {
			// final int refCoord =
			// sizeOfTextBeforeParagraph(reference.ParagraphIndex);
			// final int xCoord =
			// left + 2 * lineWidth + (int)(1.0 * myGaugeWidth * refCoord /
			// fullLength);
			// context.drawLine(xCoord, height - lineWidth, xCoord, lineWidth);
			// }
			// }
			// }
		}

		// TODO: remove
		// int myGaugeWidth = 1;
		/*
		 * public int getGaugeWidth() { return myGaugeWidth; }
		 */

		/*
		 * public void setProgress(int x) { // set progress according to tap
		 * coordinate int gaugeWidth = getGaugeWidth(); float progress = 1.0f *
		 * Math.min(x, gaugeWidth) / gaugeWidth; int page = (int)(progress *
		 * computePageNumber()); if (page <= 1) { gotoHome(); } else {
		 * gotoPage(page); } myReader.getViewWidget().reset();
		 * myReader.getViewWidget().repaint(); }
		 */
	}

	private Footer myFooter;

	@Override
	public Footer getFooterArea() {
		if (myReader.ScrollbarTypeOption.getValue() == SCROLLBAR_SHOW_AS_FOOTER) {
			if (myFooter == null) {
				myFooter = new Footer();
				myReader.addTimerTask(myFooter.UpdateTask, 15000);
			}
		} else {
			if (myFooter != null) {
				myReader.removeTimerTask(myFooter.UpdateTask);
				myFooter = null;
			}
		}
		return myFooter;
	}

	/**
	 * 选中
	 */
	@Override
	protected void releaseSelectionCursor() {
		super.releaseSelectionCursor();
		// 选中文本菜单
		if (getCountOfSelectedWords() > 0) {
			BookUtils.isSelected = true;
			myReader.runAction(ActionCode.SELECTION_SHOW_PANEL);
		}
	}

	public String getSelectedText() {
		final TextBuildTraverser traverser = new TextBuildTraverser(this);
		if (!isSelectionEmpty()) {
			traverser.traverse(getSelectionStartPosition(),
					getSelectionEndPosition());
		}
		return traverser.getText();
	}

	public int getCountOfSelectedWords() {
		final WordCountTraverser traverser = new WordCountTraverser(this);
		if (!isSelectionEmpty()) {
			traverser.traverse(getSelectionStartPosition(),
					getSelectionEndPosition());
		}
		return traverser.getCount();
	}

	public static final int SCROLLBAR_SHOW_AS_FOOTER = 3;

	@Override
	public int scrollbarType() {
		return myReader.ScrollbarTypeOption.getValue();
	}

	@Override
	public Animation getAnimationType() {
		return myReader.PageTurningOptions.Animation.getValue();
	}
}
