import request from '@/utils/request'

// 实时库存查询
export function realtimeList(query) {
  return request({
    url: '/stock/realtimeList',
    method: 'get',
    params: query
  })
}
