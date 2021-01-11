import request from '@/utils/request'

// 订单发货--list
export function deliveryList(query) {
  return request({
    url: '/delivery/list',
    method: 'get',
    params: query
  })
}

// 订单发货
export function deliveryShip(data) {
  return request({
    url: '/delivery/ship',
    method: 'post',
    data
  })
}

// 发货清单
export function goodsDetail(id) {
  return request({
    url: '/delivery/goodsDetail',
    method: 'get',
    params: id
  })
}

// 识别单号
export function courierDelivery(id) {
  return request({
    url: '/delivery/' + id,
    method: 'get'
  })
}
