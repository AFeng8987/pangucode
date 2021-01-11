import request from '@/utils/request'

// 查询
export function listOrder() {
  return request({
    url: '/config/order',
    method: 'get'
  })
}

// 修改
export function updateOrder(data) {
  return request({
    url: '/config/order',
    method: 'post',
    data
  })
}
