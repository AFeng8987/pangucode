<template>
  <div class="coupon">
    <van-coupon-cell :coupons="coupons" :chosen-coupon="chosenCoupon" @click="showList = true" />

    <!-- 优惠券列表 -->
    <van-popup v-model="showList" round position="bottom" style="height: 80%; padding-top: 4px;">
      <van-coupon-list :show-exchange-bar="false" :coupons="coupons" :chosen-coupon="chosenCoupon" @change="onChange" />
    </van-popup>
  </div>
</template>

<script>
import { CouponCell, CouponList } from 'vant'

import { selectList } from '@/api/cart'

export default {
  name: 'ConfirmCoupon',
  components: {
    [CouponCell.name]: CouponCell,
    [CouponList.name]: CouponList
  },
  data() {
    return {
      showList: false,
      coupons: [],
      disabledCoupons: [],
      chosenCoupon: -1
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      selectList().then(res => {
        this.getData(res.data.data.list)
      })
    },
    getData(obj) {
      this.coupons = []
      obj.map(item => {
        const couponData = {}
        couponData.id = item.id
        couponData.name = item.name
        couponData.condition = item.desc
        couponData.startAt = Math.round(new Date(item.startTime).getTime() / 1000)
        couponData.endAt = Math.round(new Date(item.endTime).getTime() / 1000)
        couponData.value = item.discount * 100

        couponData.valueDesc = item.discount
        couponData.unitDesc = '元'

        this.coupons.push(couponData)
      })
    },
    onChange(index) {
      this.showList = false
      this.chosenCoupon = index
      this.$emit('submit', this.couponData(index))
    },
    couponData(index) {
      let data = {}
      this.coupons.map((item, vv) => {
        if (index === vv) {
          data = item
        }
      })

      return data
    }
  }
}
</script>

<style lang="scss" scoped>
.coupon{
  width: 95%;
  margin: auto;
  margin-top: 10px;
}
</style>
