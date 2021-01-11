import request from '@/utils/request'

// 分类List
export function listCategory(page, limit, level, pid) {
  return request({
    url: '/category/list',
    method: 'get',
    params: { page: page, limit: limit, level: level, pid: pid }
  }).then(({ data }) => {
    if (!level) {
      data.data.list.forEach(element => {
        element.create = []
      })
      return data.data
    }
    return data.data
  })
}

// 一级分类
export function listCatL1() {
  return request({
    url: '/category/L1',
    method: 'get'
  })
}

// 而级分类
export function listCatL2(pid) {
  return request({
    url: '/category/L2/' + pid,
    method: 'get'
  })
}

// 分类添加
export function createCategory(data) {
  return request({
    url: '/category/create',
    method: 'post',
    data
  })
}

// 分类编辑
export function updateCategory(data) {
  return request({
    url: '/category/update',
    method: 'post',
    data
  })
}

// 删除
export function deleteCategory(data) {
  return request({
    url: '/category/delete',
    method: 'post',
    data
  })
}
