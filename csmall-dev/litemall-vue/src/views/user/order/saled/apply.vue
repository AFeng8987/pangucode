<template>
  <div class="saled-apply">
    <header-bar title="申请售后" left-arrow/>
    <div class="goods"><goods :data="order.goods" :gid="gid" /> </div>
    <van-cell-group>
      <van-cell
        v-for="item in refundItems"
        :key="item.name"
        :title="item.title"
        :label="item.label"
        center
        is-link
        @click="goRefund(item)" />
    </van-cell-group>
  </div>
</template>

<script>
import Vue from 'vue'
import { CellGroup, Cell } from 'vant'

import HeaderBar from '@/components/HeaderNavBar'
import goods from '../common/goods'

import { detail } from '@/api/order'

Vue.use(CellGroup).use(Cell)

export default {
  components: {
    goods, HeaderBar
  },
  props: {
    oid: {
      type: String,
      default: ''
    },
    gid: {
      type: [Number, String],
      default: 0
    }
  },
  data() {
    return {
      refundItems: [
        { name: 'refund', title: '仅退款', label: '未收到（包括未签收），或者与卖家协商同意' },
        { name: 'return', title: '退款退货', label: '已经到货，需要退换已经收到的货物' }
      ],
      order: {}
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {
    this.getGood(this.oid, this.gid)
  },
  methods: {
    getGood(oid, gid) {
      detail(oid).then(order => {
        this.order = order
      })
    },
    goRefund(item) {
      const returned = item.name === 'return'
      this.$router.replace({ name: 'saledRefund', params: { oid: this.oid, gid: this.gid, returned }})
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
