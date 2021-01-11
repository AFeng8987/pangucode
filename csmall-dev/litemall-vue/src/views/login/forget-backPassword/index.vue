<template>
  <div class="code">
    <header-bar :border="false" title="手机号找回密码" left-arrow class="top_header"/>
    <div class="code_top_background" />
    <div class="code_box">
      <div class="code_box_input">
        <div class="code_font">验证码已发送到绑定手机号</div>
        <div class="code_phone">{{ getPhone() }}</div>
        <div class="code_input">
          <van-field v-model="code" type="number" class="code_field" placeholder="请输入验证码" clearable>
            <template #left-icon>
              <van-icon :name="loginCode" class="code_icon" />
            </template>
            <template #button>
              <van-button :disabled="counting" color="#FFF5F5" class="code_field_button" size="mini" @click="startCountdown">
                <countdown v-if="counting" :time="60000" style="color: #FF6600;font-size:14px;" @end="handleCountdownEnd">
                  <template slot-scope="scope">{{ scope.totalSeconds }}秒</template>
                </countdown>
                <span v-else style="color: #FF6600;font-size:14px;">获取验证码</span>
              </van-button>
            </template>
          </van-field>

          <van-field v-model="password" :type="inputType" size="large" class="code_field field" placeholder="请输入新密码" clearable>
            <template #left-icon>
              <van-icon :name="psw" class="code_icon" />
            </template>
            <template #right-icon>
              <van-icon :name="iconName" class="code_field_icon" @click="statusClick" />
            </template>
          </van-field>

          <van-field v-model="confirmPassword" :type="inputType" size="large" class="code_field field" placeholder="请确认新密码" clearable>
            <template #left-icon>
              <van-icon :name="psw" class="code_icon" />
            </template>
            <template #right-icon>
              <van-icon :name="iconName" class="code_field_icon" @click="statusClick" />
            </template>
          </van-field>
        </div>

        <van-button :loading="isLogining" loading-text="提交中..." type="primary" color="#FF6600" class="forget_button" block @click="submit">提交</van-button>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Field } from 'vant'
import { phoneReg, pwdReg } from '@/utils/validate'
import { authCaptcha, authReset } from '@/api/login'

import psw from '@/assets/images/login_password.png'
import loginCode from '@/assets/images/login_code.png'
import display from '@/assets/images/display.png'
import hidden from '@/assets/images/hidden.png'

export default {
  name: 'VerificationCode',
  components: {
    HeaderBar,
    [Field.name]: Field
  },
  data() {
    return {
      psw,
      loginCode,
      display,
      hidden,
      code: '',
      counting: false,
      mobile: '',
      password: '',
      confirmPassword: '',
      inputType: 'password',
      iconName: hidden,
      isLogining: false
    }
  },
  created() {
    this.mobile = this.$route.query.mobile
  },
  methods: {
    // 手机号码加****
    getPhone() {
      return this.mobile.replace(this.mobile.substring(3, 7), '****')
    },
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
        authCaptcha({ mobile: this.mobile, type: 'forget' }).then(res => {
          this.$toast({ message: '验证码发送成功，请注意查收', position: 'top' })
          this.counting = true
        }).catch(err => {
          this.$toast({ message: err.data.errmsg, position: 'top' })
        })
      } else {
        this.$toast({ message: '您输入的手机号码有误', position: 'top' })
      }
    },
    // 倒计时完成之后
    handleCountdownEnd() {
      this.counting = false
    },
    submit() {
      this.isLogining = true
      const forgetData = this.getForgetData()
      if (forgetData !== false) {
        authReset(forgetData).then(res => {
          this.isLogining = false
          this.$toast({ message: '修改密码成功，将跳转到登录界面', position: 'top' })
          setTimeout(() => {
            this.$router.push({
              path: '/login'
            })
          }, 2000)
        }).catch(err => {
          this.$toast({ message: err.data.errmsg, position: 'top' })
        })
      } else {
        this.isLogining = false
      }
    },
    // 拼装数据
    getForgetData() {
      if (!phoneReg.test(this.mobile)) {
        this.$toast({ message: '您输入的手机号码有误', position: 'top' })
        return false
      }

      if (this.code.length === 0) {
        this.$toast({ message: '您还未输入验证码', position: 'top' })
        return false
      }

      if (this.code.length !== 6) {
        this.$toast({ message: '您输入的验证码有误', position: 'top' })
        return false
      }

      if (this.password.length === 0 || this.confirmPassword.length === 0) {
        this.$toast({ message: '您还未输入密码', position: 'top' })
        return false
      } else {
        if (this.password !== this.confirmPassword) {
          this.$toast({ message: '您两次输入的密码不一致', position: 'top' })
          return false
        } else {
          if (!pwdReg.test(this.password)) {
            this.$toast({ message: '您输入的密码不能为纯数字或者字母', position: 'top' })
            return false
          }
        }
      }

      const password = this.$md5(this.password)
      const mobile = this.mobile
      const code = this.code
      const type = 'forget'

      return {
        password: password,
        mobile: mobile,
        code: code,
        type: type
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.code{
  background-color: #f7f8fa;
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
  .code_top_background{
    background-color: #FF6600;
    width: 100%;
    height: 250px;
  }
  .code_box{
    width: 93%;
    margin: -200px auto;
    background-color: #FFFFFF;
    border-radius: 10px;
    text-align: center;
    padding-bottom: 50px;
    .code_box_input{
      width: 90%;
      margin: auto;
      padding-top: 20px;
    }
    .code_font{
      margin-top: 20px;
      font-size: 14px;
    }
    .code_phone{
      margin-top: 10px;
      font-size: 14px;
    }
    .code_input{
      margin-top: 40px;
      .code_field{
        background-color: #FFF5F5;
        border-radius: 10px;
        .code_icon{
          display: inline-block;
          vertical-align: middle;
          img {
            font-size: 22px;
          }
        }
        .code_field_button{
          width: 80px;
          border-radius: 5px;
        }
        .code_field_icon{
          font-size: 20px;
        }
      }
      .field{
        margin-top: 30px;
      }
    }
    .forget_button{
      border-radius: 10px;
      margin-top: 40px;
    }
  }
}
</style>
