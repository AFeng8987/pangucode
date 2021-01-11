import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/views/layout/Layout'

/** note: Submenu only appear when children.length>=1
 *  detail see  https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 **/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    perms: ['GET /aaa','POST /bbb']     will control the page perms (you can set multiple perms)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
**/
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/authredirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/errorPage/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/errorPage/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  }
]

export const asyncRoutes = [
  {
    path: '/user',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'userManage',
    meta: {
      title: '用户管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'member',
        component: () => import('@/views/user/member/index'),
        name: 'member',
        meta: {
          perms: ['GET /admin/user/list'],
          title: '会员信息',
          noCache: true
        }
      },
      {
        path: 'factory',
        component: () => import('@/views/user/factory/index'),
        name: 'factory',
        meta: {
          perms: ['GET /admin/plant/sel'],
          title: '工厂信息',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/goods',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'goodsManage',
    meta: {
      title: '商品管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'list',
        component: () => import('@/views/goods/list/index'),
        name: 'goodsList',
        meta: {
          perms: ['GET /admin/goods/list'],
          title: '商品列表',
          noCache: true
        }
      },
      {
        path: 'create',
        component: () => import('@/views/goods/create/index'),
        name: 'goodsCreate',
        meta: {
          perms: ['POST /admin/goods/create'],
          title: '商品添加',
          noCache: true
        }
      },
      {
        path: 'edit',
        component: () => import('@/views/goods/edit/index'),
        name: 'goodsEdit',
        meta: {
          perms: ['POST /admin/goods/update'],
          title: '商品编辑',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'category',
        component: () => import('@/views/goods/category/index'),
        name: 'goodsCategory',
        meta: {
          perms: ['GET /admin/category/list'],
          title: '商品分类',
          noCache: true
        }
      },
      {
        path: 'banner',
        component: () => import('@/views/goods/banner/index'),
        name: 'goodsBanner',
        meta: {
          perms: ['GET /admin/ad/list'],
          title: 'Banner配置',
          noCache: true
        }
      },
      {
        path: 'coupon',
        component: () => import('@/views/goods/coupon/index'),
        name: 'coupon',
        meta: {
          perms: ['GET /admin/coupon/list'],
          title: '优惠券管理',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'orderManage',
    meta: {
      title: '订单管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'order',
        component: () => import('@/views/order/order/index'),
        name: 'order',
        meta: {
          perms: ['GET /admin/order/list'],
          title: '订单列表',
          noCache: true
        }
      },
      {
        path: 'refund',
        component: () => import('@/views/order/refund/index'),
        name: 'refund',
        meta: {
          perms: ['GET /admin/afterSale/list'],
          title: '退款审核',
          noCache: true
        }
      },
      {
        path: 'orderExport',
        component: () => import('@/views/order/orderExport/index'),
        name: 'orderExport',
        meta: {
          perms: ['GET /admin/order/export/list'],
          title: '订单导出',
          noCache: true
        }
      },
      {
        path: 'refundDetails',
        component: () => import('@/views/order/refundDetails/index'),
        name: 'refundDetails',
        meta: {
          perms: ['GET /admin/afterSale/detail/{id}'],
          title: '退款详情',
          noCache: true
        },
        hidden: true
      }
    ]
  },
  {
    path: '/orderDelivery',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'orderDelivery',
    meta: {
      title: '订单发货',
      icon: 'chart'
    },
    children: [
      {
        path: 'delivery',
        component: () => import('@/views/logistics/orderDelivery/index'),
        name: 'delivery',
        meta: {
          perms: ['GET /admin/delivery/list'],
          title: '订单发货',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/activity',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'activityManage',
    meta: {
      title: '活动板块',
      icon: 'chart'
    },
    children: [
      {
        path: 'boutique',
        component: () => import('@/views/activity/boutique/index'),
        name: 'boutique',
        meta: {
          perms: ['GET /admin/activity/activityList'],
          title: '精品模块',
          noCache: true
        }
      },
      {
        path: 'heatBuy',
        component: () => import('@/views/activity/heatBuy/index'),
        name: 'heatBuy',
        meta: {
          perms: ['POST /admin/goods/hotSale'],
          title: '热卖模块',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/stock',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'stockManage',
    meta: {
      title: '库存管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'code',
        component: () => import('@/views/stock/code/index'),
        name: 'code',
        meta: {
          perms: ['GET /admin/code/list'],
          title: '编码维护',
          noCache: true
        }
      },
      {
        path: 'maintain',
        component: () => import('@/views/stock/code/maintain'),
        name: 'maintain',
        meta: {
          perms: ['GET /admin/code/read'],
          title: '规格编码维护',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'adjust',
        component: () => import('@/views/stock/adjust/index'),
        name: 'adjust',
        meta: {
          perms: ['GET /admin/stock/list'],
          title: '库存调整',
          noCache: true
        }
      },
      {
        path: 'real',
        component: () => import('@/views/stock/real/index'),
        name: 'realTime',
        meta: {
          perms: ['GET /admin/stock/realtimeList'],
          title: '实时库存',
          noCache: true
        }
      },
      {
        path: 'warning',
        component: () => import('@/views/stock/warning/index'),
        name: 'warning',
        meta: {
          perms: ['GET /admin/stock/warnList'],
          title: '预警库存',
          noCache: true
        }
      },
      {
        path: 'out',
        component: () => import('@/views/stock/out/index'),
        name: 'out',
        meta: {
          perms: ['GET /admin/stock/outList'],
          title: '出库',
          noCache: true
        }
      },
      {
        path: 'enter',
        component: () => import('@/views/stock/enter/index'),
        name: 'enter',
        meta: {
          perms: ['GET /admin/stock/inList'],
          title: '入库',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/sys',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'sysManage',
    meta: {
      title: '系统管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'admin',
        component: () => import('@/views/sys/admin/index'),
        name: 'admin',
        meta: {
          perms: ['GET /admin/admin/list', 'POST /admin/admin/create', 'POST /admin/admin/update', 'POST /admin/admin/delete', 'POST /admin/admin/updatepswd'],
          title: '管理员',
          noCache: true
        }
      },
      {
        path: 'role',
        component: () => import('@/views/sys/role/index'),
        name: 'role',
        meta: {
          perms: ['GET /admin/role/list', 'POST /admin/role/create', 'POST /admin/role/update', 'POST /admin/role/delete', 'GET /admin/role/permissions', 'POST /admin/role/permissions'],
          title: '角色管理',
          noCache: true
        }
      },
      {
        path: 'log',
        component: () => import('@/views/sys/log/index'),
        name: 'log',
        meta: {
          perms: ['GET /admin/log/list'],
          title: '操作日志',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/config',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'configManage',
    meta: {
      title: '配置管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'client',
        component: () => import('@/views/config/client/index'),
        name: 'clientConfig',
        meta: {
          perms: ['GET /admin/config/customer'],
          title: '客服配置',
          noCache: true
        }
      },
      {
        path: 'postage',
        component: () => import('@/views/config/postage/index'),
        name: 'postageConfig',
        meta: {
          perms: ['GET /admin/config/express'],
          title: '邮费配置',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/config/order/index'),
        name: 'configOrder',
        meta: {
          perms: ['GET /admin/config/order'],
          title: '订单配置',
          noCache: true
        }
      },
      {
        path: 'selling',
        component: () => import('@/views/config/selling/index'),
        name: 'configSelling',
        meta: {
          perms: ['GET /admin/config/hotNumber'],
          title: '热卖配置',
          noCache: true
        }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    children: [
      {
        path: 'password',
        component: () => import('@/views/profile/password'),
        name: 'password',
        meta: { title: '修改密码', noCache: true }
      },
      {
        path: 'notice',
        component: () => import('@/views/profile/notice'),
        name: 'notice',
        meta: { title: '通知中心', noCache: true }
      }
    ],
    hidden: true
  },

  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
