package com.redstone.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 展示登录和注册的页面Controller
 * <p>Title:PageController</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Controller
public class PageController {

	@RequestMapping("/page/register")
	public String showRegister(){
		return "register";
	}
	
	@RequestMapping("/page/login")
	public String showLogin(String url,Model model){
		model.addAttribute("redirect", url);
		return "login";
	}
}
