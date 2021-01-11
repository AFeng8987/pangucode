import request from '@/utils/request'

// 账号登录
export function authLogin({ username, password, type, code }) {
  return request({
    url: '/wx/auth/login',
    method: 'post',
    data: { username, password, type, code }
  })
}

// 用户注册
export function authCreate({ mobile, code, password, type, invitationCode }) {
  return request({
    url: '/wx/auth/create',
    method: 'post',
    data: { mobile, code, password, type, invitationCode }
  })
}
// 获取验证码 typ: login(登录)  register(注册)  forget(找回) binding(微信登录绑定手机号码)
export function authCaptcha({ mobile, type }) {
  return request({
    url: '/wx/auth/captcha',
    method: 'post',
    data: { mobile, type }
  })
}

// 校验手机号码是否注册
export function authVerify({ mobile }) {
  return request({
    url: '/wx/auth/verify',
    method: 'post',
    data: { mobile }
  })
}

// 找回密码-修改密码
export function authReset({ password, mobile, code, type }) {
  return request({
    url: '/wx/auth/reset',
    method: 'post',
    data: { password, mobile, code, type }
  })
}

// 微信登录
export function wechatLogin(data) {
  return request({
    url: '/wx/auth/weixin/login',
    method: 'post',
    data
  })
}

// 微信登录绑定手机号码
export function wechatCheckCaptcha({ mobile, code, invitationCode, type, userInfo }) {
  return request({
    url: '/wx/auth/wxLogin/checkCaptcha',
    method: 'post',
    data: { mobile, code, invitationCode, type, userInfo }
  })
}
