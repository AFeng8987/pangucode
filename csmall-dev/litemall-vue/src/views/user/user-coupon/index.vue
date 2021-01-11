<template>
  <div class="coupon">
    <van-sticky>
      <header-bar title="优惠券" left-arrow />

      <van-tabs v-model="activeIndex" @change="clickTab">
        <van-tab v-for="(item, index) in tabTitles" :key="index" :title="item" />
      </van-tabs>
    </van-sticky>

    <van-list v-model="loading" :finished="finished" :immediate-check="false" finished-text="没有更多了" @load="getCouponList">
      <div class="coupon_content">
        <div v-for="item in list" :key="item.id" class="coupon_item">
          <van-row type="flex" justify="center" align="center">
            <van-col span="10" class="coupon_left">
              <h2><span>¥</span> {{ item.discount }}元</h2>
              <p>{{ item.desc }}-{{ item.tag }}</p>
            </van-col>
            <van-col span="14" class="coupon_right">
              <h2>{{ item.name }}</h2>
              <p>有效期至: {{ item.endTime }}</p>
            </van-col>
          </van-row>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Tab, Tabs, Sticky, List, Col, Row } from 'vant'

import { couponList } from '@/api/user'

export default {
  name: 'Coupon',
  components: {
    HeaderBar,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Sticky.name]: Sticky,
    [List.name]: List,
    [Col.name]: Col,
    [Row.name]: Row
  },
  props: {
    active: {
      type: [String, Number],
      default: 0
    }
  },
  data() {
    return {
      activeIndex: Number(this.active),
      tabTitles: ['未使用', '已使用', '已过期'],
      loading: false,
      finished: false,
      list: [],
      page: 0,
      limit: 10
    }
  },
  created() {
    this.getinit()
  },
  methods: {
    getinit() {
      this.page = 0
      this.list = []
      this.getCouponList()
    },
    clickTab() {
      this.getinit()
      this.$router.replace({ params: { active: this.activeIndex }})
    },
    getCouponList() {
      this.page++

      couponList(this.page, this.limit, this.activeIndex).then(res => {
        if (!res.list) {
          return
        }

        this.list.push(...res.list)
        this.finished = this.page >= res.pages
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.coupon{
  background-color: #f7f8fa;
  .coupon_content{
    background-color: #ffffff;
    padding: 15px 10px 5px 10px;
    .coupon_item{
      border: 1px solid red;
      border-radius: 5px;
      margin-bottom: 15px;
      .coupon_left{
        text-align: center;
        h2 {
          color: red;
          font-size: 24px;
          margin: 15px 0;
          span {
            font-size: 12px;
          }
        }
        p {
          color: #969799;
        }
      }
      .coupon_right{
        text-align: center;
        h2 {
          height: 32px;
          line-height: 32px;
        }
        p {
          color: #969799;
        }
      }
    }
  }
}
</style>
