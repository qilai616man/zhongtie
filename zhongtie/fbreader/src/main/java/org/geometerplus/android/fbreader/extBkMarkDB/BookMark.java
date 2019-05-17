package org.geometerplus.android.fbreader.extBkMarkDB;

import java.util.Date;

/**
 * 自定义书签实体
 * @author zuo
 *
 */
public class BookMark {
	private Long _id;
	private String content;
	private Date createTime;
	private Long bookId;
	private int paragraphIndex;
	private int elementIndex;
	private int charIndex;
	private int startParagraphIndex;
	public Long get_id() {
		return _id;
	}
	public void set_id(Long _id) {
		this._id = _id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getBookId() {
		return bookId;
	}
	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
	public int getParagraphIndex() {
		return paragraphIndex;
	}
	public void setParagraphIndex(int paragraphIndex) {
		this.paragraphIndex = paragraphIndex;
	}
	public int getElementIndex() {
		return elementIndex;
	}
	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}
	public int getCharIndex() {
		return charIndex;
	}
	public void setCharIndex(int charIndex) {
		this.charIndex = charIndex;
	}
	public int getStartParagraphIndex() {
		return startParagraphIndex;
	}
	public void setStartParagraphIndex(int startParagraphIndex) {
		this.startParagraphIndex = startParagraphIndex;
	}
}
