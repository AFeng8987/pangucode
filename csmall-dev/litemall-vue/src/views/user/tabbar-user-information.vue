<template>
  <div class="coupon_header">
    <div class="coupon_titel">
      <div class="coupon_my">信息中心</div>
    </div>

    <div class="coupon_icon">
      <van-grid :border="false" class="coupon_grid">
        <van-grid-item :icon="shouhuodizhi" @click="$router.push({path: '/user/addressList'})">
          <span slot="text" class="coupon_name">收货地址</span>
        </van-grid-item>
        <van-grid-item :icon="extension" @click="$router.push({path: '/user/extension', query: { code: code }})">
          <span slot="text" class="coupon_name">推广</span>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<script>
import { Grid, GridItem } from 'vant'
import shouhuodizhi from '@/assets/images/shouhuodizhi.png'
import extension from '@/assets/images/extension.png'

export default {
  name: 'CouponGroup',
  components: {
    [Grid.name]: Grid,
    [GridItem.name]: GridItem
  },
  props: {
    code: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      shouhuodizhi,
      extension
    }
  }
}
</script>

<style lang="scss" scoped>
.coupon_header{
  background-color: #ffffff;
  margin-top: 10px;
  .coupon_titel{
    width: 90%;
    margin: auto;
    padding-top: 14px;
    .coupon_my{
      font-size: 15px;
    }
  }
  .coupon_icon{
    margin-top: 5px;
    padding-bottom: 12px;
    .coupon_grid{
      .coupon_name {
        padding-top: 6px;
        color: #000000;
      }
      /deep/ .van-icon{
        font-size: 36px;
      }
    }
  }
}
</style>
