<template>
  <div class="home">
    <van-pull-refresh v-model="isLoading" success-text="刷新成功" @refresh="onRefresh">
      <ios-danger-header class="header_top"/>
      <home-header :banner="banner" />
      <home-module :activity="activity" :hot="hotGoodsList" />
      <home-goods :goods="goodsList" :page="page" @submit="getStatus" />
      <ios-danger-bottom />
    </van-pull-refresh>

    <!-- 协议 -->
    <van-dialog v-model="show" :before-close="agreementClick" title="服务协议和隐私政策" show-cancel-button>
      <div class="agreement">欢迎使用财神商城APP。我们非常重视您的个人信息和隐私政策，在你使用"财神商城"服务之前，请您务必慎重阅读<span style="color: #FF6600" @click="openAgreement('2')">《隐私政策》</span>和<span style="color: #FF6600" @click="openAgreement('1')">《用户协议》</span>，并充分理解协议条款内容。我们将严格按照您同意的的各项条款使用您的个人信息，以便为您提供更好的服务。</div>
    </van-dialog>
  </div>
</template>

<script>
import homeHeader from './tabbar-home-header'
import homeModule from './tabbar-home-module'
import homeGoods from './tabbar-home-goods'

import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import { homeIndex } from '@/api/goods'

import { PullRefresh, Dialog } from 'vant'

export default {
  name: 'Home',
  components: {
    iosDangerHeader,
    iosDangerBottom,
    [homeHeader.name]: homeHeader,
    [homeModule.name]: homeModule,
    [homeGoods.name]: homeGoods,
    [PullRefresh.name]: PullRefresh,
    [Dialog.name]: Dialog
  },
  data() {
    return {
      banner: [],
      activity: [],
      hotGoodsList: [],
      goodsList: [],
      isLoading: false,
      page: false,
      show: true
    }
  },
  beforeRouteLeave(to, from, next) {
    from.meta.scrollTop = document.getElementsByClassName('view-router')[0].scrollTop
    next()
  },
  beforeRouteEnter(to, from, next) {
    const scrollTop = to.meta.scrollTop || 0
    next(vm => {
      setTimeout(() => {
        const router = document.getElementsByClassName('view-router')
        if (router && router.length) {
          router[0].scrollTop = scrollTop
        }
      }, 100)
    })
  },
  created() {
    this.init()
    if (localStorage.getItem('agreement') === 'false') {
      this.show = false
    } else {
      this.show = true
    }
  },
  methods: {
    init() {
      homeIndex().then(res => {
        this.banner = res.data.data.banner
        this.activity = res.data.data.activity
        this.hotGoodsList = res.data.data.hotGoodsList
        this.goodsList = res.data.data.goodsList
      })
    },
    onRefresh() {
      setTimeout(() => {
        homeIndex().then(res => {
          this.isLoading = false
          this.banner = res.data.data.banner
          this.activity = res.data.data.activity
          this.hotGoodsList = res.data.data.hotGoodsList
          this.goodsList = res.data.data.goodsList
          this.page = true
        })
      }, 1000)
    },
    getStatus(val) {
      this.page = val
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
    },
    agreementClick(action, done) {
      if (action === 'confirm') {
        this.show = false
        localStorage.setItem('agreement', false)
      } else {
        navigator.app.exitApp()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.home{
  background-color: #f7f8fa;
  .header_top{
    background-color: #FF4600;
  }
  .agreement{
    width: 90%;
    margin: 20px auto;
  }
}
</style>
