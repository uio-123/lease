import request from '@/utils/request';
import type { RoomItemVo, RoomDetailVo, RoomQueryVo, PageResult, PaymentType, LeaseTerm } from '@/types';

// 分页查询房间列表
export const getRoomPage = (current: number, size: number, queryVo?: RoomQueryVo) => {
  return request<PageResult<RoomItemVo>>({
    url: '/room/pageItem',
    method: 'get',
    params: { current, size, ...queryVo },
  });
};

// 获取房间详情
export const getRoomDetailById = (id: number) => {
  return request<RoomDetailVo>({
    url: '/room/getDetailById',
    method: 'get',
    params: { id },
  });
};

// 根据公寓ID分页查询房间列表
export const getRoomPageByApartmentId = (current: number, size: number, id: number) => {
  return request<PageResult<RoomItemVo>>({
    url: '/room/pageItemByApartmentId',
    method: 'get',
    params: { current, size, id },
  });
};

// 根据房间ID获取支付方式列表
export const getPaymentTypeListByRoomId = (id: number) => {
  return request<PaymentType[]>({
    url: '/payment/listByRoomId',
    method: 'get',
    params: { id },
  });
};

// 获取全部支付方式列表
export const getAllPaymentTypeList = () => {
  return request<PaymentType[]>({
    url: '/payment/list',
    method: 'get',
  });
};

// 根据房间ID获取租期列表
export const getLeaseTermListByRoomId = (id: number) => {
  return request<LeaseTerm[]>({
    url: '/term/listByRoomId',
    method: 'get',
    params: { id },
  });
};
