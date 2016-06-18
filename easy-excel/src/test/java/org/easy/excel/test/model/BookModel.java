package org.easy.excel.test.model;

/**
 * 测试图书类
 * @author lisuo
 *
 */
public class BookModel {
	
	/** 图书名称 */
	private String bookName;
	/** 作者信息 */
	private AuthorModel author;

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public AuthorModel getAuthor() {
		return author;
	}

	public void setAuthor(AuthorModel author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "BookModel [bookName=" + bookName + ", author=" + author + "]";
	}
	
	
	
	
}
