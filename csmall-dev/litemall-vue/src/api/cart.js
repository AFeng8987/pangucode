import request from '@/utils/request'

const CartList = 'wx/cart/index' // 获取购物车的数据
export function cartList(query) {
  return request({
    url: CartList,
    method: 'get',
    params: query
  })
}

const CartChecked = 'wx/cart/checked' // 选择或取消选择商品
export function cartChecked({ ids, isChecked }) {
  return request({
    url: CartChecked,
    method: 'post',
    data: { ids, isChecked }
  })
}

const CartUpdate = 'wx/cart/update' // 更新购物车的商品
export function cartUpdate({ id, number, goodsId }) {
  return request({
    url: CartUpdate,
    method: 'post',
    data: { id, number, goodsId }
  })
}

const CartDelete = 'wx/cart/delete' // 删除购物车的商品
export function cartDelete({ ids }) {
  return request({
    url: CartDelete,
    method: 'post',
    data: { ids }
  })
}

const CartCheckout = 'wx/cart/checkout' // 下单前信息确认
export function cartCheckout(data) {
  return request({
    url: CartCheckout,
    method: 'post',
    data
  })
}

const OrderSubmit = 'wx/order/submit' // 提交订单
export function orderSubmit(data) {
  return request({
    url: OrderSubmit,
    method: 'post',
    data
  })
}

const SelectList = 'wx/coupon/selectlist' // 当前购物车下单可用优惠券
export function selectList() {
  return request({
    url: SelectList,
    method: 'get'
  })
}
