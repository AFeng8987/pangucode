<template>
  <div class="orderStatus">
    <span>{{ label }}</span>
  </div>
</template>

<script>
import orderStatus from '@/utils/orderStatus'

export default {
  props: {
    status: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      labels: {
        'paying': '等待付款',
        'cancelled': '订单已取消',
        'payed': '等待卖家发货',
        'shipping': '等待收货',
        'finished': '交易结束',
        'closed': '订单已关闭'
      }
    }
  },
  computed: {
    label() {
      return this.labels[orderStatus['order'][this.status]]
    }
  }
}
</script>
<style lang="scss" scoped>
.orderStatus {
  text-align: right;
  margin-bottom: 10px;
  margin-right: 5px;
  color: #febf00;
}
</style>
