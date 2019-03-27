package org.easy.excel.test;

import java.util.List;

import org.easy.excel.ExcelContext;
import org.easy.excel.result.ExcelImportResult;
import org.easy.excel.test.model.BookModel;
import org.easy.excel.test.model.StudentModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
/**
 * Excel导入测试
 * @author lisuo
 *
 */
public class ImportTest {
	
	// 测试时文件磁盘路径
	private static String path = "test-excel.xls";
	// 配置文件路径
	private static ExcelContext context = new ExcelContext("excel-config.xml");
	private static ExcelContext revealContext = new ExcelContext("raveal-config.xml");
	// Excel配置文件中配置的id
	private static String excelId = "student";
	private static String revealExcelId = "reveal";

	/**
	 * 导入Excel,使用了org.easy.excel.test.ExportTest.testExportCustomHeader()方法生成的Excel
	 * @throws Exception
	 */
	@Test
	public void testImport()throws Exception{
		ClassPathResource resource = new ClassPathResource(path);
		//第二个参数需要注意,它是指标题索引的位置,可能你的前几行并不是标题,而是其他信息,
		//比如数据批次号之类的,关于如何转换成javaBean,具体参考配置信息描述
		ExcelImportResult result = context.readExcel(excelId, 3, resource.getInputStream());
		System.out.println(result.getHeader());
		List<StudentModel> stus = result.getListBean();
		for(StudentModel stu:stus){
			System.out.println(stu);
			BookModel book = stu.getBook();
			System.out.println(book);
			if(book!=null){
				System.out.println(book.getAuthor());
			}
		}
		resource.getInputStream().close();
		//这种方式和上面的没有任何区别,底层方法默认标题索引为0
		//context.readExcel(excelId, fis);
	}
	
	
		
}
