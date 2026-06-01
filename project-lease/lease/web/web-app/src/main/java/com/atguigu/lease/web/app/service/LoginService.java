package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;

public interface LoginService {
    String login(String phone, String code);

    UserInfoVo getUserInfo();
}
