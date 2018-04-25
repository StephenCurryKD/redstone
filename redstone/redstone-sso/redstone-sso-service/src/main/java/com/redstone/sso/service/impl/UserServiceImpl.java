package com.redstone.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.common.utils.JsonUtils;
import com.redstone.jedis.JedisClient;
import com.redstone.mapper.TbUserMapper;
import com.redstone.pojo.TbUser;
import com.redstone.pojo.TbUserExample;
import com.redstone.pojo.TbUserExample.Criteria;
import com.redstone.sso.service.UserService;
/**
 * 用户处理Service
 * <p>Title:UserServiceImpl</p>
 * <p>Description:</p>
 * @author sky
 * @version V1.0
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${SESSION_EXPIRE}")
	private int SESSION_EXPIRE;
	
	@Override
	public RedStoneResult checkData(String data, int type) {
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		//1.判断用户名是否可用
		if(type == 1) {
			criteria.andUsernameEqualTo(data);
		}//2.判断手机号是否可用
		else if (type == 2) {
			criteria.andPhoneEqualTo(data);
		}//3.判断邮箱是否可用
		else if (type == 3) {
			criteria.andEmailEqualTo(data);
		}else {
			return RedStoneResult.build(400, "参数包含非法数据");
		}
		
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if (list !=null && list.size()>0) {
			//查询到数据，则返回false
			return RedStoneResult.ok(false);
		}
		//未查询到数据，则返回true
		return RedStoneResult.ok(true);
	}

	/**
	 * 用户注册
	 * <p>Title: userRegister</p> 
	 * <p>Description:</p>  
	 * @param user
	 * @return
	 */
	@Override
	public RedStoneResult userRegister(TbUser user) {
		//检查数据的有效性
		//1.用户名检验
		if (StringUtils.isBlank(user.getUsername())) {
			return RedStoneResult.build(400,"用户名不能为空");
		}
		RedStoneResult redStoneResult = checkData(user.getUsername(), 1);
		if (!(boolean) redStoneResult.getData()) {
			return RedStoneResult.build(400, "用户名重复");
		}
		//2.密码检验
		if (StringUtils.isBlank(user.getPassword())) {
			return RedStoneResult.build(400,"密码不能为空");
		}
		//3.电话检验
		if (StringUtils.isNotBlank(user.getPhone())) {
			//电话重复校验
			redStoneResult = checkData(user.getPhone(), 2);
			if (!(boolean) redStoneResult.getData()) {
				return RedStoneResult.build(400, "电话号码重复");
			}
		}
		//4.邮箱检验
		if (StringUtils.isNotBlank(user.getEmail())) {
			//电话重复校验
			redStoneResult = checkData(user.getEmail(), 3);
			if (!(boolean) redStoneResult.getData()) {
				return RedStoneResult.build(400, "邮箱地址重复");
			}
		}
		//补全pojo属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//密码进行md5加密
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		//插入数据
		userMapper.insert(user);
		//返回注册成功
		return RedStoneResult.ok();
	}
	
	/**
	 * 用户登录
	 * <p>Title: userLogin</p> 
	 * <p>Description:</p>  
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public RedStoneResult userLogin(String username, String password) {
		//判断用户名和密码是否正确
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0) {
			//返回登录失败
			return RedStoneResult.build(400, "用户名或密码不正确");
		}
		TbUser user = list.get(0);
		//密码要进行md5加密然后再校验
		if (!DigestUtils.md5DigestAsHex(password.getBytes())
				.equals(user.getPassword())) {
			//返回登录失败
			return RedStoneResult.build(400, "用户名或密码不正确");
		}
		//生成token,使用uuid
		String token=UUID.randomUUID().toString();
		//把用户信息保存到redis,key就是token,value就是用户信息
		//为了安全,清空用户信息密码然后再加入缓存
		user.setPassword(null);
		jedisClient.set(USER_SESSION+":"+token, JsonUtils.objectToJson(user));
		//设置key的过期时间
		jedisClient.expire(USER_SESSION+":"+token,SESSION_EXPIRE);
		//返回登录成功,并返回token
		return RedStoneResult.ok(token);
	}

	/**
	 * 根据token查询用户登录信息
	 * <p>Title: getUserByToken</p> 
	 * <p>Description:</p>  
	 * @param token
	 * @return
	 */
	@Override
	public RedStoneResult getUserByToken(String token) {
		String json = jedisClient.get(USER_SESSION+":"+token);
		if (StringUtils.isBlank(json)) {
			return RedStoneResult.build(400, "用户登录过期");
		}
		//重置Session过期时间
		jedisClient.expire(USER_SESSION+":"+token, SESSION_EXPIRE);
		//把json转成user对象
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return RedStoneResult.ok(user);
	}

	@Override
	public RedStoneResult userLogout(String token) {
		jedisClient.expire(USER_SESSION+":"+token, 0);
		return RedStoneResult.ok();
	}

}
