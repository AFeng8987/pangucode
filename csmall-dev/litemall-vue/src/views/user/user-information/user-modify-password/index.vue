<template>
  <div class="modify">
    <header-bar title="修改密码" left-arrow />

    <div class="modify_input">

      <div class="modify_font">请获取验证码</div>
      <div class="modify_phone">{{ getPhone() }}</div>

      <div class="modify_box">
        <van-field v-model="code" type="number" class="modify_field" placeholder="请输入验证码" clearable>
          <template #button>
            <van-button :disabled="counting" :color="colorType" class="modify_field_button" size="small" @click="startCountdown">
              <countdown v-if="counting" :time="60000" @end="handleCountdownEnd">
                <template slot-scope="scope">{{ scope.totalSeconds }}秒</template>
              </countdown>
              <span v-else>获取验证码</span>
            </van-button>
          </template>
        </van-field>

        <van-field v-model="password" :type="inputType" class="modify_field field" placeholder="请输入新密码">
          <template #right-icon>
            <van-icon :name="iconName" class="modify_field_icon" @click="statusClick" />
          </template>
        </van-field>

        <van-field v-model="confirmPassword" :type="inputType" class="modify_field field" placeholder="请确认新密码">
          <template #right-icon>
            <van-icon :name="iconName" class="modify_field_icon" @click="statusClick" />
          </template>
        </van-field>
      </div>

      <van-button :loading="isLogining" loading-text="登录中..." color="#FEC000" class="modify_button" type="primary" block @click="submit">提交</van-button>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Field, Button } from 'vant'
import { phoneReg, pwdReg } from '@/utils/validate'
import { authCaptcha, authReset } from '@/api/login'

export default {
  name: 'ModifyPassword',
  components: {
    HeaderBar,
    [Field.name]: Field,
    [Button.name]: Button
  },
  data() {
    return {
      code: '',
      password: '',
      confirmPassword: '',
      mobile: '',
      isLogining: false,
      inputType: 'password',
      iconName: 'closed-eye',
      counting: false,
      colorType: '#ff0102'
    }
  },
  created() {
    this.mobile = this.$route.query.mobile
  },
  methods: {
    statusClick() {
      if (this.inputType === 'password') {
        this.inputType = 'text'
        this.iconName = 'eye-o'
      } else {
        this.inputType = 'password'
        this.iconName = 'closed-eye'
      }
    },
    // 获取验证码
    startCountdown() {
      if (phoneReg.test(this.mobile)) {
        authCaptcha({ mobile: this.mobile, type: 'forget' }).then(res => {
          this.$toast({ message: '验证码发送成功，请注意查收', position: 'top' })
          this.counting = true
          this.colorType = '#999'
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
      this.colorType = '#ff0102'
    },
    // 手机号码加****
    getPhone() {
      return this.mobile.replace(this.mobile.substring(3, 7), '****')
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
.modify{
  background-color: #f7f8fa;
  .modify_input{
    width: 90%;
    margin: 40px auto;
    text-align: center;
    .modify_font{
      margin-top: 20px;
      font-size: 14px;
    }
    .modify_phone{
      margin-top: 10px;
      font-size: 14px;
    }
    .modify_box {
      margin-top: 20px;
      .modify_field{
        border-radius: 10px;
        .modify_field_button{
          width: 80px;
          border-radius: 5px;
        }
        .modify_field_icon{
          font-size: 20px;
        }
      }
      .field{
        margin-top: 30px;
      }
    }
    .modify_button{
      margin-top: 30px;
      border-radius: 10px;
    }
  }
}
</style>
