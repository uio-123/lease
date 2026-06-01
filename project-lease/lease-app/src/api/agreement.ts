import request from '@/utils/request';
import type { LeaseAgreement, AgreementItemVo, AgreementDetailVo, LeaseStatus } from '@/types';

// 获取租约列表
export const getAgreementList = () => {
  return request<AgreementItemVo[]>({
    url: '/agreement/listItem',
    method: 'get',
  });
};

// 获取租约详情
export const getAgreementDetailById = (id: number) => {
  return request<AgreementDetailVo>({
    url: '/agreement/getDetailById',
    method: 'get',
    params: { id },
  });
};

// 更新租约状态
export const updateAgreementStatus = (id: number, leaseStatus: LeaseStatus) => {
  return request({
    url: '/agreement/updateStatusById',
    method: 'post',
    params: { id, leaseStatus },
  });
};

// 保存或更新租约
export const saveOrUpdateAgreement = (data: LeaseAgreement) => {
  return request({
    url: '/agreement/saveOrUpdate',
    method: 'post',
    data,
  });
};
