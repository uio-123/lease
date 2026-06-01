package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String login(String phone, String code) {
        // 1. 校验手机号
        if (phone == null || phone.isEmpty()) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }

        // 2. 校验验证码
        if (code == null || code.isEmpty()) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }

        // 3. 从Redis获取验证码（测试手机号13888888888跳过验证码校验）
        if (!"13888888888".equals(phone)) {
            String codeKey = RedisConstant.APP_LOGIN_PREFIX + phone + ":code";
            String cachedCode = redisTemplate.opsForValue().get(codeKey);
            if (cachedCode == null) {
                throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
            }
            if (!cachedCode.equals(code)) {
                throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
            }
            redisTemplate.delete(codeKey);
        }

        // 5. 查询或创建用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, phone);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if (userInfo == null) {
            // 新用户注册
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setNickname("用户" + phone.substring(phone.length() - 4));
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfoMapper.insert(userInfo);
        } else {
            // 检查用户状态
            if (userInfo.getStatus() == BaseStatus.DISABLE) {
                throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
            }
        }

        // 6. 创建Token并返回（测试手机号13888888888使用永久token，100年）
        String token;
        if ("13888888888".equals(phone)) {
            SecretKey key = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());
            token = Jwts.builder()
                    .setSubject("USER_INFO")
                    .setExpiration(new java.util.Date(System.currentTimeMillis() + 100L * 365 * 24 * 60 * 60 * 1000))
                    .claim("userId", userInfo.getId())
                    .claim("username", userInfo.getPhone())
                    .signWith(key)
                    .compact();
        } else {
            token = JwtUtil.createToken(userInfo.getId(), userInfo.getPhone());
        }

        // 7. 将用户信息存入Redis
        String userKey = RedisConstant.APP_LOGIN_PREFIX + "user:" + userInfo.getId();
        redisTemplate.opsForValue().set(userKey, userInfo.getId().toString(), 30, TimeUnit.DAYS);

        return token;
    }

    @Override
    public UserInfoVo getUserInfo() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        UserInfo userInfo = userInfoMapper.selectById(loginUser.getUserId());
        if (userInfo == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setNickname(userInfo.getNickname());
        userInfoVo.setAvatarUrl(userInfo.getAvatarUrl());
        return userInfoVo;
    }
}
