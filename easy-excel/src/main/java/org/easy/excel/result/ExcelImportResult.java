package org.easy.excel.result;

import java.util.List;

/**
 * Excel导入结果
 * 
 * @author lisuo
 *
 */
public class ExcelImportResult {
	
	/** 头信息,标题行之前的数据,每行表示一个List<Object>,每个Object存放一个cell单元的值 */
	private List<List<Object>> header = null;

	/** JavaBean集合,从标题行下面解析的数据 */
	private List<?> listBean;

	public List<List<Object>> getHeader() {
		return header;
	}

	public void setHeader(List<List<Object>> header) {
		this.header = header;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getListBean() {
		return (List<T>) listBean;
	}

	public void setListBean(List<?> listBean) {
		this.listBean = listBean;
	}

}
