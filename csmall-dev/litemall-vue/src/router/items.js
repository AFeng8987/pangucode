const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/items',
    name: 'class',
    meta: {
      keepAlive: false,
      showHeader: false,
      title: '分类'
    },
    components: {
      default: () => import('@/views/items/tabbar-catalog'),
      tabbar: Tabbar
    }
  },
  {
    // 商品详情界面
    path: '/items/goodsDetail/:itemId',
    name: 'goodsDetail',
    props: true,
    component: () => import('@/views/items/goods-detail')
  },
  {
    // 商品分类
    path: '/items/goodsClass',
    name: 'goodsClass',
    component: () => import('@/views/items/goods-classification')
  },
  {
    // 商品分享
    path: '/items/goodsShare',
    name: 'goodsShare',
    component: () => import('@/views/items/goods-share')
  }
]
