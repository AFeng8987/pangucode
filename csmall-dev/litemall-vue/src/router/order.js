export default [
  {
    path: '/order/:active?',
    name: 'order',
    meta: {
      login: true,
      title: '订单列表'
    },
    components: {
      default: () => import('@/views/user/order/list')
    }
  },
  {
    path: '/order/detail/:id',
    name: 'orderDetail',
    meta: {
      login: true,
      title: '订单详情'
    },
    components: {
      default: () => import('@/views/user/order/detail')
    }
  },
  {
    path: '/saled/apply/:oid?/:gid?',
    name: 'saledApply',
    meta: {
      login: true,
      title: '申请售后'
    },
    props: { default: true },
    components: {
      default: () => import('@/views/user/order/saled/apply')
    }
  },
  {
    path: '/saled/detail/:oid?/:gid?',
    name: 'saledDetail',
    meta: {
      login: true,
      title: '售后详情'
    },
    props: { default: true },
    components: {
      default: () => import('@/views/user/order/saled/detail')
    }
  },
  {
    path: '/saled/refund/:oid?/:gid?',
    name: 'saledRefund',
    meta: {
      login: true,
      title: '仅退款'
    },
    props: { default: true },
    components: {
      default: () => import('@/views/user/order/saled/refund')
    }
  }
]
