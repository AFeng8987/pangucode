import request from '@/utils/request'

// 个人页面相关信息
export function userIndex() {
  return request({
    url: '/wx/user/index',
    method: 'get'
  })
}

// 用户信息
export function authInfo() {
  return request({
    url: '/wx/auth/info',
    method: 'get'
  })
}

// 修改用户信息
export function authProfile({ avatar, gender, nickname }) {
  return request({
    url: 'wx/auth/profile',
    method: 'post',
    data: { avatar, gender, nickname }
  })
}

const AuthLogout = 'wx/auth/logout' // 账号登出
export function authLogout() {
  return request({
    url: AuthLogout,
    method: 'post'
  })
}

const AddressList = 'wx/address/list' // 收货地址列表
export function addressList() {
  return request({
    url: AddressList,
    method: 'get'
  })
}

export function addressSave({ id, name, tel, province, city, county, street, addressDetail, areaCode, postalCode, isDefault }) {
  const url = id ? 'wx/address/update' : 'wx/address/save'
  return request({
    url,
    method: 'post',
    data: { id, name, tel, province, city, county, street, addressDetail, areaCode, postalCode, isDefault }
  })
}

const AddressDetail = 'wx/address/detail' // 收货地址详情
export function addressDetail(id) {
  return request({
    url: AddressDetail,
    method: 'get',
    params: id
  })
}

const AddressDelete = 'wx/address/delete' // 删除收货地址
export function addressDelete({ id }) {
  return request({
    url: AddressDelete,
    method: 'post',
    data: { id }
  })
}

export function userStatus() {
  return request({
    url: '/wx/user/index',
    method: 'get',
    userLevelId: 'status'
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

// 优惠券
export function couponList(page, limit, status) {
  return request({
    url: '/wx/coupon/mylist',
    method: 'get',
    params: { page, limit, status }
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}

// 获取售后信息
export function aftersaleCustomer() {
  return request({
    url: '/wx/aftersale/customer',
    method: 'get'
  })
}
