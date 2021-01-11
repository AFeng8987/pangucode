<template>
  <div class="orderStatus">
    <van-row type="flex" gutter="10" class="status_pannel">
      <van-col :span="16" class="left">
        <h3>{{ status.label }}</h3>
        <div>{{ status.content }}</div>
      </van-col>
      <van-col :span="8" class="right">
        <img :src="status.img" style="width: 60px" >
      </van-col>
    </van-row>
  </div>
</template>

<script>
import Vue from 'vue'
import { Row, Col } from 'vant'
Vue.use(Row).use(Col)

import orderStatus from '@/utils/orderStatus'

import PayingIcon from '@/assets/images/order_detail_paying.png'
import PayedIcon from '@/assets/images/order_detail_payed.png'
import ShippingIcon from '@/assets/images/order_detail_shipping.png'
import FinishedIcon from '@/assets/images/order_detail_finished.png'
import CancelledIcon from '@/assets/images/order_detail_cancelled.png'

export default {
  components: {},
  props: {
    type: {
      type: String,
      default: 'order'
    },
    reject: {
      type: String,
      default: ''
    },
    value: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      orderStates: {
        paying: { label: '等待买家付款', content: '', img: PayingIcon },
        payed: { label: '买家已付款', content: '请等待卖家发货', img: PayedIcon },
        shipping: { label: '卖家已发货', content: '货物已经在路上，收到货请及时确认收货', img: ShippingIcon },
        finished: { label: '交易完成', content: '', img: FinishedIcon },
        cancelled: { label: '交易已取消', content: '', img: CancelledIcon }
      },
      saledStates: {
        notapply: { label: '尚未申请售后', content: '', img: PayedIcon },
        returning: { label: '卖家退货审核中', content: '申请已提交，请耐心等待', img: PayedIcon },
        applied: { label: '卖家正在审核退款', content: '如需退货请填写物流单号', img: PayedIcon },
        refund_reject: { label: '商家驳回申请', content: this.reject, img: PayedIcon },
        refunding: { label: '商家已同意退款', content: '资金将退回支付原路', img: PayedIcon },
        refunded: { label: '退款成功', content: '', img: PayedIcon },
        cancelled: { label: '用户取消申请', content: '', img: PayedIcon },
        failure: { label: '退款失败', content: '', img: PayedIcon }
      }
    }
  },
  computed: {
    status() {
      if (!this.value) {
        return {}
      }
      const states = this[`${this.type}States`]
      console.log(states, orderStatus[this.type][this.value], this.type, this.value)
      return states[orderStatus[this.type][this.value]]
    }
  }
}
</script>
<style lang="scss" scoped>
.status_pannel {
  align-items: center;
  padding: 5px 20px 20px 20px;
  color: #fff;
  .left {
    padding-left: 30px;
    h3 {
      font-size: 0.5rem;
      margin: 10px 0;
    }
  }
}
</style>
