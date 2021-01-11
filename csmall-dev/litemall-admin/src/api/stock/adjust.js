import request from '@/utils/request'

// 库存调整list列表数据
export function stockList(query) {
  return request({
    url: '/stock/list',
    method: 'get',
    params: query
  })
}

// 添加工厂
export function addFactory(data) {
  return request({
    url: '/stock/addFactory',
    method: 'post',
    data
  })
}

// 规格下工厂库存信息查询
export function listFactory(query) {
  return request({
    url: '/stock/listFactory',
    method: 'get',
    params: query
  })
}

// 删除工厂
export function delectFactory(data) {
  return request({
    url: '/stock/delFactory',
    method: 'post',
    headers: { 'Content-Type': 'application/json' },
    data
  })
}

// 工厂库存调整
export function updateStock(data) {
  return request({
    url: '/stock/updateStock',
    method: 'post',
    headers: { 'Content-Type': 'application/json' },
    data
  })
}
