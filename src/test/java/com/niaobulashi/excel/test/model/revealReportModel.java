package com.niaobulashi.excel.test.model;

/**
 * 测试学生类
 * 
 * @author lisuo
 *
 */
public class revealReportModel {

	// id
	private String id;

	// 项目名称
	private String projectName;

	// 项目简称
	private String projectShortName;

	// 项目来源
	private String projectSource;

	// 披露类型
	private String revealType;

	// 披露频率
	private String revealRate;

	// 披露日期期限
	private String revealDueTime;

	// 通知期限
	private String noticeDueTime;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectShortName() {
		return projectShortName;
	}

	public void setProjectShortName(String projectShortName) {
		this.projectShortName = projectShortName;
	}

	public String getProjectSource() {
		return projectSource;
	}

	public void setProjectSource(String projectSource) {
		this.projectSource = projectSource;
	}

	public String getRevealRate() {
		return revealRate;
	}

	public void setRevealRate(String revealRate) {
		this.revealRate = revealRate;
	}

	public String getRevealDueTime() {
		return revealDueTime;
	}

	public void setRevealDueTime(String revealDueTime) {
		this.revealDueTime = revealDueTime;
	}

	public String getNoticeDueTime() {
		return noticeDueTime;
	}

	public void setNoticeDueTime(String noticeDueTime) {
		this.noticeDueTime = noticeDueTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRevealType() {
		return revealType;
	}

	public void setRevealType(String revealType) {
		this.revealType = revealType;
	}

	@Override
	public String toString() {
		return "revealReportModel [id = " + id + ", projectName=" + projectName + ", projectShortName=" + projectShortName + ", projectSource=" + projectSource + ", revealType=" + revealType
				+ ", revealRate=" + revealRate + ", revealDueTime=" + revealDueTime + ", noticeDueTime=" + noticeDueTime + "]";
	}
}
