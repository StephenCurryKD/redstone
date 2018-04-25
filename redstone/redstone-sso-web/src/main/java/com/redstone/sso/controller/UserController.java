package com.redstone.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.CookieUtils;
import com.redstone.pojo.TbUser;
import com.redstone.sso.service.UserService;

/**
 * 用户处理Controller
 * <p>Title:UserController</p>
 * <p>Description:</p>
 * @author sky
 * @date 2018年2月22日
 * @version V1.0
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;
	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;
	
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public RedStoneResult checkUserData(@PathVariable String param,@PathVariable Integer type) {
		RedStoneResult result = userService.checkData(param, type);
		return result;
	}
	
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public RedStoneResult userRegister(TbUser user) {
		RedStoneResult result = userService.userRegister(user);
		return result;
	}
	
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public RedStoneResult userLogin(String username,String password,
			HttpServletResponse response,HttpServletRequest request) {
		RedStoneResult result = userService.userLogin(username, password);
		//登录成功后写cookie
		if(result.getStatus() == 200) {
			//把token写入cookie
			CookieUtils.setCookie(request, response, TOKEN_KEY, result.getData().toString());
		}
		return result;
	}
	
//	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET)
//	@ResponseBody
//	public RedStoneResult userLogout(@PathVariable String token) {
//		RedStoneResult result = userService.userLogout(token);
//		return result;
//	}
	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET)
	public String userLogout(@PathVariable String token) {
		RedStoneResult result = userService.userLogout(token);
		return "redirect:/page/login";
	}
	// jsonp第一种方法
	/*
		@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,
				//指定返回相应数据的content-type
				produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
		@ResponseBody
		public String getUserByToken(@PathVariable String token,String callback) {
			RedStoneResult result = userService.getUserByToken(token);
			//判断是否为jsonp请求
			if(StringUtils.isNotBlank(callback)) {
				return callback + "(" + JsonUtils.objectToJson(result) + ");";
			}
			return JsonUtils.objectToJson(result);
		}
	*/
	
	//jsonp第二种方法，适用于spring4.1以上版本
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET)
	@ResponseBody
	public Object getUserByToken(@PathVariable String token,String callback) {
		RedStoneResult result = userService.getUserByToken(token);
		//判断是否为jsonp请求
		if(StringUtils.isNotBlank(callback)) {
			MappingJacksonValue mJacksonValue=new MappingJacksonValue(result);
		    mJacksonValue.setJsonpFunction(callback);
		    return mJacksonValue;
		}
		return result;
	}
	
}
