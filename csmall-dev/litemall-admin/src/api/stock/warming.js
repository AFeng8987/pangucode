import request from '@/utils/request'

// 库存预警查询
export function warnList(query) {
  return request({
    url: '/stock/warnList',
    method: 'get',
    params: query
  })
}
