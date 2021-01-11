<template>
  <div class="coupom">
    <van-cell-group>
      <van-cell title="我的优惠券" is-link>
        <router-link to="/user/coupon/list/0" class="text-desc">全部优惠券</router-link>
      </van-cell>
    </van-cell-group>

    <van-grid :column-num="3" :border="false">
      <van-grid-item
        v-for="item in states"
        :key="item.name"
        :icon="item.name"
        :text="item.title"
        :to="{name: 'couponList', params: {active: item.key}}"
        class="status_item"
        icon-prefix="my-icon" />
    </van-grid>

  </div>
</template>

<script>
import Vue from 'vue'
import { Row, Col, Grid, GridItem } from 'vant'

Vue.use(Grid).use(GridItem)

export default {
  name: 'CouponGroup',
  components: {
    [Row.name]: Row,
    [Col.name]: Col
  },
  data() {
    return {
      states: [
        { title: '待使用', name: 'coupon', key: '0' },
        { title: '已使用', name: 'coupon-used', key: '1' },
        { title: '过期关闭', name: 'coupon-due', key: '2' }
      ]
    }
  }
}
</script>

<style scoped lang="scss">
.status_item {
  color: #ff4600;
}
</style>
