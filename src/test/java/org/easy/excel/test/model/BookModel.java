package org.easy.excel.test.model;

/**
 * 测试图书类
 * @author lisuo
 *
 */
public class BookModel {
	
	/** 图书名称 */
	private String bookName;
	/** 图书价格 */
	private Double price;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "BookModel [bookName=" + bookName + ", price=" + price + ", author=" + author + "]";
	}

	
	
	
	
}
