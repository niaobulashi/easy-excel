package org.easy.util;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.BeanUtils;


/**
 * 反射工具类
 * @author lisuo
 */
public abstract class ReflectUtil {
	
	private static final Set<String> PRIMITIVE_NUMBER_MAP = new HashSet<>();
	static{
		PRIMITIVE_NUMBER_MAP.add("double");
		PRIMITIVE_NUMBER_MAP.add("float");
		PRIMITIVE_NUMBER_MAP.add("int");
		PRIMITIVE_NUMBER_MAP.add("short");
		PRIMITIVE_NUMBER_MAP.add("long");
		PRIMITIVE_NUMBER_MAP.add("byte");
	}
	
	/**
	 * 获取常量值
	 * @param clazz 常量类
	 * @param constName 常量名称
	 * @return 常量对应的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getConstValue(Class<?> clazz,String constName){
		Field field = ReflectUtil.getField(clazz, constName);
		if(field!=null){
			field.setAccessible(true);
	    	try {
	    		Object object = field.get(null);
	    		if(object != null){
	    			return (T) object;
	    		}
	    		return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	/**
	 * 获取指定类的所有字段名称,排除static,final字段
	 * @param clazz 类型
	 * @return List<字段名称>
	 */
	public static List<String> getFieldNames(Class<?> clazz){
		List<String> fieldNames = new ArrayList<>();
		while(clazz!=Object.class){
			try {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field:fields) {
					int modifiers = field.getModifiers();
					//过滤static或final字段
					if(Modifier.isStatic(modifiers)||Modifier.isFinal(modifiers)){
						continue;
					}
					fieldNames.add(field.getName());
				}
			} catch (Exception ignore) {}
			clazz = clazz.getSuperclass();
		}
		return fieldNames;
	}
	
	/**
	 * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型
	 * 如: public EmployeeDao extends BaseDao<Employee, String>
	 * @param clazz
	 * @param index
	 * @return
	 */
	public static Class<?> getSuperClassGenricType(Class<?> clazz, int index){
		Type genType = clazz.getGenericSuperclass();
		
		if(!(genType instanceof ParameterizedType)){
			return Object.class;
		}
		Type [] params = ((ParameterizedType)genType).getActualTypeArguments();
		if(index >= params.length || index < 0){
			return Object.class;
		}
		if(!(params[index] instanceof Class)){
			return Object.class;
		}
		return (Class<?>) params[index];
	}
	
	/**
	 * 通过反射, 获得 Class 定义中声明的父类的泛型参数类型
	 * 如: public EmployeeDao extends BaseDao<Employee, String>
	 * 返回 Employee.class
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> Class<T> getSuperGenericType(Class<?> clazz){
		return (Class<T>) getSuperClassGenricType(clazz, 0);
	}
	
	/**
	 * 拷贝非空属性
	 * @param source
	 * @param target
	 * @param ignorProps 忽略的属性
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyProps(Object source, Object target, String...ignorProps) {
		if(source instanceof Map){
			try {
				Map sourceMap = (Map)source;
				Set<String> ignorPropsSet = new HashSet<String>();
				if(null!=ignorProps){
					for(String prop : ignorProps){
						ignorPropsSet.add(prop);
					}
				}
				for(Object key : sourceMap.keySet()){
					if(null==key || ignorPropsSet.contains(key)){
						continue;
					}else{
						Object value = sourceMap.get(key);
						if(PropertyUtils.isWriteable(target, key.toString())){
							PropertyUtils.setProperty(target, key.toString(), value);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else if(source instanceof List){
			List sourceList = (List)source;
			if(target instanceof List){
				List targetList = (List)target;
				for(Object obj:sourceList){
					targetList.add(obj);
				}
			}
			
		}else{
			BeanUtils.copyProperties(source, target, ignorProps);
		}
	}
	
	
	/**
	 * java bean转Map
	 * @param bean
	 * @param propNames 需要放到map中的属性名称
	 * @return
	 */
	public static Map<String,Object> beanToMap(Object bean, String...propNames) {
		Validate.notNull(bean, "Bean is null");
		Validate.notEmpty(propNames,"PropNames is null");
		Map<String,Object> rtn = new HashMap<String,Object>();
		if(ArrayUtils.isEmpty(propNames) || "*".equals(propNames[0])){
			PropertyDescriptor[] propDescs = PropertyUtils.getPropertyDescriptors(bean);
			for(PropertyDescriptor propDesc : propDescs){
				String propName = propDesc.getName();
				Object propValue = null;
				propValue = getProperty(bean, propName);
				rtn.put(propName, propValue);
			}
		}else{
			for(String propName: propNames){
				Object propValue = null;
				if(PropertyUtils.isReadable(bean, propName)){
					propValue = getProperty(bean, propName);
				}
				rtn.put(propName, propValue);
			}
		}
		rtn.remove("class");
		return rtn;
	}
	
	/**
	 * Map 转 JavaBean
	 * @param map
	 * @param clazz
	 * @return 
	 */
	public static <T> T mapToBean(Map<String,?> map,Class<T> clazz){
		T bean = newInstance(clazz);
		try {
			for(Entry<String, ?> me:map.entrySet()){
				setProperty(bean, me.getKey(), me.getValue(), true);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bean;
	}
	
	/**
	 * 反射无参创建对象
	 * @param clazz
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz){
		return BeanUtils.instantiate(clazz);
	}
	
	/**
	 * 获取私有属性Value
	 * @param bean
	 * @param name
	 * @return
	 */
	public static Object getPrivateProperty(Object bean, String name){
		try {
			Field field = getField(bean.getClass(), name);
			if(field!=null){
				field.setAccessible(true);
				return field.get(bean);
			}else{
				throw new RuntimeException("The field [ "+field+"] in ["+bean.getClass().getName()+"] not exists");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 设置私有属性Value
	 * @param bean
	 * @param name
	 * @param value
	 */
	public static void setPrivateProperty(Object bean, String name, Object value){
		try {
			Field field = getField(bean.getClass(), name);
			if(field!=null){
				field.setAccessible(true);
				field.set(bean, value);
			}else{
				throw new RuntimeException("The field [ "+field+"] in ["+bean.getClass().getName()+"] not exists");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取字段
	 * @param clazz
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Field getField(Class<?> clazz,String name){
		Field field = null;
		while(clazz!=Object.class){
			try {
				field = clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}
			if(field!=null){
				return field;
			}
			clazz = clazz.getSuperclass();
		}
		return field;
		
	}
	
	/**
	 * 获取字段类型
	 * @param clazz
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Class<?> getFieldType(Class<?> clazz,String name) {
		Field field = getField(clazz, name);
		if(field!=null){
			return field.getType();
		}else{
			throw new RuntimeException("Cannot locate field " + name + " on " + clazz);
		}
	}

	/**
	 * @see #setProperty(Object, String, Object, boolean)
	 * @param bean
	 * @param name
	 * @param value
	 */
	public static void setProperty(Object bean, String name, Object value){
		setProperty(bean, name, value, false);
	}

	/**
	 * 设置Bean属性,类型自动转换,如果涉及日期转换,只支持long类型或者long值的字符串
	 * @param bean
	 * @param name
	 * @param value
	 * @param ignoreError 忽略找不到属性错误
	 * @throws Exception
	 */
	public static void setProperty(Object bean, String name, Object value, boolean ignoreError) {
		if (value != null) {
			try {
				Class<?> type = getPropertyType(bean, name);
				if (type != null) {
					if (!value.getClass().equals(type)) {
						//如果把string转换成日期尝试先把string转换long,在把long转换为日期
						if (Date.class.isAssignableFrom(type) && value instanceof String) {
							try {
								value = new Date(Long.parseLong((String) value));
							} catch (NumberFormatException ignore) {}
						} else {
							//如果把一个字符串转换成数值,去除数值的,
							if(Number.class.isAssignableFrom(type)){
								value = CharSetUtils.delete(value.toString(),",");
							}else {
								//是基本数据类型
								if(type.isPrimitive()){
									if(PRIMITIVE_NUMBER_MAP.contains(type.getName())){
										value = CharSetUtils.delete(value.toString(),",");
									}
								}
							}
							value = ConvertUtils.convert(value, type);
						}
					}
				}
				PropertyUtils.setProperty(bean, name, value);
			} catch (NestedNullException e) {
				createNestedBean(bean, name);
				setProperty(bean, name, value, ignoreError);
			} catch (NoSuchMethodException e) {
				if (!ignoreError)
					throw new RuntimeException(e);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 获取属性,忽略NestedNullException
	 * 
	 * @param bean
	 * @param name
	 * @return
	 */
	public static Object getProperty(Object bean, String name){
		try {
			return PropertyUtils.getProperty(bean, name);
		} catch (NestedNullException ignore) {
			return null;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取属性类型,忽略NestedNullException
	 * 
	 * @param bean
	 * @param name
	 * @return
	 */
	public static Class<?> getPropertyType(Object bean, String name) {
		try {
			return PropertyUtils.getPropertyType(bean, name);
		} catch (NestedNullException ignore) {
			return null;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建内嵌对象
	 * 
	 * @param bean
	 * @param name
	 */
	private static void createNestedBean(Object bean, String name){
		String[] names = name.split("[.]");
		if (names.length == 1)
			return;
		try {
			StringBuilder nestedName = new StringBuilder();
			for (int i = 0; i < names.length - 1; i++) {
				String n = names[i];
				if (i > 0) {
					nestedName.append("." + n);
				} else {
					nestedName.append(n);
				}
				Object val = PropertyUtils.getProperty(bean, nestedName.toString());
				if (val == null) {
					PropertyUtils.setProperty(bean, nestedName.toString(),
							PropertyUtils.getPropertyType(bean, nestedName.toString()).newInstance());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
