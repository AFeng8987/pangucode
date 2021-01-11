import request from '@/utils/request'

// 获取数据
export function hotNumber(query) {
  return request({
    url: '/config/hotNumber',
    method: 'get',
    params: query
  })
}

// 提交数据
export function hotNumberSubmit(data) {
  return request({
    url: '/config/hotNumber',
    method: 'post',
    data
  })
}
