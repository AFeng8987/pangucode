const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/record',
    name: 'record',
    meta: {
      // keepAlive: true,
      login: true,
      showHeader: false,
      title: '数据中心'
    },
    components: {
      default: () => import('@/views/record/tabbar-record'),
      tabbar: Tabbar
    }
  },
  {
    // 订单内容
    path: '/record/details/:itemId',
    name: 'recordDetails',
    props: true,
    component: () => import('@/views/record/details')
  }
]
