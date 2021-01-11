import request from '@/utils/request'

// 销售数据
export function allianceTotal(type) {
  return request({
    url: '/wx/Alliance/total',
    method: 'get',
    params: type
  })
}

// 销售数据List
export function allianceList({ page, limit }) {
  return request({
    url: 'wx/Alliance/list',
    method: 'get',
    params: { page, limit }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

// 加盟商订单详情
export function allianceDetail(orderGoodsId) {
  return request({
    url: 'wx/Alliance/detail',
    method: 'get',
    params: { orderGoodsId }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}
