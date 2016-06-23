//package org.easy.web;
//
//import javax.servlet.ServletContext;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//
//
///**
// * Spring工具类,用于静态获取Spring容器中的Bean
// * @author lisuo
// *
// */
//public abstract class SpringUtil {
//	
//	private static ApplicationContext ctx;
//	/**
//	 * 由web容器初始化（用于服务环境）
//	 * @param sc servlet上下文
//	 */
//	static void init(ServletContext sc){
//		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
//	}
//	
//	/**
//	 * 直接传入applicationContext
//	 * @param actx 应用程序上下文对象
//	 */
//	static void init(ApplicationContext actx){
//		ctx = actx;
//	}
//	
//	/**
//	 * 获取bean
//	 * @param id
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> T getBean(String id){
//		return (T) ctx.getBean(id);
//	}
//	
//	/**
//	 * 按类型获取bean
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> T getBean(Class<T> clazz){
//		return ctx.getBean(clazz);
//	}
//	
//	/**
//	 * 按类型及ID获取bean
//	 * @param id
//	 * @param clazz
//	 * @return
//	 */
//	public static <T> T getBean(String id, Class<T> clazz){
//		return ctx.getBean(id, clazz);
//	}
//	
//	/**
//	 * 
//	 * 检查SpringUtil是否已完成初始化
//	 * @param
//	 * @return boolean
//	 * @throws
//	 */
//	public static boolean isInited(){
//		return null!=ctx;
//	}
//}
