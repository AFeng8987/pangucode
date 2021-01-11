import request from '@/utils/request'
import { Toast } from 'vant'

function callback(data) {
  if (data.errno === 0) {
    return data.data
  } else {
    Toast(data.errmsg)
    return data
  }
}

function orderHandle(order) {
  const result = {}
  result.id = order.payOrderSn
  result.goods = []
  result.price = 0
  result.freightPrice = 0
  result.actualPrice = 0
  result.goodsPrice = 0
  result.couponPrice = 0
  result.cid = []
  result.sn = []
  result.states = []
  result.info = {}
  Object.keys(order.orderList[0]).forEach((key) => {
    ['goodsList'].indexOf(key) === -1 ? result.info[key] = order.orderList[0][key] : null
  })

  order.orderList.reduce((_prev, curr) => {
    result.goods.push(...transGood(curr.goodsList))
    result.price += curr.orderPrice
    result.freightPrice = curr.freightPrice
    result.actualPrice += curr.actualPrice
    result.goodsPrice += curr.goodsPrice
    result.couponPrice += curr.couponPrice
    result.cid.push({
      id: curr.orderId,
      goid: curr.goodsList.map(good => {
        good.orderId = curr.orderId
        return good.orderGoodsId || good.id
      })
    })
    result.states.push(curr.orderStatus)
    result.sn.push(curr.orderSn)

    return curr
  }, {})

  result.status = getStatus(result.states)

  return result
}

function transGood(goods) {
  return goods.map(good => {
    good.price = parseFloat(good.price).toFixed(2)
    return good
  })
}

function getStatus(states) {
  // return 301
  let result = true
  states.reduce((prev, curr) => {
    if (prev) {
      result = result && (prev === curr)
    }
    return curr
  })
  return result ? states[0] : 301
}

export function changeStatus(order, orderId, status) {
  const index = order.cid.findIndex(({ id }) => id === orderId)
  order.states[index] = status
  order.status = getStatus(order.states)
}

export function list({ type, page }) {
  const limit = 10
  return request.get('/wx/order/list', {
    params: { showType: type, page, limit }
  }).then(({ data }) => callback(data))
    .then(data => {
      data.list = data.list.map(order => orderHandle(order))
      return data
    })
}

export function detail(orderId) {
  return request.get('/wx/order/detail', {
    loading: true,
    params: { payOrderSn: orderId }
  }).then(({ data }) => callback(data))
    .then(data => {
      return orderHandle({ payOrderSn: orderId, orderList: data })
    })
}

export function confirm(orderId) {
  return request.post('/wx/order/confirm', {
    orderId
  }).then(({ data }) => callback(data))
}

export function cancel(orderId) {
  return request.post('/wx/order/cancel', {
    payOrderSn: orderId
  }).then(({ data }) => callback(data))
}

export function remind(payOrderSn) {
  return request.post('/wx/order/reminderShipment', {
    payOrderSn
  }).then(({ data }) => callback(data))
}

export function logistics(orderId) {
  return request.get('/wx/order/logistics', {
    params: { orderId }
  })
    .then(({ data }) => callback(data))
    // .catch(data => loMockData.data)
}

export function saledDetail(id) {
  const params = {
    orderGoodsId: id
  }
  return request.get('/wx/aftersale/detail', { params })
    .then(({ data }) => callback(data))
}
/**
 * 申请售后
 * @param {object} data 传输数据
 *   // 'amount': 2000.00, // 退款金额
 *   // 'cargoStatus': 1, // 货物状态（1.未收到货，2已收到货）'
 *   // 'orderGoodsId': 6, // 订单子表id
 *   // 'reason': '', // 原因
 *   // 'serviceType': 1, // 服务类型（1.仅退款,    2.退货退款）
 *   // 'comment': '快点退', // 退款说明  非必填
 *   // 'pictures': ['string'] // 退款凭证图片链接数组
 */

export function saledApply(data) {
  const postData = {}
  postData.amount = data.amount
  postData.cargoStatus = data.recieved ? 2 : 1
  postData.orderGoodsId = data.ogid || 0
  postData.reason = data.reason || ''
  postData.serviceType = data.returned ? 2 : 1
  postData.comment = data.comment || ''
  postData.pictures = data.imgs || []

  return request.post('/wx/aftersale/submit', postData)
    .then(({ data }) => callback(data))
}

export function updateSaled({ id, company, no }) {
  return request.post('/wx/aftersale/update', {
    id,
    courierCompany: company,
    courierNumber: no
  })
    .then(({ data }) => callback(data))
}

export function cancelSaled(id) {
  return request.post('/wx/aftersale/cancel', { id })
    .then(({ data }) => callback(data))
}

export default {
  changeStatus,
  detail,
  list,
  cancel,
  confirm,
  logistics,
  saledDetail,
  saledApply,
  updateSaled,
  cancelSaled,
  remind
}
