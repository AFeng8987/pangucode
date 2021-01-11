<template>
  <div class="orderDetail">
    <div class="header_pannel">
      <header-bar :border="false" :transparent="true" class="header" title="订单详情" left-arrow />
      <status v-model="info.orderStatus" />
    </div>
    <van-card
      :desc="info.address"
      class="order_addr">
      <template #thumb>
        <div class="location"><van-icon name="location" color="#fff" size="0.6rem"/></div>
      </template>
      <template #title>
        <div class="title"><span>{{ info.consignee }}</span> <span> {{ info.mobile }} </span></div>
      </template>
    </van-card>
    <logistics />
    <div class="good_panel">
      <goods :data="goods">
        <template #footer="{ good }">
          <van-button v-if="isSaled(good)" size="mini" @click="saled(good)">{{ saledLabel(good) }}</van-button>
        </template>
      </goods>
      <van-cell-group class="price_info">
        <van-cell :value="order.goodsPrice | yuan" :border="false" class="secondary_info" title="商品总价" />
        <van-cell :value="order.freightPrice | yuan" :border="false" class="secondary_info" title="商品运费" />
        <van-cell :value="order.couponPrice | yuan | minus" class="secondary_info" title="商品优惠" />
        <van-cell :value="order.actualPrice | yuan" title="实付款" />
      </van-cell-group>
    </div>
    <div class="order_info">
      <van-cell-group>
        <van-cell title="订单信息" icon="info" />
        <van-cell :value="info.message" title="订单备注" />
        <van-cell :value="id" title="订单编号" title-class="info_title" />
        <van-cell :value="info.payId" title="支付交易号" title-class="info_title" />
        <van-cell :value="info.addTime" title="创建时间" title-class="info_title" />
      </van-cell-group>
    </div>
    <div class="operator">
      <operator
        :status="info.orderStatus"
        :money="info.actualPrice"
        :order-id="info.payOrderSn"
        :order="order"
        @cancel="cancel"
        @confirm="confirm"
        @pay="goPay" />
    </div>
  </div>
</template>

<script>
import { detail, changeStatus } from '@/api/order'

import Vue from 'vue'
import { Row, Col, Card, CellGroup, Cell } from 'vant'
Vue.use(Row).use(Col).use(Card).use(CellGroup).use(Cell)

import HeaderBar from '@/components/HeaderNavBar'
import Operator from './common/operator'
import Status from './common/status'
import Address from './detail/address'
import Logistics from './detail/logistics'
import Goods from './common/goods'

export default {
  components: {
    Status, Address, Logistics, Goods, HeaderBar, Operator
  },
  data() {
    return {
      id: 0,
      goods: [],
      order: {},
      info: {}
    }
  },
  // computed: {
  //   isSaled() {
  //     return [301, 401, 402].indexOf(this.info.orderStatus)
  //   }
  // },
  watch: {},
  mounted() {
    this.id = this.$route.params.id
    this.load()
  },
  methods: {
    load() {
      detail(this.id).then((order) => {
        console.log(order)
        this.goods = order.goods
        Object.keys(order).forEach((key) => this.$set(this.order, key, order[key]))
        Object.keys(order.info).forEach((key) => this.$set(this.info, key, order.info[key]))
      })
    },
    cancel() {
      this.info.orderStatus = 102
    },
    goPay() {

    },
    confirm({ pid, id }) {
      changeStatus(this.order, id, 401)
    },
    saled(good) {
      const id = good.id
      const isRefund = good.aftersaleStatus !== 0
      const name = isRefund ? 'saledDetail' : 'saledApply'

      this.$router.push({ name, params: { good, gid: id, oid: this.id }})
    },
    isSaled(good) {
      return [201, 301, 401, 402].indexOf(this.info.orderStatus) !== -1 || (this.info.orderStatus === 501 && good.aftersaleStatus !== 0)
    },
    saledLabel(good) {
      let label = '申请售后'
      // aftersaleStatus
      // 0    未申请
      // 1    退货审核通过
      // 2    退货审核驳回
      // 3    退款中
      // 4    退款驳回
      // 5    退款成功
      // 6    退款失败
      // 7    用户取消
      // 11   待收货
      // 12   确认收货
      if (!good.aftersaleStatus) {
        label = '申请售后'
      } else if (good.aftersaleStatus === 5) {
        label = '退款成功'
      } else {
        label = '查看售后'
      }
      // todo 获取售后状态
      return label
    }
  }
}
</script>
<style lang="scss" scoped>
.header_pannel {
  background-color: #f24445;
}
.order_addr {
  margin-top: 0;
  .title {
    // font-size: 0.6rem;
    margin-bottom: 10px;
  }
  .van-card__header {
    align-items: center;
  }
  .van-card__thumb {
    height: auto;
  }
  .van-card__content {
    min-height: auto;
  }
}
.location {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.6rem;
  height: 1.6rem;
  border-radius: 100rem;
  background-color: #a96;
}
.good_panel {
  margin: 10px 0;
  padding: 10px;
  background-color: #fff;
  .total {
    margin: 5px 0;
    text-align: right;
  }
}
.price_info {
  margin-top: 10px;
}
.secondary_info {
  padding-top: 0;
  padding-bottom: 0;
  font-size: 12px;
  color: #666;
}
.order_info {
  margin: 10px 0;
}
.info_title {
  flex: 0.5;
}
.operator {
  position: fixed;
  bottom: 0;
  width: 100%;
  padding: 20px 0;
  background-color: #fff;
  z-index: 2;
}
</style>
