
//         订单状态：101 订单生成，未支付；
// 102，下单未支付用户取消；
// 103，下单未支付超期系统自动取消
// 201 支付完成，商家未发货；
// 301 商家发货，用户未确认；
// 401 用户确认收货，订单结束；
// 402 系统自动确认收货，订单结束。
// 501订单关闭',

export default {
  order: {
    '101': 'paying',
    '102': 'cancelled',
    '103': 'cancelled',
    '201': 'payed',
    '301': 'shipping',
    '401': 'finished',
    '402': 'finished',
    '501': 'closed'
  },

  // 0    未申请
  // '1': '',    退货审核通过,退款审核中
  // '2': '',    退货审核驳回
  // '3': '',    退款中
  // '4': '',    退款驳回
  // '5': '',    退款成功
  // '6': '',    退款失败
  // '7': '',    用户取消
  // '11': '',   退货审核中，待收货
  // '12': '',   退货审核中，已收货
  saled: {
    '0': 'notapply',
    '1': 'applied',
    '2': 'refund_reject',
    '3': 'refunding',
    '4': 'refund_reject',
    '5': 'refunded',
    '6': 'failure',
    '7': 'cancelled',
    '11': 'returning',
    '12': 'returning'
  }
}