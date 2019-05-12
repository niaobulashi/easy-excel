package org.easy.excel.test.model;

import java.util.Date;

/**
 * 测试学生类
 * 
 * @author lisuo
 *
 */
public class StudentModel {

	/** ID */
	protected String id;
	/** 创建时间 */
	protected Date createTime;
	/** 姓名 */
	private String name;
	/** 年龄 */
	private Integer age;
	/** 学号 */
	private String studentNo;
	/** 创建人 */
	private String createUser;
	/** 创建人ID */
	private String createUserId;
	/** 状态 */
	private Integer status;
	/** 图书信息 */
	private BookModel book;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getStudentNo() {
		return studentNo;
	}
	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public BookModel getBook() {
		return book;
	}
	public void setBook(BookModel book) {
		this.book = book;
	}
	@Override
	public String toString() {
		return "StudentModel [id=" + id + ", createTime=" + createTime + ", name=" + name + ", age=" + age
				+ ", studentNo=" + studentNo + ", createUser=" + createUser + ", createUserId=" + createUserId
				+ ", status=" + status + ", book=" + book + "]";
	}

	
}
