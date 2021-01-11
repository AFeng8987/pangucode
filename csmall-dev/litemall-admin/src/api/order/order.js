import request from '@/utils/request'
import Qs from 'qs'

// 查询
export function orderList(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

// 详情
export function orderDetail(id) {
  return request({
    url: '/order/detail',
    method: 'get',
    params: id
  })
}
