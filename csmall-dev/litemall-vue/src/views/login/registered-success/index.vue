<template>
  <div class="success">
    <van-nav-bar title="注册成功" />
    <div class="success_image">
      <van-image :src="successIcon" class="success_icon" />
      <div class="success_font">恭喜您已经注册成功～</div>
      <van-button type="primary" color="#FEC000" class="success_button" block @click="submit">前往登录</van-button>
    </div>
  </div>
</template>

<script>
import { NavBar, Image } from 'vant'
import successIcon from '@/assets/images/register_success.png'

export default {
  name: 'RegisteredSuccess',
  components: {
    [NavBar.name]: NavBar,
    [Image.name]: Image
  },
  data() {
    return {
      successIcon
    }
  },
  methods: {
    submit() {
      this.$router.push({
        path: '/login'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.success{
  background-color: #f7f8fa;
  .success_image{
    width: 90%;
    margin: 40px auto;
    text-align: center;
    .success_icon{
      width: 60px;
      height: 60px;
    }
    .success_font{
      margin-top: 30px;
      font-size: 16px;
      color: #F24445;
    }
    .success_button{
      margin-top: 50px;
      border-radius: 10px;
    }
  }
}
</style>
