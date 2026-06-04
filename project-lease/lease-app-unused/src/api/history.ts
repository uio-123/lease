import request from '@/utils/request';
import type { HistoryItemVo, PageResult } from '@/types';

// 获取浏览历史分页列表
export const getBrowsingHistoryPage = (current: number, size: number) => {
  return request<PageResult<HistoryItemVo>>({
    url: '/history/pageItem',
    method: 'get',
    params: { current, size },
  });
};
