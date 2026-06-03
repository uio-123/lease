package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${sms.mock:true}")
    private boolean mock;

    @Value("${sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.sign-name:}")
    private String signName;

    @Value("${sms.aliyun.template-code:}")
    private String templateCode;

    private Client client;

    @PostConstruct
    public void init() {
        if (!mock) {
            if (accessKeyId.isEmpty() || accessKeySecret.isEmpty()) {
                throw new LeaseException("短信服务配置缺失：access-key-id 或 access-key-secret 未配置", ResultCodeEnum.SERVICE_ERROR.getCode());
            }
            try {
                Config config = new Config()
                        .setAccessKeyId(accessKeyId)
                        .setAccessKeySecret(accessKeySecret);
                config.endpoint = "dysmsapi.aliyuncs.com";
                client = new Client(config);
            } catch (Exception e) {
                throw new LeaseException("短信服务初始化失败：" + e.getMessage(), ResultCodeEnum.SERVICE_ERROR.getCode());
            }
        }
    }

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

        if (mock) {
            // 模拟模式：验证码打印到控制台
            System.out.println("【模拟短信】验证码：" + code + "，手机号：" + phone);
        } else {
            // 真实模式：调用阿里云短信
            sendRealSms(phone, code);
        }
    }

    private void sendRealSms(String phone, String code) {
        if (signName.isEmpty() || templateCode.isEmpty()) {
            throw new LeaseException("短信服务配置缺失：sign-name 或 template-code 未配置", ResultCodeEnum.SERVICE_ERROR.getCode());
        }
        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            client.sendSms(request);
            System.out.println("短信已发送至：" + phone);
        } catch (Exception e) {
            throw new LeaseException("短信发送失败：" + e.getMessage(), ResultCodeEnum.SERVICE_ERROR.getCode());
        }
    }
}
