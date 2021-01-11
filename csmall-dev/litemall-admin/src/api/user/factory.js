import request from '@/utils/request'

// 工厂列表
export function plantList(query) {
  return request({
    url: '/plant/sel',
    method: 'get',
    params: query
  })
}

// 工厂--添加
export function plantAdd(data) {
  return request({
    url: '/plant/add',
    method: 'post',
    data
  })
}

// 工厂--编辑
export function plantUpdate(data) {
  return request({
    url: '/plant/update',
    method: 'post',
    data
  })
}

// 工厂--删除
export function plantDelete(data) {
  return request({
    url: '/plant/del/' + data,
    method: 'post'
  })
}

// 工厂--详情
export function plantDetail(id) {
  return request({
    url: '/plant/detail',
    method: 'get',
    params: id
  })
}
