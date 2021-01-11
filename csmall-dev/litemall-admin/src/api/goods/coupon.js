import request from '@/utils/request'

// 优惠券List
export function couponList(query) {
  return request({
    url: '/coupon/list',
    method: 'get',
    params: query
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

// 优惠券减免金额修改
export function couponUpdate(data) {
  return request({
    url: '/coupon/update',
    method: 'post',
    data
  })
}
