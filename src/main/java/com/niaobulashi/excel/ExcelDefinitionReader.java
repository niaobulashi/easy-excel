package com.niaobulashi.excel;

import java.util.Map;

import com.niaobulashi.excel.config.ExcelDefinition;

/**
 * Excel定义接口
 * @author niaobulashi
 *
 */
public interface ExcelDefinitionReader {
	/**
	 * 获取 ExcelDefinition注册信息
	 * @return
	 */
	Map<String, ExcelDefinition> getRegistry();
}
