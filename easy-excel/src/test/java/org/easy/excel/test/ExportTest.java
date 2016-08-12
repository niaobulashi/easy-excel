package org.easy.excel.test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.easy.excel.ExcelContext;
import org.easy.excel.ExcelExportResult;
import org.easy.excel.ExcelHeader;
import org.easy.excel.test.model.AuthorModel;
import org.easy.excel.test.model.BookModel;
import org.easy.excel.test.model.StudentModel;
import org.easy.excel.vo.ExcelDefinition;
import org.easy.excel.vo.FieldValue;
import org.junit.Test;

/**
 * Excel导出测试
 * @author lisuo
 *
 */
public class ExportTest {
	
	//测试时文件磁盘路径
	private static String path = "C:/Users/Administrator/Desktop/stu.xlsx";
	//配置文件路径
	private static ExcelContext context = new ExcelContext("excel-config.xml");
	//Excel配置文件中配置的id
	private static String excelId = "student";
	
	/***
	 * 导出测试,分多次导出
	 * @throws Exception
	 */
	@Test
	public void testExporPart()throws Exception{
		//需求概述.数据量较大,可能大批量数据导出,会对DB造成压力,这里分批次检索数据,一部分一部分向Excel中写
		OutputStream ops = new FileOutputStream(path);
		ExcelExportResult exportResult = context.createExcelForPart(excelId,getStudents());
		//假设这是第二次从数据库或其他平台查询到到数据
		exportResult.append(getStudents());
		//第n次....
		exportResult.append(getStudents());
		exportResult.append(getStudents());
		exportResult.append(getStudents());
		exportResult.append(getStudents());

		Workbook workbook = exportResult.build();
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	/***
	 * 导出测试,简单版
	 * @throws Exception
	 */
	@Test
	public void testExportSimple()throws Exception{
		OutputStream ops = new FileOutputStream(path);
		Workbook workbook = context.createExcel(excelId,getStudents());
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	/***
	 * 导出测试,自定义头信息
	 * @throws Exception
	 */
	@Test
	public void testExportCustomHeader()throws Exception{
		OutputStream ops = new FileOutputStream(path);
		final List<StudentModel> stus = getStudents();
		Workbook workbook = context.createExcel(excelId,stus,new ExcelHeader() {
			@Override
			public void buildHeader(Sheet sheet, ExcelDefinition excelDefinition, List<?> beans) {
				Row row1 = sheet.createRow(0);
				Cell cell = row1.createCell(0);
				cell.setCellValue("共导出【"+stus.size()+"】条数据");
				Row row2 = sheet.createRow(1);
				Cell cell2 = row2.createCell(0);
				cell2.setCellValue("本次批次号为:XXX");
			}
		});
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	/***
	 * 导出测试,指定导出字段
	 * @throws Exception
	 */
	@Test
	public void testExportSpecifyFields()throws Exception{
		OutputStream ops = new FileOutputStream(path);
		//注意,如果传的集合为null,默认导出所有字段,所以想要导出指定的字段集合一定不能为empty,
		//这里我指定导出id,name,age三个字段(以配置文件中的name属性为准,而不是标题)
		List<String> specifyFields = new ArrayList<String>();
		specifyFields.add("age");
		specifyFields.add("name");
		specifyFields.add("id");
		List<StudentModel> stus = getStudents();
		Workbook workbook = context.createExcel(excelId,stus,null,specifyFields);
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	/***
	 * 导出测试,指定导出字段,和头信息
	 * @throws Exception
	 */
	@Test
	public void testExport()throws Exception{
		OutputStream ops = new FileOutputStream(path);
		//注意,如果传的集合为null,默认导出所有字段,所以想要导出指定的字段集合一定不能为empty,
		//这里我指定导出id,name,age三个字段(以配置文件中的name属性为准,而不是标题)
		List<String> specifyFields = new ArrayList<String>();
		specifyFields.add("id");
		specifyFields.add("name");
		specifyFields.add("age");
		final List<StudentModel> stus = getStudents();
		Workbook workbook = context.createExcel(excelId,stus,new ExcelHeader() {
			
			@Override
			public void buildHeader(Sheet sheet, ExcelDefinition excelDefinition, List<?> beans) {
				Row row1 = sheet.createRow(0);
				Cell cell = row1.createCell(0);
				cell.setCellValue("共导出【"+stus.size()+"】条数据");
				Row row2 = sheet.createRow(1);
				Cell cell2 = row2.createCell(0);
				cell2.setCellValue("本次批次号为:XXX");
			}
		},specifyFields);
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	/**
	 * 获取配置信息测试
	 */
	@Test
	public void testGetFieldValues(){
		//获取配置过的所有字段,可能会有这种需求,列出所有可以导出的列,然后使用checkbox进行勾选,导出指定的列
		List<FieldValue> fieldValues = context.getFieldValues(excelId);
		for(FieldValue fv:fieldValues){
			System.out.println(fv.getName());
		}
	}
	
	/**
	 * 导出空数据模板示例1
	 * @throws Exception 
	 */
	@Test
	public void testCreateExcelTemplate1() throws Exception{
		OutputStream ops = new FileOutputStream(path);
		Workbook workbook = context.createExcelTemplate(excelId,null,null);
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	/**
	 * 导出空数据模板示例2
	 * @throws Exception 
	 */
	@Test
	public void testCreateExcelTemplate2() throws Exception{
		OutputStream ops = new FileOutputStream(path);
		//注意,如果传的集合为null,默认导出所有字段,所以想要导出指定的字段集合一定不能为empty,
		//这里我指定导出id,name,age三个字段(以配置文件中的name属性为准,而不是标题)
		List<String> specifyFields = new ArrayList<String>();
		specifyFields.add("age");
		specifyFields.add("name");
		specifyFields.add("id");
		//Workbook workbook = context.createExcelTemplate(excelId,null,specifyFields);
		//Workbook workbook = context.createExcelTemplate(excelId,null,null);
		Workbook workbook = context.createExcelTemplate(excelId,new ExcelHeader() {
			@Override
			public void buildHeader(Sheet sheet, ExcelDefinition excelDefinition, List<?> beans) {
				Row row1 = sheet.createRow(0);
				Cell cell = row1.createCell(0);
				cell.setCellValue("如果需要生成填写案例的数据,建议使用传统的方式,添加一条数据");
				Row row2 = sheet.createRow(1);
				Cell cell2 = row2.createCell(0);
				cell2.setCellValue("这种模板还是有弊端的,没有示例数据,用户不知道填写数据的格式等..");
			}
		},
			//specifyFields);//指定字段,可以打开注释测试
			null);//不指定字段
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	//获取模拟数据,数据库数据...
	public static List<StudentModel> getStudents(){
		int size = 5;
		List<StudentModel> students = new ArrayList<StudentModel>(size);
		for(int i=0;i<size;i++){
			StudentModel stu = new StudentModel();
			stu.setId(""+(i+1));
			stu.setName("张三"+i);
			stu.setAge(20+i);
			stu.setStudentNo("Stu_"+i);
			stu.setCreateTime(new Date());
			stu.setStatus(i%2==0?1:0);
			stu.setCreateUser("王五"+i);
			
			//创建复杂对象
			if(i % 2==0){
				BookModel book = new BookModel();
				book.setBookName("Thinking in java");
				AuthorModel author = new AuthorModel();
				author.setAuthorName("Bruce Eckel");
				book.setAuthor(author);
				stu.setBook(book);
			}
			
			students.add(stu);
		}
		return students;
	}
}
