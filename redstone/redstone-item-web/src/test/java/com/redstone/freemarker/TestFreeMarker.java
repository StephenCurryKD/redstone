package com.redstone.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFreeMarker {

	@Test
	public void testFreeMarker()throws Exception{
		//1.创建模板文件
		//2.创建一个Configuration对象
		Configuration configuration=new Configuration(Configuration.getVersion());
		//3.设置模板所在的路径
		configuration.setDirectoryForTemplateLoading(new File("G:/eclipse-workspace/redstone-item-web/src/main/webapp/WEB-INF/ftl"));
		//4.设置模板的字符集utf-8
		configuration.setDefaultEncoding("utf-8");
		//5.使用Configuration对象加载一个模板文件，指定模板文件的文件名
		Template template = configuration.getTemplate("hello.ftl");
		//6.创建一个数据集,可以是pojo,map.
		Map data=new HashMap<>();
		data.put("hello", "hello freemarker!");
		List<Student> stuList=new ArrayList<>();
		stuList.add(new Student(1,"李四","男",11));
		stuList.add(new Student(2,"张三","男",23));
		stuList.add(new Student(3,"郭达","男",40));
		stuList.add(new Student(4,"冯巩","男",30));
		data.put("stuList", stuList);
		//日期类型的处理
		data.put("val", "asd");
		data.put("date", new Date());
		//7.创建一个Writer对象，指定输出文件的路径及文件名
		Writer out=new FileWriter("D:/freemarker/out/student.html");
		//8.使用模板对象的process方法输出文件
		template.process(data, out);
		//9.关闭流
		out.close();
	}
}
