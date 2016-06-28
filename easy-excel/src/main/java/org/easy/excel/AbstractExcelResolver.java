package org.easy.excel;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.easy.excel.vo.FieldValue;
import org.easy.excel.xml.ExcelDefinitionReader;
import org.easy.util.DateUtil;
import org.easy.util.ReflectUtil;
import org.easy.util.SpringUtil;


/**
 * Excel抽象解析器
 * 
 * @author lisuo
 *
 */
public abstract class AbstractExcelResolver implements ResolveFieldValueConverter{
	
	/** 日志 */
	protected Logger log = Logger.getLogger(this.getClass());

	protected ExcelDefinitionReader definitionReader;

	/** 注册字段解析信息 */
	private Map<String,ResolveFieldValueConverter> resolveFieldValueConverters = new HashMap<>();
	
	public AbstractExcelResolver(ExcelDefinitionReader definitionReader) {
		this.definitionReader = definitionReader;
	}

	/**
	 * 解析表达式format 属性
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	protected String resolverExpression(String value, String format, Type type) {
		try {
			String[] expressions = StringUtils.split(format, ",");
			for (String expression : expressions) {
				String[] val = StringUtils.split(expression, ":");
				String v1 = val[0];
				String v2 = val[1];
				if (Type.EXPORT == type) {
					if (value.equals(v1)) {
						return v2;
					}
				} else if (Type.IMPORT == type) {
					if (value.equals(v2)) {
						return v1;
					}
				}
			}
		} catch (Exception e) {
			log.error("表达式:" + format + "错误,正确的格式应该以[,]号分割,[:]取值");
			//throw new IllegalArgumentException("表达式:" + format + "错误,正确的格式应该以[,]号分割,[:]取值");
			return value;
		}
		return value;
	}

	/**
	 * 设置Cell单元的值
	 * 
	 * @param cell
	 * @param value
	 */
	protected void setCellValue(Cell cell, Object value) {
		if (value != null) {
			if (value instanceof String) {
				cell.setCellValue((String) value);
			} else if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (value instanceof Boolean) {
				cell.setCellValue((Boolean) value);
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
			} else {
				cell.setCellValue(value.toString());
			}
		}
	}

	/**
	 * 获取cell值
	 * 
	 * @param cell
	 * @return
	 */
	protected Object getCellValue(Cell cell) {
		Object value = null;
		if (null != cell) {
			switch (cell.getCellType()) {
			// 空白
			case Cell.CELL_TYPE_BLANK:
				break;
			// Boolean
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			// 错误格式
			case Cell.CELL_TYPE_ERROR:
				break;
			// 公式
			case Cell.CELL_TYPE_FORMULA:
				Workbook wb = cell.getSheet().getWorkbook();
				CreationHelper crateHelper = wb.getCreationHelper();
				FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
				value = getCellValue(evaluator.evaluateInCell(cell));
				break;
			// 数值
			case Cell.CELL_TYPE_NUMERIC:
				// 处理日期格式
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
					value = NumberToTextConverter.toText(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue().getString();
				break;
			default:
				value = null;
			}
		}
		return value;
	}
	
	//默认实现
	@Override
	public Object resolveFieldValue(Object bean,Object value, FieldValue fieldValue, Type type,int rowNum) throws Exception {
		if(value !=null){
			//解析器实现，读取数据
			String convName = fieldValue.getResolveFieldValueConverterName();
			if(convName==null){
				//执行默认
				String name = fieldValue.getName();
				String pattern = fieldValue.getPattern();
				String format = fieldValue.getFormat();
				if (StringUtils.isNotBlank(pattern)) {
					String [] patterns = StringUtils.split(pattern, ",");
					if (Type.EXPORT == type) {
						//导出使用第一个pattern
						return DateUtil.date2Str((Date) value, patterns[0]);
					} else if (Type.IMPORT == type) {
						if (value instanceof String) {
							Date date = DateUtil.tryStr2Date((String) value, patterns);
							if(date==null){
								StringBuilder errMsg = new StringBuilder("[");
								errMsg.append(value.toString()).append("]")
								.append("不能转换成日期,正确的格式应该是:[").append(pattern+"]");
								String err = getErrorMsg(fieldValue, errMsg.toString(), rowNum);
								throw new ExcelException(err);
							}
							return date;
						} else if (value instanceof Date) {
							return value;
						} else if(value instanceof Number){
							Number val = (Number) value;
							return new Date(val.longValue());
						} else {
							throw new ExcelException("数据格式错误,[ " + name + " ]的类型是:" + value.getClass()+",无法转换成日期");
						}
					}
				} else if (format != null) {
					return resolverExpression(value.toString(), format, type);
				} else {
					return value;
				}
			}else{
				//自定义
				ResolveFieldValueConverter conv = resolveFieldValueConverters.get(convName);
				if(conv == null){
					synchronized(this){
						if(conv == null){
							conv = getBean(convName);
							resolveFieldValueConverters.put(convName, conv);
						}
					}
					conv = resolveFieldValueConverters.get(convName);
				}
				value = conv.resolveFieldValue(bean,value, fieldValue, type, rowNum);
			}
		}
		return value;

	}
	
	//获取bean
	private ResolveFieldValueConverter getBean(String convName) throws ClassNotFoundException {
		ResolveFieldValueConverter bean = null;
		if(SpringUtil.isInited()){
			bean = (ResolveFieldValueConverter) SpringUtil.getBean(Class.forName(convName));
		}else{
			bean =  (ResolveFieldValueConverter) ReflectUtil.newInstance(Class.forName(convName));
		}
		return bean;
	}

	/**
	 * 解析配置中Field元素 处理后的值
	 * 
	 * @param value 原值
	 * @param fieldValue FieldValue信息
	 * @param type 导入或导出
	 * @return 解析结果对应的value
	 * @throws Exception
	 */
	protected Object resolveFieldValue(Object bean,Object value, FieldValue fieldValue, Type type) throws Exception {
		return this.resolveFieldValue(bean,value, fieldValue, type, 0);
	}

	/**
	 * 获取错误消息
	 * @param fieldValue
	 * @param errMsg 消息提示内容
	 * @param rowNum
	 * @return
	 */
	protected String getErrorMsg(FieldValue fieldValue,String errMsg,int rowNum){
		StringBuilder err = new StringBuilder();
		err.append("第[").append(rowNum).append("行],[")
		.append(fieldValue.getTitle()).append("]").append(errMsg);
		return err.toString();
	}
	
}
