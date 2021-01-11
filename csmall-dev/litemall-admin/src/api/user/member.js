import request from '@/utils/request'

// 会员信息list
export function userList(query) {
  return request({
    url: '/user/list',
    method: 'get',
    params: query
  })
}

// 会员变更为加盟商
export function allianceAdd(data) {
  return request({
    url: '/user/allianceAdd',
    method: 'post',
    data
  })
}

// 加盟商变更会员
export function allianceDel(data) {
  return request({
    url: '/user/allianceDel/' + data,
    method: 'post'
  })
}

// 团队
export function subordinate(query) {
  return request({
    url: '/user/subordinate',
    method: 'get',
    params: query
  })
}
