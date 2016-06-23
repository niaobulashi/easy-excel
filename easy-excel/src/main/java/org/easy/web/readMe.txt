这里主要是考虑到大家可能需要与Spring进行集成自定义的转换器
如果需要集成需要打开InitSpringUtilListener.java中的注释
SpringUtil.java中的注释

同时在web.xml中配置
<listener>
    <description>初始化SpringUtil的监听器</description>
    <listener-class>org.easy.web.InitSpringUtilListener</listener-class>
</listener>

最后打开org.easy.excel.AbstractExcelResolver.getBean(String)
中的注释,也就是//return (ResolveFieldValueConverter) SpringUtil.getBean(Class.forName(convName));
