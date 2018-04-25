package com.redstone.sso.service;

import com.redstone.common.pojo.RedStoneResult;
import com.redstone.pojo.TbUser;

public interface UserService {

	RedStoneResult checkData(String data,int type);
	RedStoneResult userRegister(TbUser user);
	RedStoneResult userLogin(String username,String password);
	RedStoneResult getUserByToken(String token);
	RedStoneResult userLogout(String token);
}
