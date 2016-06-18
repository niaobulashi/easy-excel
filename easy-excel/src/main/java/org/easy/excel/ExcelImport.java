package org.easy.excel;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.easy.excel.vo.ExcelDefinition;
import org.easy.excel.vo.ExcelImportResult;
import org.easy.excel.vo.FieldValue;
import org.easy.excel.xml.ExcelDefinitionReader;
import org.easy.util.ReflectUtil;
/**
 * Excel导入实现类
 * @author lisuo
 *
 */
public class ExcelImport extends AbstractExcelResolver{
	
	
	public ExcelImport(ExcelDefinitionReader definitionReader) {
		super(definitionReader);
	}
	
	/**
	 * 读取Excel信息
	 * @param id 注册的ID
	 * @param titleIndex 标题索引
	 * @param excelStream Excel文件流
	 * @return
	 * @throws Exception
	 */
	public ExcelImportResult readExcel(String id, int titleIndex,InputStream excelStream) throws Exception {
		//从注册信息中获取Bean信息
		ExcelDefinition excelDefinition = definitionReader.getRegistry().get(id);
		if(excelDefinition==null){
			throw new IllegalArgumentException("没有找到 ["+id+"] 的配置信息");
		}
		return doReadExcel(excelDefinition,titleIndex,excelStream);
	}
	
	protected ExcelImportResult doReadExcel(ExcelDefinition excelDefinition,int titleIndex,InputStream excelStream) throws Exception {
		Workbook workbook = WorkbookFactory.create(excelStream);
		ExcelImportResult result = new ExcelImportResult();
		Sheet sheet = workbook.getSheetAt(0);
		//标题之前的数据处理
		List<List<Object>> header = readHeader(excelDefinition, sheet,titleIndex);
		result.setHeader(header);
		//获取标题
		List<String> titles = readTitle(excelDefinition,sheet,titleIndex);
		//获取Bean
		List<Object> listBean = readRows(excelDefinition,titles, sheet,titleIndex);
		result.setListBean(listBean);
		return result;
		
	}
	
	/**
	 * 解析标题之前的内容,如果ExcelDefinition中titleIndex 不是0
	 * @param excelDefinition
	 * @param sheet
	 * @return
	 */
	protected List<List<Object>> readHeader(ExcelDefinition excelDefinition,Sheet sheet,int titleIndex){
		List<List<Object>> header = null;
		if(titleIndex!=0){
			header = new ArrayList<>(titleIndex);
			for(int i=0;i<titleIndex;i++){
				Row row = sheet.getRow(i);
				short cellNum = row.getLastCellNum();
				List<Object> item = new ArrayList<>(cellNum);
				for(int j=0;j<cellNum;j++){
					Cell cell = row.getCell(j);
					Object value = getCellValue(cell);
					item.add(value);
				}
				header.add(item);
			}
		}
		return header;
	}
	
	/**
	 * 读取多行
	 * @param excelDefinition
	 * @param titles
	 * @param sheet
	 * @param titleIndex
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> readRows(ExcelDefinition excelDefinition, List<String> titles, Sheet sheet,int titleIndex)throws Exception {
		int rowNum = sheet.getLastRowNum();
		//读取数据的总共次数
		int totalNum = rowNum - titleIndex;
		List<T> listBean = new ArrayList<>(totalNum);
		for (int i = titleIndex+1; i <= rowNum; i++) {
			Row row = sheet.getRow(i);
			Object bean = readRow(excelDefinition,row,titles,i);
			listBean.add((T) bean);
		}
		return listBean;
	}
	
	/**
	 * 读取1行
	 * @param excelDefinition
	 * @param row
	 * @param titles
	 * @param rowNum 第几行
	 * @return
	 * @throws Exception
	 */
	protected Object readRow(ExcelDefinition excelDefinition, Row row, List<String> titles,int rowNum) throws Exception {
		//创建注册时配置的bean类型
		Object bean = ReflectUtil.newInstance(excelDefinition.getClazz());
		for(FieldValue fieldValue:excelDefinition.getFieldValues()){
			String title = fieldValue.getTitle();
			for (int j = 0; j < titles.size(); j++) {
				if(title.equals(titles.get(j))){
					Cell cell = row.getCell(j);
					//获取Excel原生value值
					Object value = getCellValue(cell);
					//校验
					validate(fieldValue, value, rowNum);
					if(value != null){
						if(value instanceof String){
							//去除前后空格
							value = value.toString().trim();
						}
						value = super.resolveFieldValue(bean,value, fieldValue, Type.IMPORT,rowNum);
						ReflectUtil.setProperty(bean, fieldValue.getName(), value);
					}
					break;
				}
			}
		}
		return bean;
	}

	protected List<String> readTitle(ExcelDefinition excelDefinition, Sheet sheet,int titleIndex) {
		// 获取Excel标题数据
		Row hssfRowTitle = sheet.getRow(titleIndex);
		int cellNum = hssfRowTitle.getLastCellNum();
		List<String> titles = new ArrayList<String>(cellNum);
		// 获取标题数据
		for (int i = 0; i < cellNum; i++) {
			Cell cell = hssfRowTitle.getCell(i);
			Object value = getCellValue(cell);
			if(value==null){
				throw new IllegalArgumentException("id 为:["+excelDefinition.getId()+"]的标题不能为[ null ]");
			}
			titles.add(value.toString());
		}
		return titles;
	}
	
	/**
	 * 数据有效性校验
	 * @param fieldValue
	 * @param value
	 * @param rowNum
	 */
	private void validate(FieldValue fieldValue,Object value,int rowNum){
		if(value == null || StringUtils.isBlank(value.toString())){
			//空校验
			if(!fieldValue.isNull()){
				String err = getErrorMsg(fieldValue, "不能为空", rowNum);
				throw new ExcelException(err);
			}
		}else{
			//正则校验
			String regex = fieldValue.getRegex();
			if(StringUtils.isNotBlank(regex)){
				String val = value.toString().trim();
				if(!val.matches(regex)){
					String errMsg = fieldValue.getRegexErrMsg()==null?"格式错误":fieldValue.getRegexErrMsg();
					String err = getErrorMsg(fieldValue, errMsg, rowNum);
					throw new ExcelException(err);
				}
			}
		}
	}

	
}
