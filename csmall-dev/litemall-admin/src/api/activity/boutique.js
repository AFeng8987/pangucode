import request from '@/utils/request'

// 精品模块--List
export function activityList(query) {
  return request({
    url: '/activity/activityList',
    method: 'get',
    params: query
  })
}

// 精品模块--编辑
export function activityUpdate(data) {
  return request({
    url: '/activity/activityUpdate',
    method: 'post',
    data
  })
}

// 活动商品查询
export function activityGoodsList({ activityId, goodsCode, goodsName, limit, page }) {
  return request({
    url: '/activity/activityGoods/' + activityId,
    method: 'get',
    params: { goodsCode, goodsName, limit, page }
  })
}

// 查询活动下未添加的商品list
export function activityGoodsListData({ activityId, goodsCode, goodsName, limit, page }) {
  return request({
    url: '/activity/goodsList/' + activityId,
    method: 'get',
    params: { goodsName, goodsCode, limit, page }
  })
}

// 活动商品--删除
export function activityGoodsDelete(activityId, goodsId) {
  return request({
    url: '/activity/activityGoodsDel/' + activityId + '/' + goodsId,
    method: 'post'
  })
}

// 活动商品--添加
export function activityGoodsAdd(data) {
  return request({
    url: '/activity/activityGoodsAdd',
    method: 'post',
    data
  })
}
