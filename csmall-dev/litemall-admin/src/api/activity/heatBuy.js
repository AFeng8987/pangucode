import request from '@/utils/request'

export function updateGoods(data) {
  return request({
    url: '/goods/hotSale',
    method: 'post',
    data
  })
}

export function hotGoods(query) {
  return request({
    url: '/activity/hotGoods',
    method: 'get',
    params: query
  })
}
