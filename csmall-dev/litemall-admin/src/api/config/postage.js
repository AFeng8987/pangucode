import request from '@/utils/request'

// 查询客服配置信息
export function configExpress(query) {
  return request({
    url: '/config/express',
    method: 'get',
    params: query
  })
}

// 修改客服配置信息
export function configExpressSubmit(data) {
  return request({
    url: '/config/express',
    method: 'post',
    data
  })
}
