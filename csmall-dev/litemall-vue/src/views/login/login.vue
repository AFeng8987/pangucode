<template>
  <div class="login">
    <ios-danger-header class="top_header"/>
    <div class="login_top_background">
      <van-icon name="cross" class="login_icon" @click="returns" />
    </div>
    <div class="login_box">
      <div class="login_input">
        <van-field v-model="username" class="login_field" size="large" type="number" placeholder="请输入手机号码" clearable>
          <template #left-icon>
            <van-icon :name="phone" class="login_icon" />
          </template>
          <template v-if="!landingType" #button>
            <van-button :disabled="counting" color="#FFF5F5" size="mini" @click="startCountdown">
              <countdown v-if="counting" :time="60000" style="color: #FF6600;font-size:14px;" @end="handleCountdownEnd">
                <template slot-scope="scope">{{ scope.totalSeconds }}S</template>
              </countdown>
              <span v-else style="color: #FF6600;font-size:14px;">获取验证码</span>
            </van-button>
          </template>
        </van-field>
        <!-- 密码 -->
        <van-field v-show="landingType" v-model="password" :type="inputType" size="large" class="login_field field" placeholder="请输入密码" clearable>
          <template #left-icon>
            <van-icon :name="psw" class="login_icon" />
          </template>
          <template #button>
            <van-icon :name="iconName" class="login_field_icon" @click="statusClick" />&nbsp;
            <div class="login_field_font" @click="forgetClick">忘记密码</div>
          </template>
        </van-field>
        <!-- 验证码 -->
        <van-field v-show="!landingType" v-model="code" type="number" size="large" class="login_field field" placeholder="请输入验证码" clearable>
          <template #left-icon>
            <van-icon :name="loginCode" class="login_icon" />
          </template>
        </van-field>

        <van-button :loading="isLogining" type="primary" color="#FF6600" class="login_button" loading-text="登录中..." block @click="loginClick">登录</van-button>
        <van-row class="login_font">
          <van-col :span="11"><span @click="landingClick">{{ landingFont }}</span></van-col>
          <van-col :span="2"><div class="login_font_vertical" /></van-col>
          <van-col :span="11" @click="registerClick">新用户注册</van-col>
        </van-row>

        <div v-if="showWeChat">
          <div class="login_other">其他方式登录</div>
          <van-button type="primary" class="login_button" color="#31CC31" block @click="wxLogin">微信登录</van-button>
        </div>
      </div>

      <footer class="login_agreement">
        <div class="login_terms">登录即代表您已经同意<span style="color: #F24546" @click="openAgreement('1')">(服务协议)</span>和<span style="color: #F24546" @click="openAgreement('2')">(隐私政策)</span></div>
      </footer>
    </div>
  </div>
</template>

<script>
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import { Image, Field, Col, Row } from 'vant'

import phone from '@/assets/images/login_phone.png'
import psw from '@/assets/images/login_password.png'
import loginCode from '@/assets/images/login_code.png'
import display from '@/assets/images/display.png'
import hidden from '@/assets/images/hidden.png'

import { authLogin, authCaptcha, wechatLogin } from '@/api/login'
import { phoneReg } from '@/utils/validate'
import { setLocalStorage } from '@/utils/local-storage'

export default {
  name: 'Login',
  components: {
    iosDangerHeader,
    [Image.name]: Image,
    [Field.name]: Field,
    [Col.name]: Col,
    [Row.name]: Row
  },
  data() {
    return {
      phone,
      psw,
      loginCode,
      display,
      hidden,
      username: '',
      password: '',
      code: '',
      landingType: false,
      inputType: 'password',
      iconName: hidden,
      isLogining: false,
      landingFont: '密码登录',
      counting: false,
      showWeChat: true
    }
  },
  created() {
    // 检测ios是否安装了微信客户端
    this.detectionWeChat()
  },
  methods: {
    detectionWeChat() {
      const _that = this
      if (window.device.platform !== 'Android') {
        this.isInstall().then(success => {
          if (success) {
            _that.showWeChat = true
          } else {
            _that.showWeChat = false
          }
        })
      }
    },
    // 忘记密码
    forgetClick() {
      this.$router.push({
        path: '/login/forget'
      })
    },
    // 密码显示隐藏
    statusClick() {
      if (this.inputType === 'password') {
        this.inputType = 'text'
        this.iconName = display
      } else {
        this.inputType = 'password'
        this.iconName = hidden
      }
    },
    // 登录类型切换
    landingClick() {
      if (this.landingType === false) {
        this.landingType = true
        this.landingFont = '验证码登录'
      } else {
        this.landingType = false
        this.landingFont = '密码登录'
      }
    },
    startCountdown() {
      if (phoneReg.test(this.username)) {
        authCaptcha({ mobile: this.username, type: 'login' }).then(res => {
          this.$toast({ message: '验证码发送成功，请注意查收', position: 'top' })
          this.counting = true
        }).catch(err => {
          this.$toast({ message: err.data.errmsg, position: 'top' })
        })
      } else {
        this.$toast({ message: '您输入的手机号码有误', position: 'top' })
      }
    },
    // 倒计时完成之后的操作
    handleCountdownEnd() {
      this.counting = false
    },
    // 注册
    registerClick() {
      this.$router.push({
        path: '/login/registered',
        query: {
          title: 'register'
        }
      })
    },
    // 登录
    loginClick() {
      this.isLogining = true
      const loginData = this.getLoginData()
      if (loginData !== false) {
        if (this.landingType === false) {
          loginData.type = 'login'
        }
        this.login(loginData)
      } else {
        this.isLogining = false
      }
    },
    // 请求登录接口
    login(row) {
      authLogin(row).then(res => {
        this.isLogining = false
        this.$store.dispatch('updateInfo')
        setLocalStorage({ Authorization: res.data.data.token })
        this.routerRedirect()
      }).catch(err => {
        if (err.data && err.data.errmsg) {
          this.$toast({ message: err.data.errmsg, position: 'top' })
        }
        this.isLogining = false
      })
    },
    returns() {
      this.$router.push({
        name: 'home'
      })
    },
    // 登录之后跳转页面
    routerRedirect() {
      if (this.back) {
        this.$router.back()
      } else {
        this.$router.replace({ path: '/user' })
      }
    },
    // 获取账号/密码/验证码
    getLoginData() {
      if (!phoneReg.test(this.username)) {
        this.$toast('您输入的手机号码有误')
        return false
      }

      if (this.landingType === true) {
        if (this.password.length < 6) {
          this.$toast('您输入的密码有误')
          return false
        }
      } else {
        if (this.code.length !== 6) {
          this.$toast({ message: '您输入的验证码有误', position: 'top' })
          return false
        }
      }

      const username = this.username ? this.username : undefined
      const password = this.password ? this.$md5(this.password.trim()) : undefined
      const code = this.code ? this.code : undefined
      return {
        username: username,
        password: password,
        code: code,
        type: undefined
      }
    },
    // 微信登录
    wxLogin() {
      const _this = this
      this.isInstall().then(success => {
        if (success) {
          _this.$toast.loading({
            mask: true,
            message: '跳转微信登录中...',
            duration: 0
          })
          var scope = 'snsapi_userinfo'
          var state = '_' + (+new Date())
          window.Wechat.auth(scope, state, function(response) {
            wechatLogin({ code: response.code }).then(res => {
              _this.$toast.clear()
              _this.$store.dispatch('updateInfo')
              setLocalStorage({ Authorization: res.data.data.token })
              _this.routerRedirect()
            }).catch(err => {
              if (err.data.errno === 902) {
                setLocalStorage({
                  data: JSON.stringify(err.data.data)
                })
                _this.$toast.clear()
                _this.$router.push({
                  path: '/login/registered',
                  query: {
                    title: 'binding'
                  }
                })
              } else {
                _this.$toast.clear()
                _this.$toast(err.data.errmsg)
              }
            })
          }, function(reason) {
            _this.$toast.clear()
          })
        } else {
          _this.$toast.clear()
          _this.$toast('您未安装微信客户端')
        }
      })
    },
    //  检测微信客户端
    isInstall() {
      return new Promise((resolve, reject) => {
        window.Wechat.isInstalled(function(res) {
          if (res) {
            resolve(true)
          } else {
            resolve(false)
          }
        }, function(error) {
          reject(error)
        })
      })
    },
    openAgreement(reg) {
      if (reg === '1') {
        this.$router.push({
          path: '/user/service'
        })
      } else {
        this.$router.push({
          path: '/user/privacy'
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.login{
  padding: 0;
  .top_header{
    background-color: #ff751a;
  }
  background-color: #F5F5F5;
  .login_top_background{
    background-image: url('../../assets/images/login_background.png');
    width: 100%;
    // height: 270px;
    height: 33vh;
    background-repeat: no-repeat;
    background-size: 100% auto;
    .login_icon{
      font-size: 24px;
      margin: 20px 20px;
      color: #FFFFFF;
    }
  }
  .login_box{
    width: 93%;
    background-color: #FFFFFF;
    margin: -100px auto;
    border-radius: 15px 15px 0 0;
    height: 79vh;
    .login_input{
      width: 90%;
      margin: auto;
      padding-top: 30px;
      .login_field{
        background-color: #FFF5F5;
        border-radius: 10px;
        .login_icon{
          display: inline-block;
          vertical-align: middle;
          img {
            font-size: 22px;
          }
        }
        .login_field_icon{
          font-size: 20px;
          display: inline-block;
          vertical-align: middle;
        }
        .login_field_font{
          color:#F24546;
          display: inline-block;
          vertical-align: middle;
        }
      }
      .field{
        margin-top: 20px;
      }
      .login_button{
        border-radius: 30px;
        width: 90%;
        margin: auto;
        margin-top: 40px;
      }
      .login_font{
        margin: auto;
        margin-top: 16px;
        width: 90%;
        text-align: center;
        font-size: 14px;
        color: #979797;
        .login_font_vertical{
          width: 1px;
          height: 20px;
          background-color: #979797;
          margin: auto;
        }
      }
      .login_other{
        color: #979797;
        text-align: center;
        margin-top: 60px;
      }
    }
    .login_agreement{
      color: #979797;
      position: fixed;
      bottom: 30px;
      z-index: 9999;
      width: 93%;
      .login_terms{
        width: 100%;
        text-align: center;
      }
    }
  }
}
</style>
