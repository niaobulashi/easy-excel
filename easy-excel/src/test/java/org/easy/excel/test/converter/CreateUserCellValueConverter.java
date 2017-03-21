package org.easy.excel.test.converter;

import org.easy.excel.config.FieldValue;
import org.easy.excel.exception.ExcelException;
import org.easy.excel.parsing.CellValueConverter;
import org.easy.excel.test.model.StudentModel;
import org.springframework.stereotype.Component;

/**
 * 自定义转换,测试学生创建人
 * @author lisuo
 *
 */
//@Component,与spring集成之后可以使用
public class CreateUserCellValueConverter implements CellValueConverter{

	@Override
	public Object convert(Object bean, Object value, FieldValue fieldValue, Type type, int rowNum)
			throws Exception {
		System.out.println("org.easy.excel.test.converter.CreateUserCellValueConverter:20行输出："+fieldValue.getOtherConfig());
		//如果是导入
		if(type==Type.IMPORT){
			if(queryForDb(value.toString())){
				//这里可以重新对对象进行设置
				StudentModel stu = (StudentModel) bean;
				stu.setCreateUserId("001");
				//stu.setCreateUserId(xx);
				return value;
			}else{
				StringBuilder err = new StringBuilder()
						.append("第[").append(rowNum).append("行],[")
						.append(fieldValue.getTitle()).append("]")
						.append("在数据库中没有找到["+value.toString()+"]的用户信息");
				throw new ExcelException(err.toString());
			}
		}else{
			System.out.println("第【"+rowNum+"】行Cell数据被创建");
		}
		return value;
	}
	
	//模拟查询数据库
	private boolean queryForDb(String createUser){
		if(createUser.startsWith("王五")){
			System.out.println("数据库有查询到王五,发生在org.easy.excel.test.converter.CreateUserCellValueConverter");
			return true;
		}
		return false;
	}
	
}
