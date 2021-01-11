import request from '@/utils/request'

// 退货--list
export function afterSaleList(query) {
  return request({
    url: '/afterSale/list',
    method: 'get',
    params: query
  })
}

// 导出
export function afterSaleExport(data) {
  return request({
    url: '/afterSale/export',
    method: 'post',
    data
  })
}

// 退货
export function returnGoodAudit(data) {
  return request({
    url: '/afterSale/returnGoodAudit',
    method: 'post',
    data
  })
}

// 批量退货
export function batchReturnGoodAudit(data) {
  return request({
    url: '/afterSale/batch-returnGoodAudit',
    method: 'post',
    data
  })
}

// 退款
export function returnAudit(data) {
  return request({
    url: '/afterSale/returnAudit',
    method: 'post',
    data
  })
}

// 批量退款
export function batchReturnAudit(data) {
  return request({
    url: '/afterSale/batch-returnAudit',
    method: 'post',
    data
  })
}

// 详情
export function afterSaleDetail(id) {
  return request({
    url: '/afterSale/detail/' + id,
    method: 'get'
  })
}

// 手动退款
export function afterSaleUpdateStatus(afterSaleSn) {
  return request({
    url: '/afterSale/updateStatus',
    method: 'post',
    data: { afterSaleSn }
  })
}

export function afterSaleConfirm(data) {
  return request({
    url: '/afterSale/confirm',
    method: 'post',
    data
  })
}
