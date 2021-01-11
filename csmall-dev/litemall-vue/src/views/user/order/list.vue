<template>
  <div class="orderList">
    <van-sticky>
      <header-bar :border="false" title="我的订单" left-arrow class="top_header"/>
      <van-tabs v-model="active" :swipe-threshold="5" color="#FFFFFF" title-active-color="#FFFFFF" title-inactive-color="#FFFFFF" background="#FF6600" class="tab" @change="switchTab">
        <van-tab v-for="tab in tabs" :title="tab.title" :key="tab.id"/>
      </van-tabs>
    </van-sticky>

    <!-- 下拉刷新 -->
    <van-pull-refresh v-if="!(!list.length && finished)" v-model="refreshing" class="content" @refresh="refresh">
      <van-list v-model="loading" :finished="finished" :immediate-check="false" class="list" finished-text="没有更多了" style="padding-bottom:0.3rem;margin-bottom:.3rem;" @load="load">
        <div v-for="order in list" :key="order.id" class="item">
          <div class="order_status"><status :status="order.status" /></div>
          <div class="goods" @click="detail(order.id)"><goods :data="order.goods" /> </div>
          <div class="total">共计{{ total(order.goods) }}件商品 合计: <span class="red">{{ order.price | yuan }}</span>（优惠{{ order.couponPrice | yuan }}）</div>

          <div slot="footer" class="footer_btns">
            <operator
              :status="order.status"
              :money="order.price"
              :order-id="order.id"
              :order="order"
              @cancel="cancel"
              @confirm="confirm"
              @pay="goPay" />
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- 判断是否有订单 -->
    <van-empty v-show="!loading && !list.length" :image="emptyOrdersBg" description="还未有您的订单，请前往商城购物">
      <van-button round type="info" color="#FF6600" @click="home">去首页选购商品</van-button>
    </van-empty>
    <ios-danger-bottom />
  </div>
</template>

<script>
import { list, changeStatus } from '@/api/order'

import Vue from 'vue'
import { Card, Empty, List, PullRefresh, Sticky, Tab, Tabs, Tag } from 'vant'

import HeaderBar from '@/components/HeaderNavBar'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'
import Goods from './common/goods'
import Operator from './common/operator'

import Status from './list/status'

import emptyOrdersBg from '@/assets/images/empty_orders_bg.png'

Vue.use(Card).use(List).use(PullRefresh).use(Tab).use(Tabs).use(Tag).use(Sticky).use(Empty)

export default {
  components: {
    Status,
    Goods,
    Operator,
    HeaderBar,
    iosDangerBottom
  },
  data() {
    return {
      list: [],
      active: 0,
      emptyOrdersBg,
      loading: true,
      refreshing: false,
      finished: false,
      page: 1,
      pay: {
        money: 0,
        id: null
      },
      confirmList: [],
      tabs: [
        { id: 'all', title: '全部', type: 0 },
        { id: 'paying', title: '待付款', type: 1 },
        { id: 'shipping', title: '待发货', type: 2 },
        { id: 'recieved', title: '待收货', type: 3 },
        { id: 'finished', title: '已完成', type: 4 }
      ]
    }
  },
  watch: {},
  created() {},
  mounted() {
    this.active = parseInt(this.$route.params.active) || 0
    this.load()
  },
  methods: {
    reset() {
      this.finished = false
      this.loading = true
      this.page = 1
    },
    refresh() {
      this.reset()
      this.load()
    },
    load() {
      const type = this.tabs[this.active].type
      const page = this.page
      list({ type, page }).then(({ list, pages }) => {
        this.loading = false
        if (!list) {
          return
        }
        if (this.refreshing) {
          this.list = []
          this.refreshing = false
        }
        this.list.push(...list)
        this.finished = this.page >= pages
        if (!this.finished) {
          this.page++
        }
      })
    },
    switchTab() {
      this.reset()
      this.list = []

      this.load()

      // 能正确返回相应的tab
      this.$router.replace({
        params: {
          active: this.active
        }
      })
    },
    goPay({ money, id }) {
      this.$store.dispatch('payment/show', {
        id, money
      })
    },
    cancel(id) {
      const order = this.list.find(order => order.id === id)
      order.status = 102
    },
    remind(id) {
      console.log(id)
    },
    confirm({ pid, id }) {
      const order = this.list.find(order => order.id === pid)
      changeStatus(order, id, 401)
    },
    detail(id) {
      this.$router.push({ name: 'orderDetail', params: { id }})
    },
    home() {
      this.$router.push({ name: 'home' })
    },
    total(goods) {
      return goods.reduce((prev, next) => prev + next.number, 0)
    }
  }
}
</script>

<style lang="scss" scoped>
.orderList{
  background-color: #FFFFFF;
  .top_header{
    /deep/ div {
      background-color: #FF6600;
    }
    /deep/ i {
      color: #FFFFFF;
    }
    /deep/ .van-nav-bar__title{
      color: #FFFFFF;
      font-size: 16px;
    }
  }
}

.ios {
  padding-top: constant(safe-area-inset-bottom);
    /* Status bar height on iOS 11+ */
  padding-top: env(safe-area-inset-bottom);
}
.item {
  background: #fff;
  border-radius: 10px;
  padding: 10px;
  margin: 10px;
}
.footer_btns {
  margin-top: 10px;
}
.total {
  text-align: right;
  margin: 8px 0 0 10px;
}
.nodata {
    width: 100%;
    height: 100%;
    display: flex;
    padding-top: 2.5rem;
    // align-items: center;
    justify-content: center;
    text-align: center;
    .empty-btn, .empty-font {
      margin: 20px 0;
    }
    .empty-font {
      font-size: 0.4rem;
      color: #999;
    }
}
.red {
  color: #f00;
}
</style>
