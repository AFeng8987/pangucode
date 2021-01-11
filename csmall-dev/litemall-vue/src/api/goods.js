import request from '@/utils/request'

// 首页全部数据
export function homeIndex() {
  return request({
    url: '/wx/home/index',
    method: 'get'
  })
}

// 首页商品分页
export function homeGoodsPage({ page, limit }) {
  return request({
    url: '/wx/home/goodsPage',
    method: 'get',
    params: { page, limit }
  })
}

// 获取商品详情
const GoodsDetail = 'wx/goods/detail' // 获得商品的详情
export function goodsDetail(id) {
  return request({
    url: GoodsDetail,
    method: 'get',
    params: id
  })
}

const CartGoodsCount = 'wx/cart/goodscount' // 获取购物车商品件数
export function cartGoodsCount() {
  return request({
    url: CartGoodsCount,
    method: 'get'
  })
}

const CartAdd = 'wx/cart/add' // 添加商品到购物车
export function cartAdd({ goodsId, number, specCodeId, goodsCodeId }) {
  return request({
    url: CartAdd,
    method: 'post',
    data: { goodsId, number, specCodeId, goodsCodeId }
  })
}

const CatalogList = 'wx/catalog/index' // 分类目录全部分类数据接口
export function catalogList() {
  return request({
    url: CatalogList,
    method: 'get'
  })
}

const CatalogCurrent = 'wx/catalog/current' // 分类目录当前分类数据接口
export function catalogCurrent({ id }) {
  return request({
    url: CatalogCurrent,
    method: 'get',
    params: { id }
  })
}

const ActivityGoods = '/wx/goods/activityGoods' // 当前活动下的商品list
export function activityGoods({ activityId }) {
  return request({
    url: ActivityGoods,
    method: 'get',
    params: { activityId }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

const GoodsList = 'wx/goods/list' // 获得商品列表
export function goodsList({ categoryId, page, limit, sort, order, keyword }) {
  return request({
    url: GoodsList,
    method: 'get',
    params: { categoryId, page, limit, sort, order, keyword }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

const ShareGoods = '/wx/goods/shareGoods' // 分享商品
export function shareGoods(id) {
  return request({
    url: ShareGoods,
    method: 'get',
    params: { goodsId: id }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}
