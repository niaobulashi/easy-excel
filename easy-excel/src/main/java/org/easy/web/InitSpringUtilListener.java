//package org.easy.web;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
//import org.apache.log4j.Logger;
//
///**
// * WEB 环境初始化Spring 工具类
// * @author lisuo
// *
// */
//public class InitSpringUtilListener implements ServletContextListener {
//	private Logger log = Logger.getLogger(InitSpringUtilListener.class);
//	
//	public void contextDestroyed(ServletContextEvent evt) {}
//
//	public void contextInitialized(ServletContextEvent evt) {
//		log.debug("开始初始化SpringUtil...");
//		SpringUtil.init(evt.getServletContext());
//		log.debug("SpringUtil初始化完成.");
//	}
//
//}

//这里配置到WEB.xml
/*<listener>
    <description>初始化SpringUtil的监听器</description>
    <listener-class>org.easy.web.InitSpringUtilListener</listener-class>
</listener>*/