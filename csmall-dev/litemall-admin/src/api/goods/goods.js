import request from '@/utils/request'

// 商品列表
export function listGoods(query) {
  return request({
    url: '/goods/list',
    method: 'get',
    params: query
  })
}

// 添加
export function publishGoods(data) {
  return request({
    url: '/goods/create',
    method: 'post',
    data
  })
}

// 删除
export function deleteGoods(data) {
  return request({
    url: '/goods/delete',
    method: 'post',
    data
  })
}

// 根据商品id查询详情
export function detailGoods(id) {
  return request({
    url: '/goods/detail',
    method: 'get',
    params: { id }
  })
}

// 更新商品
export function editGoods(data) {
  return request({
    url: '/goods/update',
    method: 'post',
    data
  })
}

// 上下架
export function goodsOnSale(data) {
  return request({
    url: '/goods/onSale',
    method: 'post',
    data
  })
}

// 设置是否首页展示
export function homeGoods(data) {
  return request({
    url: '/goods/homeGoods',
    method: 'post',
    data
  })
}
