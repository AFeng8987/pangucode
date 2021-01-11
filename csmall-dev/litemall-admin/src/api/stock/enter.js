import request from '@/utils/request'

// 入库列表
export function inList(query) {
  return request({
    url: '/stock/inList',
    method: 'get',
    params: query
  })
}
