import request from '@/utils/request'

// 订单导出列表list
export function orderExportList(query) {
  return request({
    url: '/order/export/list',
    method: 'get',
    params: query
  })
}

// 导出
export function orderExport(data) {
  return request({
    url: '/order/export',
    method: 'post',
    responseType: 'blob',
    data
  })
}
