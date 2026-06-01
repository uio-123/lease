package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void sendCode(String phone) {
        // 检查发送频率
        String sendTimeKey = RedisConstant.APP_LOGIN_PREFIX + phone + ":sendTime";
        String lastSendTime = redisTemplate.opsForValue().get(sendTimeKey);
        if (lastSendTime != null) {
            throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
        }

        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 保存验证码到Redis，10分钟有效
        String codeKey = RedisConstant.APP_LOGIN_PREFIX + phone + ":code";
        redisTemplate.opsForValue().set(codeKey, code, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);

        // 保存发送时间，用于限制发送频率
        redisTemplate.opsForValue().set(sendTimeKey, "1", RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC, TimeUnit.SECONDS);

        // 实际项目中这里应该调用短信服务发送验证码
        // 这里只是模拟，将验证码打印到控制台
        System.out.println("短信验证码：" + code);
    }
}
