#easy-excel
jar包依赖
Apache commons 
beanutils
collections
lang
poi
xmlbeans
spring-core
spring-beans

如何使用？,参考
org.easy.excel.test.ExportTest
org.easy.excel.test.ImportTest

支持,复杂对象导航,支持自定义(单元格宽度)
标题样式(背景色,对齐方式,字体颜色)

导出测试使用时,运行org.easy.excel.test.ExportTest类的测试方法,观察具体生成的excel文件

导入测试使用时,运行org.easy.excel.test.ImportTest,观察org.easy.excel.vo.ExcelImportResult

下面展示配置文件

<?xml version="1.0" encoding="UTF-8"?>

<excels>
	<!-- excel标签参看:ExcelDefinition,Field标签参看:FieldValue -->
	
	<!-- 测试使用 ,学生类-->
	<excel id="student" class="org.easy.excel.test.model.StudentModel" enableStyle="true">
		<field name="id" align="center" titleBgColor="dark_blue"
		columnWidth="3000" titleFountColor="white" title="ID"/>
		<field name="name" title="学生姓名" align="right"/>
		<field name="age" title="年龄" align="center" titleFountColor="red" titleBgColor="dark_blue" isNull="false" regex="^[1-9]\d*$" regexErrMsg="必须是数字"/>
		<field name="studentNo" title="学号" titleFountColor="blue" isNull="false" />
		<field name="createTime" columnWidth="4000" title="创建时间" pattern="yyyy-MM-dd"/>
		<field name="status" title="状态" titleBgColor="green" format="1:正常,0:禁用,-1:无效" />
		<!-- 创建人,可能需要查询数据库校验,这里使用自定义解析器 -->
		<field name="createUser" title="创建人" 
		resolveFieldValueConverterName="org.easy.excel.test.converter.CreateUserFieldValueConverter"/>
		
		<!-- 复杂对象 -->
		<field name="book.bookName" title="图书名称" columnWidth="6000"/>
		<field name="book.author.authorName" title="作者名称"/>
	</excel>
	
</excels>

上述配置,目前以包含所有支持的可配置属性。

关于excel配置属性说明：参看org.easy.excel.vo.ExcelDefinition

关于field配置属性说明参看:org.easy.excel.vo.FieldValue

关于使用：这里附上部分代码

1、导入

public void testImport()throws Exception{
	InputStream fis = new FileInputStream(path);
	ExcelImportResult result = context.readExcel(excelId, fis);
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
}

2、导出
public void testExportSimple()throws Exception{
	OutputStream ops = new FileOutputStream(path);
	Workbook workbook = context.createExcel(excelId,getStudents());
	workbook.write(ops);
	ops.close();
	workbook.close();
}