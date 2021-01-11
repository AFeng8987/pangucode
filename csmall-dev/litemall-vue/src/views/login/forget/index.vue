<template>
  <div class="forget">
    <header-bar :border="false" title="手机号找回密码" left-arrow class="top_header" />
    <div class="forget_top_background" />
    <div class="forget_input">
      <div class="forget_box">
        <van-field v-model="mobile" class="forget_field" size="large" type="number" placeholder="请输入手机号码" clearable>
          <template #left-icon>
            <van-icon :name="phone" class="forget_icon" />
          </template>
        </van-field>

        <van-button :loading="isLogining" loading-text="提交中..." type="primary" color="#FF6600" class="forget_button" block @click="submit">提交</van-button>

        <div class="forget_font">已有账号情况下，才能找回密码</div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Field, Dialog } from 'vant'
import { authVerify } from '@/api/login'
import { phoneReg } from '@/utils/validate'

import phone from '@/assets/images/login_phone.png'

export default {
  name: 'Forget',
  components: {
    HeaderBar,
    [Field.name]: Field,
    Dialog
  },
  data() {
    return {
      mobile: '',
      isLogining: false,
      phone
    }
  },
  created() {},
  methods: {
    submit() {
      this.isLogining = true
      const forgetData = this.getPhoneCheck()
      if (forgetData !== false) {
        authVerify(forgetData).then(res => {
          this.isLogining = false
          Dialog.confirm({
            title: <van-icon class='vanIcon' class-prefix='my-icon' name='lock' />,
            message: '此账号还未注册！',
            showConfirmButton: true,
            showCancelButton: true,
            confirmButtonText: '前往注册',
            confirmButtonColor: '#F24445'
          }).then(() => {
            this.$router.replace({
              path: '/login/registered'
            })
          }).catch(() => {})
        }).catch(err => {
          this.isLogining = false
          if (err.data.errno === 706) {
            this.$router.push({
              path: '/login/backPassword',
              query: forgetData
            })
          } else {
            this.$toast({ message: err.data.errmsg, position: 'top' })
          }
        })
      } else {
        this.isLogining = false
      }
    },
    // 校验手机号码
    getPhoneCheck() {
      if (!this.mobile) {
        this.$toast({ message: '您还未输入手机号码', position: 'top' })
        return false
      }

      if (!phoneReg.test(this.mobile)) {
        this.$toast({ message: '您输入的手机号码有误', position: 'top' })
        return false
      }

      return {
        mobile: this.mobile
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.vanIcon{
  font-size: 30px;
  color: #BBBBBB;
}

.forget{
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
  .forget_top_background{
    background-color: #FF6600;
    width: 100%;
    height: 100px;
  }
  .forget_input{
    width: 90%;
    margin: -80px auto;
    border-radius: 10px;
    background-color: #FFFFFF;
    padding-bottom: 20px;
    .forget_box{
      width: 90%;
      margin: auto;
      padding-top: 30px;
    }
    .forget_field{
      border-radius: 10px;
      background-color: #FFF5F5;
      .forget_icon{
        display: inline-block;
          vertical-align: middle;
          img {
            font-size: 22px;
          }
      }
    }
    .forget_button{
      margin-top: 40px;
      border-radius: 10px;
    }
    .forget_font{
      margin-top: 10px;
      color: #000000;
      font-size: 12px;
    }
  }
}
</style>
