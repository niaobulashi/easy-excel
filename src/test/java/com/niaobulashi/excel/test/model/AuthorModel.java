package com.niaobulashi.excel.test.model;

/**
 * 测试作者类
 * @author niaobulashi
 *
 */
public class AuthorModel {
	
	/** 作者姓名 */
	private String authorName;

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	@Override
	public String toString() {
		return "AuthorModel [authorName=" + authorName + "]";
	}
	
	
}
