import request from '@/utils/request';
import type { ProvinceInfo, CityInfo, DistrictInfo } from '@/types';

// 获取省份列表
export const getProvinceList = () => {
  return request<ProvinceInfo[]>({
    url: '/region/province/list',
    method: 'get',
  });
};

// 根据省份ID获取城市列表
export const getCityListByProvinceId = (id: number) => {
  return request<CityInfo[]>({
    url: '/region/city/listByProvinceId',
    method: 'get',
    params: { id },
  });
};

// 根据城市ID获取区县列表
export const getDistrictListByCityId = (id: number) => {
  return request<DistrictInfo[]>({
    url: '/region/district/listByCityId',
    method: 'get',
    params: { id },
  });
};
