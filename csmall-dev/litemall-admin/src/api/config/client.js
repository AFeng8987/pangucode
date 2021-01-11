import request from '@/utils/request'

// 查询客服配置信息
export function configCustomer(query) {
  return request({
    url: '/config/customer',
    method: 'get',
    params: query
  })
}

// 修改客服配置信息
export function configCustomerSubmit(data) {
  return request({
    url: '/config/customer',
    method: 'post',
    data
  })
}
