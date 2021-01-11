<template>
  <div class="register">
    <header-bar :title="textMap[navBarStatus]" :border="false" left-arrow class="top_header"/>
    <div class="login_top_background" />
    <div class="register_input">
      <div class="register_box">
        <van-field v-model="mobile" class="register_field" size="large" type="number" placeholder="请输入手机号码" clearable>
          <template #left-icon>
            <van-icon :name="phone" class="register_icon" />
          </template>
          <template #button>
            <van-button :disabled="counting" color="#FFF5F5" size="mini" @click="startCountdown">
              <countdown v-if="counting" :time="60000" style="color: #FF6600;font-size:14px;" @end="handleCountdownEnd">
                <template slot-scope="scope">{{ scope.totalSeconds }}S</template>
              </countdown>
              <span v-else style="color: #FF6600;font-size:14px;">获取验证码</span>
            </van-button>
          </template>
        </van-field>

        <van-field v-model="verification" size="large" class="register_field number" type="number" placeholder="请输入验证码" clearable>
          <template #left-icon>
            <van-icon :name="loginCode" class="register_icon" />
          </template>
        </van-field>

        <van-field v-if="navBarStatus === 'register'" v-model.trim="password" :type="inputType" size="large" class="register_field number" placeholder="请输入登录密码" clearable>
          <template #left-icon>
            <van-icon :name="psw" class="register_icon" />
          </template>
          <template #right-icon>
            <van-icon :name="iconName" class="register_field_icon" @click="statusClick" />
          </template>
        </van-field>
        <van-field v-if="navBarStatus === 'register'" v-model.trim="confirmPassword" :type="inputType" size="large" class="register_field number" placeholder="请确认登录密码" clearable>
          <template #left-icon>
            <van-icon :name="psw" class="register_icon" />
          </template>
          <template #right-icon>
            <van-icon :name="iconName" class="register_field_icon" @click="statusClick" />
          </template>
        </van-field>

        <van-field v-model.trim="invitationCode" class="register_field number" size="large" placeholder="推荐码" clearable>
          <template #left-icon>
            <van-icon :name="loginRecommend" class="register_icon" />
          </template>
        </van-field>

        <van-button v-if="navBarStatus === 'register'" :disabled="!checked" :loading="isLogining" color="#FF6600" loading-text="提交中..." class="register_button" block @click="submit">提交</van-button>
        <van-button v-else :loading="isLogining" :disabled="!checked" color="#FF6600" loading-text="提交中..." class="register_button" block @click="bindingClick">同意协议并注册</van-button>

        <div class="register_font">
          <div class="register_checkbox"><van-checkbox v-model="checked" icon-size="16px" checked-color="red">已阅读并同意以下协议</van-checkbox></div>
          <div class="register_agreement"><span @click="openAgreement('1')">服务协议</span>、<span @click="openAgreement('2')">隐私条款</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Field, Checkbox } from 'vant'
import { phoneReg, pwdReg } from '@/utils/validate'
import { authCaptcha, authCreate, wechatCheckCaptcha } from '@/api/login'
import { setLocalStorage, getLocalStorage, removeLocalStorage } from '@/utils/local-storage'

import phone from '@/assets/images/login_phone.png'
import psw from '@/assets/images/login_password.png'
import loginCode from '@/assets/images/login_code.png'
import loginRecommend from '@/assets/images/login_recommend.png'
import display from '@/assets/images/display.png'
import hidden from '@/assets/images/hidden.png'

export default {
  name: 'Registered',
  components: {
    HeaderBar,
    [Field.name]: Field,
    [Checkbox.name]: Checkbox
  },
  data() {
    return {
      phone,
      psw,
      loginCode,
      loginRecommend,
      display,
      hidden,
      mobile: '',
      verification: '',
      password: '',
      confirmPassword: '',
      invitationCode: '',
      counting: false,
      inputType: 'password',
      iconName: hidden,
      isLogining: false,
      navBarStatus: '',
      textMap: {
        register: '手机号快速注册',
        binding: '绑定手机号码'
      },
      checked: false
    }
  },
  created() {
    this.invitationCode = this.$route.query.inviteCode
    this.navBarStatus = this.$route.query.title
  },
  methods: {
    // 密码显示隐藏切换
    statusClick() {
      if (this.inputType === 'password') {
        this.inputType = 'text'
        this.iconName = display
      } else {
        this.inputType = 'password'
        this.iconName = hidden
      }
    },
    // 获取验证码
    startCountdown() {
      if (phoneReg.test(this.mobile)) {
        let countingType = 'register'
        if (this.navBarStatus === 'binding') {
          countingType = 'binding'
        }
        authCaptcha({ mobile: this.mobile, type: countingType }).then(res => {
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
    // 提交
    submit() {
      this.isLogining = true
      const registerData = this.getRegisterData()
      if (registerData !== false) {
        authCreate(registerData).then(res => {
          this.$router.replace({
            path: '/login/success'
          })
          this.isLogining = false
        }).catch(err => {
          this.$toast({ message: err.data.errmsg, position: 'top' })
          this.isLogining = false
        })
      } else {
        this.isLogining = false
      }
    },
    // 微信登录并绑定手机号码
    bindingClick() {
      this.isLogining = true
      const registerData = this.getRegisterData()
      if (registerData !== false) {
        const userInfo = getLocalStorage('data')
        registerData.userInfo = JSON.parse(userInfo.data)
        wechatCheckCaptcha(registerData).then(res => {
          removeLocalStorage('data')
          setLocalStorage({ Authorization: res.data.data.token })
          this.$router.replace({ path: '/user' })
          this.isLogining = false
        }).catch(err => {
          this.$toast(err)
          this.isLogining = false
        })
      } else {
        this.isLogining = false
      }
    },
    // 获取手机号码/验证码
    getRegisterData() {
      if (!phoneReg.test(this.mobile)) {
        this.$toast({ message: '您输入的手机号码有误', position: 'top' })
        return false
      }

      if (this.verification.length !== 6) {
        this.$toast({ message: '您输入的验证码有误', position: 'top' })
        return false
      }

      if (this.navBarStatus === 'register') {
        if (this.password !== this.confirmPassword) {
          this.$toast({ message: '您两次输入的密码不一致', position: 'top' })
          return false
        } else {
          if (!pwdReg.test(this.password)) {
            this.$toast({ message: '您输入的密码不能为纯数字或者字母并且长度为6-20位', position: 'top' })
            return false
          }
        }
      }

      const mobile = this.mobile
      const code = this.verification
      const password = this.navBarStatus === 'register' ? this.$md5(this.password) : undefined
      const invitationCode = this.invitationCode
      const type = this.navBarStatus === 'register' ? 'register' : 'binding'
      return {
        mobile: mobile,
        code: code,
        password: password,
        invitationCode: invitationCode,
        type: type
      }
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
.register{
  background-color: #F5F5F5;
  padding: 0;
  .top_header{
    /deep/ div {
      background-color: #FF6600;
    }
    /deep/ i {
      color: #FFFFFF;
    }
    /deep/ .van-nav-bar__title{
      color: #FFFFFF;
      font-family: Helvetica;
      text-shadow: 0px 2px 4px rgba(0, 0, 0, 0.5);
    }
  }
  .login_top_background{
    background-color: #FF6600;
    width: 100%;
    height: 250px;
  }
  .register_input{
    width: 93%;
    margin: -170px auto;
    background-color: #FFFFFF;
    border-radius: 10px;
    .register_box{
      width: 90%;
      margin: auto;
      padding-top: 30px;
      .register_field{
        background-color: #FFF5F5;
        border-radius: 10px;
        .register_icon{
          display: inline-block;
          vertical-align: middle;
          img {
            font-size: 22px;
          }
        }
        .register_field_icon{
          font-size: 20px;
        }
      }
      .number{
        margin-top: 20px;
      }
      .register_button{
        margin-top: 25px;
        border-radius: 30px;
      }
      .register_font{
        width: 90%;
        margin: 15px auto;
        color: #9C9696;
        padding-bottom: 26px;
        .register_checkbox{
          display: inline-block;
          vertical-align: middle;
        }
        .register_agreement{
          color: #FF432D;
          display: inline-block;
          vertical-align: middle;
          margin-left: 10px;
        }
      }
    }
  }
}
</style>
