export default [
  {
    path: '/login',
    name: 'login',
    meta: {
      showHeader: false,
      title: '登录'
    },
    component: () => import('@/views/login/login')
  },
  {
    // 注册账号
    path: '/login/registered',
    name: 'registered',
    component: () => import('@/views/login/registered')
  },
  {
    // 注册账号--注册成功
    path: '/login/success',
    name: 'success',
    component: () => import('@/views/login/registered-success')
  },
  {
    // 找回密码
    path: '/login/forget',
    name: 'forget',
    component: () => import('@/views/login/forget')
  },
  {
    // 找回密码--修改密码
    path: '/login/backPassword',
    name: 'setPassword',
    component: () => import('@/views/login/forget-backPassword')
  }
]
