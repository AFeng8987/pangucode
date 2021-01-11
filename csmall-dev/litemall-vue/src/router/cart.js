const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/cart',
    name: 'cart',
    meta: {
      login: true,
      showHeader: false,
      title: '购物车'
    },
    components: {
      default: () => import('@/views/cart/tabbar-cart'),
      tabbar: Tabbar
    }
  },
  {
    // 确认订单
    path: '/cart/confirm',
    name: 'orderConfirm',
    component: () => import('@/views/cart/order-confirm')
  }
]
