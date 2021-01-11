<template>
  <div class="order_header">
    <div class="order_titel">
      <div class="order_my">我的订单</div>
      <router-link :to="{name: 'order'}" class="order_router">
        <span class="order_router_font">全部订单</span>
        <van-icon class="order_router_icon" name="arrow" />
      </router-link>
    </div>

    <div class="order_icon">
      <van-grid :border="false" class="order_grid" >
        <van-grid-item v-for="item in orderButtons" :key="item.id" :icon="item.icon" :to="{name: 'order', params: {active: item.type}}" :info="order[item.badge] > 0 ? order[item.badge] : ''" >
          <span slot="text" class="order_zi">{{ item.label }}</span>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<script>
import { Grid, GridItem } from 'vant'
import paying from '@/assets/images/daifukuan.png'
import shipping from '@/assets/images/daifahuo.png'
import receiving from '@/assets/images/daishouhuo.png'
import postsale from '@/assets/images/tuikuan.png'

export default {
  name: 'OrderGroup',
  components: {
    [Grid.name]: Grid,
    [GridItem.name]: GridItem
  },
  props: {
    order: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      orderButtons: [
        { id: 'paying', label: '待付款', type: 1, icon: paying, badge: 'unpaid' },
        { id: 'shipping', label: '待发货', type: 2, icon: shipping, badge: 'unship' },
        { id: 'receiving', label: '待收货', type: 3, icon: receiving, badge: 'unrecv' },
        { id: 'postsale', label: '已完成', type: 4, icon: postsale }
      ]
    }
  }

}
</script>

<style lang="scss" scoped>
.order_header{
  background-color: #ffffff;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  margin-top: -30px;
  .order_titel{
    width: 90%;
    margin: auto;
    padding-top: 14px;
    .order_my{
      font-size: 14px;
      display: inline-block;
      vertical-align: middle;
      width: 50%;
    }
    .order_router{
      font-size: 13px;
      display: inline-block;
      vertical-align: middle;
      color: #BCBCBC;
      width: 50%;
      text-align: right;
      .order_router_font{
        vertical-align: middle;
        padding-right: 2px;
      }
      .order_router_icon{
        vertical-align: middle;
        font-size: 14px;
      }
    }
  }
  .order_icon{
    margin-top: 5px;
    padding-bottom: 12px;
    .order_grid{
      .order_zi{
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
