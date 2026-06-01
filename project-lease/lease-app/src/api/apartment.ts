import request from '@/utils/request';
import type { ApartmentItemVo, ApartmentDetailVo } from '@/types';

// 获取公寓详情
export const getApartmentDetailById = (id: number) => {
  return request<ApartmentDetailVo>({
    url: '/apartment/getDetailById',
    method: 'get',
    params: { id },
  });
};

// 根据区县ID获取公寓列表
export const getApartmentListByDistrictId = (districtId: number) => {
  return request<ApartmentItemVo[]>({
    url: '/apartment/listByDistrictId',
    method: 'get',
    params: { districtId },
  });
};

// 根据条件查询公寓列表
export const getApartmentListByQuery = (params: {
  provinceId?: number;
  cityId?: number;
  districtId?: number;
}) => {
  return request<ApartmentItemVo[]>({
    url: '/apartment/listByQuery',
    method: 'get',
    params,
  });
};

// 保存浏览历史
export const saveBrowsingHistory = (apartmentId: number) => {
  return request({
    url: '/history/save',
    method: 'post',
    params: { apartmentId },
  });
};
