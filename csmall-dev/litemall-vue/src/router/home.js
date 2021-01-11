const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/',
    name: 'home',
    components: {
      default: () => import('@/views/home/tabbar-home'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: true,
      title: '首页'
    }
  },
  {
    path: '*',
    redirect: {
      name: 'home'
    }
  },
  {
    // 搜索
    path: '/home/search',
    name: 'search',
    meta: {
      keepAlive: true
    },
    component: () => import('@/views/home/home-search')
  },
  {
    // 活动商品界面
    path: '/home/activityGoods',
    name: 'activityGoods',
    component: () => import('@/views/home/home-activity')
  },
  {
    // 扫一扫
    path: '/qrscanner',
    name: 'qrscanner',
    components: {
      default: () => import('@/views/scanner/scanner')
    }
  },
  {
    path: '/external',
    name: 'external',
    component: () => import('@/views/home/external-links')
  }
]
