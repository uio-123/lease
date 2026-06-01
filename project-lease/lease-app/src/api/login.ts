import request from '@/utils/request';
import type { LoginVo, UserInfoVo } from '@/types';

// 获取短信验证码
export const getCode = (phone: string) => {
  return request({
    url: '/login/getCode',
    method: 'get',
    params: { phone },
  });
};

// 登录
export const login = (data: LoginVo) => {
  return request<string>({
    url: '/login',
    method: 'post',
    data,
  });
};

// 获取用户信息
export const getUserInfo = () => {
  return request<UserInfoVo>({
    url: '/info',
    method: 'get',
  });
};
