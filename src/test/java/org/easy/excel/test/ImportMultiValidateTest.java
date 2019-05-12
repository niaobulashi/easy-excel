package org.easy.excel.test;

import java.util.List;

import org.easy.excel.ExcelContext;
import org.easy.excel.parsing.ExcelError;
import org.easy.excel.result.ExcelImportResult;
import org.easy.excel.test.model.BookModel;
import org.easy.excel.test.model.StudentModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
/**
 * Excel导入多行校验测试
 * @author lisuo
 *
 */
public class ImportMultiValidateTest {
	
	// 测试时文件磁盘路径
	private static String path =  "test-excel-error.xlsx";
	// 配置文件路径
	private static ExcelContext context = new ExcelContext("/template/excel-config.xml");
	// Excel配置文件中配置的id
	private static String excelId = "student2";
	
	/**
	 * 导入Excel,使用了org.easy.excel.test.ExportTest.testExportCustomHeader()方法生成的Excel
	 * @throws Exception
	 */
	@Test
	public void testImport()throws Exception{
		Resource resource = new ClassPathResource(path);
		//第二个参数需要注意,它是指标题索引的位置,可能你的前几行并不是标题,而是其他信息,
		//比如数据批次号之类的,关于如何转换成javaBean,具体参考配置信息描述
		ExcelImportResult result = context.readExcel(excelId, 2, resource.getInputStream(),true);
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
		//通过导入结果集的hasErrors方法判断
		if(result.hasErrors()){
			System.out.println("导入包含错误，下面是错误信息：");
			for (ExcelError err:result.getErrors()) {
				System.out.println(err.getErrorMsg());
			}
		}
		resource.getInputStream().close();
		//这种方式和上面的没有任何区别,底层方法默认标题索引为0
		//context.readExcel(excelId, fis);
	}
	
	
		
}
