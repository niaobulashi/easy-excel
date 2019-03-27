package org.easy.excel.test.blog;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.easy.excel.ExcelContext;
import org.easy.excel.test.model.AuthorModel;
import org.easy.excel.test.model.BookModel;
import org.easy.excel.test.model.StudentModel;

public class ExportTest {
	public static void main(String[] args) throws Exception {
		//准备excel输出流
		OutputStream ops = new FileOutputStream("src/test/resources/exportStudent.xlsx");
		//创建excel上下文实例,它的构成需要配置文件的路径
		ExcelContext context = new ExcelContext("excel-config.xml");
		//获取POI创建结果
		Workbook workbook = context.createExcel("student",getStudents());
		workbook.write(ops);
		ops.close();
		workbook.close();
	}
	
	//获取模拟数据,数据库数据...
	public static List<StudentModel> getStudents(){
		int size = 10;
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
