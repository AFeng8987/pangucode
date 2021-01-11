import request from '@/utils/request'

const AlipayPay = '/wx/order/alipay' // 支付宝
export function alipayPay({ payOrderSn }) {
  return request({
    url: AlipayPay,
    method: 'post',
    data: { payOrderSn }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data
    }
  })
}

const WXPay = '/wx/order/prepay' // wx支付
export function wxPay(data) {
  return request({
    url: WXPay,
    method: 'post',
    data
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data
    }
  })
}
