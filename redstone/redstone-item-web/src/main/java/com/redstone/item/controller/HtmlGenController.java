package com.redstone.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 网页静态化处理Controller
 * <p>Title:HtmlGenController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/genhtml")
	@ResponseBody
	public String genHtml()throws Exception {
		//生成静态页面
		Configuration configuration=freeMarkerConfigurer.getConfiguration();
		Template template = configuration.getTemplate("hello2.ftl");
		Map data=new HashMap<>();
		data.put("hello", "spring freemarker test");
		Writer out=new FileWriter(new File("D:/freemarker/out/test.html"));
		template.process(data, out);
		//返回结果
		out.close();
		return "ok";
	}
}
