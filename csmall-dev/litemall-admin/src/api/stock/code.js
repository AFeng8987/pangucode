import request from '@/utils/request'

// 编码维护列表
export function codeList(query) {
  return request({
    url: '/code/list',
    method: 'get',
    params: query
  })
}

// 编码维护-添加
export function codeCreate(data) {
  return request({
    url: '/code/create',
    method: 'post',
    data
  })
}

// 编码维护-编辑
export function codeUpdate(data) {
  return request({
    url: '/code/update',
    method: 'post',
    data
  })
}

// 编码维护-删除
export function codeDelete(data) {
  return request({
    url: '/code/delete',
    method: 'post',
    data
  })
}

// 根据编码id查询规格信息
export function codeRead(query) {
  return request({
    url: '/code/read',
    method: 'get',
    params: query
  })
}

// 规格信息修改
export function codeCreatePro(data) {
  return request({
    url: '/code/updatePro',
    method: 'post',
    data
  })
}

// 规格新增
export function createSpecs(data) {
  return request({
    url: '/code/createSpecs',
    method: 'post',
    data
  })
}

// 规格删除
export function deleteSpecs(data) {
  return request({
    url: '/code/delSpecs',
    method: 'post',
    data
  })
}
