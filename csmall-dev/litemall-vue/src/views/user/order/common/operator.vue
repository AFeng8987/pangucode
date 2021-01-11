<template>
  <div class="operate">
    <div class="btns">
      <van-button v-if="show(['paying', 'payed','shipping','finished','closed'])" size="small" class="operate_btn" @click="showCS">
        联系客服
      </van-button>
      <van-button v-if="show('paying')" size="small" class="operate_btn" @click="cancel">
        取消订单
      </van-button>
      <van-button v-if="show('paying')" size="small" class="operate_btn" type="danger" @click="pay">去支付
      </van-button>
      <van-button v-if="show('payed')" class="operate_btn" size="small" color="#febf00" @click="remind">提醒发货
      </van-button>
      <van-button v-if="show('shipping')" size="small" class="operate_btn" color="#febf00" type="danger" @click="confirm('confirm')">确认收货
      </van-button>
      <van-button v-if="show('shipping')" size="small" class="operate_btn" @click="confirm('lo')">查看物流
      </van-button>
      <!--
      <van-button v-if="show(['cancelled', 'finished'])" size="small" class="operate_btn" @click="buyAgain">再次购买
      </van-button> -->
    </div>
    <van-popup v-model="orderListPop" position="bottom">
      <van-cell-group class="list">
        <van-cell
          v-for="order in orderList"
          :key="order.id"
          :value="order.value"
          center
          is-link
          @click="sendConfirm(order.id)"
        >
          <template #title>
            <p v-for="des in order.desc" :key="des">{{ des }}</p>
          </template>
        </van-cell>
      </van-cell-group>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import { Cell, CellGroup, Popup } from 'vant'

import { remind, cancel, confirm } from '@/api/order'
import orderStatus from '@/utils/orderStatus'

Vue.use(Popup).use(Cell).use(CellGroup)

export default {
  components: {
  },
  props: {
    order: {
      type: Object,
      default: () => {}
    },
    status: {
      type: Number,
      default: 0
    },
    money: {
      type: Number,
      default: 0
    },
    orderId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      orderStatus,
      orderListPop: false,
      orderList: [],
      payMoney: 0
    }
  },
  computed: {
  },
  watch: {},
  created() {},
  mounted() {
  },
  methods: {
    show(types) {
      if (!Array.isArray(types)) {
        types = [types]
      }
      return types.includes(this.orderStatus['order'][this.status])
    },
    showCS() {
      this.$store.dispatch('cs/show')
    },
    showLogistics(id) {
      this.$store.dispatch('logistics/show', id)
    },
    cancel() {
      this.$dialog.confirm({
        title: <van-icon class='vanIcon' class-prefix='my-icon' name='lock' />,
        message: '是否确认取消订单？',
        showConfirmButton: true,
        showCancelButton: true,
        confirmButtonText: '确认取消',
        cancelButtonText: '暂不取消',
        theme: 'round-button'
      }).then(() => {
        cancel(this.orderId).then(data => {
          this.$toast('订单已取消')
          this.$emit('cancel', this.orderId)
        })
      }).catch(() => {})
    },
    pay() {
      this.payMoney = this.money
      // this.$emit('pay', { money: this.money, id: this.orderId })
      this.$store.dispatch('payment/show', {
        id: this.orderId, money: this.money
      })
    },
    remind() {
      remind(this.orderId).then(data => {
        this.$toast('提醒已发出')
        this.$emit('remind', this.orderId)
      })
    },
    logistics() {

    },
    clickOrderListItem(id) {
      if (this.confirmType === 'lo') {
        this.showLogistics(id)
      } else if (this.confirmType === 'confirm') {
        this.sendConfirm(id)
      }
    },
    confirm(type) {
      this.confirmType = type
      if (this.order.cid.length === 1) {
        return this.clickOrderListItem(this.order.cid[0].id)
      }
      this.orderListPop = true
      this.orderList = this.order.cid.map(item => {
        const goods = this.order.goods.filter(good => item.goid.includes(good.orderGoodsId))
        return {
          id: item.id,
          desc: goods.map(item => `${item.goodsName}: ${item.specifications.join(',')} 数量：${item.number}`),
          value: '总共：' + goods.reduce((prev, curr) => prev + curr.number, 0)
        }
      })
    },
    sendConfirm(id) {
      confirm(id).then(data => {
        this.$toast('已确认收货')
        this.$emit('confirm', { pid: this.orderId, id })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.operate .btns {
  text-align: right;
}
.operate_btn {
  margin: 0 5px;
  border-radius: 6px;
}
</style>
