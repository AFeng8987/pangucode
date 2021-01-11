import request from '@/utils/request'

// 出库列表
export function outList(query) {
  return request({
    url: '/stock/outList',
    method: 'get',
    params: query
  })
}
