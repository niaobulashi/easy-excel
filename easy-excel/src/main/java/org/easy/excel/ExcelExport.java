package org.easy.excel;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.easy.excel.vo.ExcelDefinition;
import org.easy.excel.vo.FieldValue;
import org.easy.excel.xml.ExcelDefinitionReader;
import org.easy.util.ReflectUtil;

/**
 * Excel导出实现类
 * @author lisuo
 *
 */
public class ExcelExport extends AbstractExcelResolver{

	
	public ExcelExport(ExcelDefinitionReader definitionReader) {
		super(definitionReader);
	}

	/**
	 * 创建导出Excel,如果集合没有数据,返回null
	 * @param id	 ExcelXML配置Bean的ID
	 * @param beans  ExcelXML配置的bean集合
	 * @param header Excel头信息(在标题之前)
	 * @param fields 指定导出的字段
	 * @return
	 * @throws Exception
	 */
	public Workbook createExcel(String id,List<?> beans,ExcelHeader header,List<String> fields) throws Exception{
		Workbook workbook = null;
		if(CollectionUtils.isNotEmpty(beans)){
			//从注册信息中获取Bean信息
			ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
			if(excelDefinition==null){
				throw new IllegalArgumentException("没有找到 ["+id+"] 的配置信息");
			}
			if(excelDefinition.getClazz()==beans.get(0).getClass()){
				if(CollectionUtils.isNotEmpty(fields)){
					//导出指定字段的标题不是null,动态创建,Excel定义
					excelDefinition = dynamicCreateExcelDefinition(excelDefinition,fields);
				}
				workbook = doCreateExcel(excelDefinition,beans,header);
			}else{
				throw new IllegalArgumentException("传入的参数类型是:"+beans.get(0).getClass().getName()
						+"但是 配置文件的类型是: "+excelDefinition.getClazz().getName());
			}
		}
		return workbook;
	}
	
	/**
	 * 动态创建ExcelDefinition
	 * @param excelDefinition 原来的ExcelDefinition
	 * @param fields 
	 * @return
	 */
	private ExcelDefinition dynamicCreateExcelDefinition(ExcelDefinition excelDefinition, List<String> fields) {
		ExcelDefinition newDef = new ExcelDefinition();
		ReflectUtil.copyProps(excelDefinition, newDef,"fieldValues");
		List<FieldValue> oldValues = excelDefinition.getFieldValues();
		List<FieldValue> newValues = new ArrayList<>(oldValues.size());
		for(FieldValue field:oldValues){
			String fieldName = field.getName();
			if(fields.contains(fieldName)){
				newValues.add(field);
			}
		}
		newDef.setFieldValues(newValues);
		return newDef;
		
	}

	protected Workbook doCreateExcel(ExcelDefinition excelDefinition, List<?> beans,ExcelHeader header) throws Exception {
		// 创建Workbook
		Workbook workbook = new SXSSFWorkbook();
		Sheet sheet = null;
		if(excelDefinition.getSheetname()!=null){
			sheet = workbook.createSheet(excelDefinition.getSheetname());
		}else{
			sheet = workbook.createSheet();
		}
		//创建标题之前,调用buildHeader方法,完成其他数据创建的一些信息
		if(header!=null){
			header.buildHeader(sheet,excelDefinition,beans);
		}
		// 默认列宽设置
		if(excelDefinition.getDefaultColumnWidth()!=null)
			sheet.setDefaultColumnWidth(excelDefinition.getDefaultColumnWidth());
		
		createTitle(excelDefinition,sheet,workbook);
		createRows(excelDefinition, sheet, beans,workbook);
		return workbook;
	}

	/**
	 * 创建Excel标题
	 * @param excelDefinition
	 * @param sheet
	 */
	protected void createTitle(ExcelDefinition excelDefinition,Sheet sheet,Workbook workbook) {
		Row titleRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
		List<FieldValue> fieldValues = excelDefinition.getFieldValues();
		for(int i=0;i<fieldValues.size();i++){
			FieldValue fieldValue = fieldValues.get(i);
			//设置单元格宽度
			if(fieldValue.getColumnWidth() !=null){
				sheet.setColumnWidth(i, fieldValue.getColumnWidth());
			}
			Cell cell = titleRow.createCell(i);
			if(excelDefinition.getEnableStyle()){
				if(fieldValue.getAlign()!=null || fieldValue.getTitleBgColor()!=null || fieldValue.getTitleFountColor() !=null){
					cell.setCellStyle(workbook.createCellStyle());
					//设置cell 对齐方式
					setAlignStyle(fieldValue, workbook, cell);
					//设置标题背景色
					setTitleBgColorStyle(fieldValue, workbook, cell);
					//设置标题字体色
					setTitleFountColorStyle(fieldValue, workbook, cell);
				}
			}
			setCellValue(cell,fieldValue.getTitle());
		}
	}
	
	/**
	 * 创建行
	 * @param excelDefinition
	 * @param sheet
	 * @param beans
	 * @throws Exception
	 */
	protected void createRows(ExcelDefinition excelDefinition,Sheet sheet,List<?> beans,Workbook workbook) throws Exception{
		int num = sheet.getPhysicalNumberOfRows();
		for(int i=0;i<beans.size();i++){
			Row row = sheet.createRow(i+num);
			createRow(excelDefinition,row,beans.get(i),workbook);
		}
	}
	
	
	/**
	 * 创建行
	 * @param excelDefinition
	 * @param row
	 * @param bean
	 * @throws Exception
	 */
	protected void createRow(ExcelDefinition excelDefinition, Row row, Object bean,Workbook workbook) throws Exception {
		List<FieldValue> fieldValues = excelDefinition.getFieldValues();
		for(int i=0;i<fieldValues.size();i++){
			FieldValue fieldValue = fieldValues.get(i);
			String name = fieldValue.getName();
			Object value = ReflectUtil.getProperty(bean, name);
			//从解析器获取值
			Object val = resolveFieldValue(bean,value, fieldValue, Type.EXPORT);
			Cell cell = row.createCell(i);
			//所有cell都按照align进行居中,考虑到性能问题,不开启此功能
//			if(excelDefinition.getEnableStyle()){
//				if(fieldValue.getAlign()!=null){
//					cell.setCellStyle(workbook.createCellStyle());
//					setAlignStyle(fieldValue, workbook, cell);
//				}
//			}
			setCellValue(cell, val);
		}
	}
	
	//设置cell 对齐方式
	private void setAlignStyle(FieldValue fieldValue,Workbook workbook,Cell cell){
		if(fieldValue.getAlign()!=null){
			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setAlignment(fieldValue.getAlign());
			cell.setCellStyle(cellStyle);
		}
	}
	
	//设置cell 背景色方式
	private void setTitleBgColorStyle(FieldValue fieldValue,Workbook workbook,Cell cell){
		if(fieldValue.getTitleBgColor()!=null){
			CellStyle cellStyle = cell.getCellStyle();
			cellStyle.setFillForegroundColor(fieldValue.getTitleBgColor());
			cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}
	}
	
	//设置cell 字体颜色
	private void setTitleFountColorStyle(FieldValue fieldValue,Workbook workbook,Cell cell){
		if(fieldValue.getTitleFountColor()!=null){
			CellStyle cellStyle = cell.getCellStyle();
			Font font = workbook.createFont();
			font.setColor(fieldValue.getTitleFountColor());
			cellStyle.setFont(font);
		}
	}
	
	
}
