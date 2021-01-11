const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/user',
    name: 'user',
    meta: {
      // keepAlive: true,
      login: true,
      showHeader: false,
      title: '我的'
    },
    components: {
      default: () => import('@/views/user/tabbar-user'),
      tabbar: Tabbar }
  },
  {
    // 用户信息
    path: '/user/information',
    name: 'information',
    component: () => import('@/views/user/user-information')
  },
  {
    // 用户信息--修改密码--设置新密码
    path: '/user/modifyPassword',
    name: 'modifyPassword',
    component: () => import('@/views/user/user-information/user-modify-password')
  },
  {
    // 用户信息--关于我们
    path: '/user/aboutUs',
    name: 'aboutUs',
    component: () => import('@/views/user/user-information/user-about-us')
  },
  {
    // 收货地址
    path: '/user/addressList',
    name: 'addressList',
    component: () => import('@/views/user/user-address-list')
  },
  {
    // 收货地址--添加/编辑
    path: '/user/address',
    name: 'address',
    component: () => import('@/views/user/user-address')
  },
  {
    // 加入加盟商
    path: '/user/merchants',
    name: 'merchants',
    component: () => import('@/views/user/user-merchants')
  },
  {
    path: '/user/success',
    name: 'success',
    meta: {
      login: true,
      title: '支付成功'
    },
    components: {
      default: () => import('@/views/user/pay-success')
    }
  },
  {
    path: '/user/coupon/list/:active',
    name: 'couponList',
    props: true,
    component: () => import('@/views/user/user-coupon')
  },
  {
    path: '/user/extension',
    name: 'extension',
    component: () => import('@/views/user/user-extension')
  },
  {
    // 服务协议
    path: '/user/service',
    name: 'serviceAgreement',
    component: () => import('@/views/user/agreement/service.vue')
  },
  {
    // 隐私政策
    path: '/user/privacy',
    name: 'privacyPolicy',
    component: () => import('@/views/user/agreement/privacy.vue')
  },
  {
    // 售后客服
    path: '/user/after-sales',
    name: 'afterAales',
    component: () => import('@/views/user/user-information/after-sales')
  }
]
