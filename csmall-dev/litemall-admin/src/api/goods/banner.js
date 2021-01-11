import request from '@/utils/request'

// 分类List
export function adList(query) {
  return request({
    url: '/ad/list',
    method: 'get',
    params: query
  })
}

// 删除
export function adDelete(data) {
  return request({
    url: '/ad/delete',
    method: 'post',
    data
  })
}

// 添加
export function adCreate(data) {
  return request({
    url: '/ad/create',
    method: 'post',
    data
  })
}

// 详情
export function adRead(id) {
  return request({
    url: '/ad/read',
    method: 'get',
    params: id
  })
}

// 编辑
export function adUpdate(data) {
  return request({
    url: '/ad/update',
    method: 'post',
    data
  })
}
