import request from '@/utils/request';
import type { ViewAppointment, AppointmentItemVo, AppointmentDetailVo } from '@/types';

// 保存或更新看房预约
export const saveOrUpdateAppointment = (data: ViewAppointment) => {
  return request({
    url: '/appointment/saveOrUpdate',
    method: 'post',
    data,
  });
};

// 获取个人预约列表
export const getAppointmentList = () => {
  return request<AppointmentItemVo[]>({
    url: '/appointment/listItem',
    method: 'get',
  });
};

// 获取预约详情
export const getAppointmentDetailById = (id: number) => {
  return request<AppointmentDetailVo>({
    url: '/appointment/getDetailById',
    method: 'get',
    params: { id },
  });
};

// 删除预约
export const deleteAppointment = (id: number) => {
  return request({
    url: '/appointment/delete',
    method: 'delete',
    params: { id },
  });
};
