<template>
  <div class="saled-refund">
    <header-bar :title="title" left-arrow/>
    <div class="goods"><goods :data="order.goods" :gid="gid" /> </div>
    <van-cell-group>
      <van-cell v-if="!returned" :title="`货物状态：${recievedTxt}`" center is-link>
        <template #right-icon>
          <van-switch v-model="recieved" size="24" />
        </template>
      </van-cell>
      <van-cell :value="reason" center title="退款原因" is-link @click="showReasonPop" />
    </van-cell-group>
    <van-cell-group>
      <van-field v-model.number="price" :disabled="!recieved" label="退款金额" @blur="validatePrice" />
      <van-notice-bar :text="noticeTxt" left-icon="info-o" color="#1989fa" background="#ecf9ff" />
      <van-field v-model="desc" placeholder="选填" label="退款说明" />
    </van-cell-group>
    <van-cell-group>
      <van-cell title="上传凭证">
        <template #label>
          <van-uploader v-model="files" :max-count="3" :after-read="afterRead" multiple />
        </template>
      </van-cell>
    </van-cell-group>

    <van-popup v-model="reasonPop" position="bottom" style="{height: 60%}">
      <van-cell-group v-model="reasonChecked">
        <van-cell v-for="(son, index) in getReasons()" :key="index" :title="son.label" clickable @click="setReason(son, index)">
          <template #right-icon>
            <van-radio :name="index" />
          </template>
        </van-cell>
      </van-cell-group>
    </van-popup>
    <van-button :disabled="invalid" type="warning" size="large" class="apply_btn" @click="apply">提交申请</van-button>

    <ios-danger-bottom />
  </div>
</template>

<script>
import Vue from 'vue'
import { CellGroup, Cell, Field, NoticeBar, Switch, Popup, Radio, Uploader } from 'vant'

import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import HeaderBar from '@/components/HeaderNavBar'
import goods from '../common/goods'

import { detail, saledApply } from '@/api/order'
import { imgUpLoad } from '@/api/upload'

Vue.use(CellGroup).use(Cell).use(Field).use(NoticeBar).use(Switch).use(Popup).use(Radio).use(Uploader)

export default {
  components: {
    goods, HeaderBar, iosDangerBottom
  },
  props: {
    oid: {
      type: String,
      default: ''
    },
    gid: {
      type: [Number, String],
      default: 0
    },
    returned: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      order: {},
      price: 0,
      maxPrice: 0,
      recieved: false,
      reasonPop: false,
      reason: '',
      reasons: [
        { label: '不喜欢/不想要' },
        { label: '空包裹/少货' },
        { label: '快递一直未送达' },
        { label: '快递无跟踪记录' },
        { label: '货物破损已拒签' },
        { label: '其他' },
        { label: '退运费', recieved: true },
        { label: '实物与商品描述不符', recieved: true },
        { label: '少件/漏发', recieved: true },
        { label: '发错货', recieved: true },
        { label: '收到商品时有破损/变形', recieved: true },
        { label: '使用后造成人身伤害', recieved: true }
      ],
      reasonChecked: null,
      files: [],
      imgs: [],
      desc: ''
    }
  },
  computed: {
    noticeTxt() {
      return (!this.recieved ? '不' : '') + `可修改，最多可退￥${this.maxPrice}`
    },
    recievedTxt() {
      return this.recieved ? '已收到货' : '未收到货'
    },
    title() {
      return '申请售后-' + (this.returned ? '退货退款' : '仅退款')
    },
    invalid() {
      let result = true

      if (this.reason && this.price <= this.maxPrice) {
        result = false
      }
      return result
    }
  },
  watch: {},
  created() {},
  mounted() {
    this.getGood(this.oid, this.gid)
  },
  methods: {
    getGood(oid, gid) {
      detail(oid).then(order => {
        const good = order.goods.find(good => good.id === Number(this.gid))
        this.order = order
        this.price = good.price * good.number - good.couponPrice
        this.maxPrice = this.price
        // if (order.goods.length === 1) {
        //   this.price = good.price + good.freightPrice
        // }
      })
    },
    showReasonPop() {
      this.reasonPop = true
      this.reason = ''
      this.reasonChecked = null
    },
    getReasons() {
      if (this.returned) {
        this.recieved = true
      }
      return this.reasons.filter(reason => Boolean(reason.recieved) === this.recieved)
    },
    setReason(reason, index) {
      this.reason = reason.label
      this.reasonChecked = index
      this.reasonPop = false
    },
    afterRead({ file }) {
      const fd = new FormData()
      fd.append('file', file)
      imgUpLoad(fd)
        .then(res => {
          this.imgs.push(res.data.data.url)
          console.log(this.imgs)
        })
        .catch(error => {
          console.log('上传失败' + error)
        })
    },
    validatePrice() {
      if (this.price > this.maxPrice) {
        console.log(this)
        this.$dialog.alert({ message: '退款金额不能大于商品价格' })
      }
    },
    apply() {
      const data = {
        reason: this.reason,
        recieved: this.recieved,
        ogid: this.gid,
        comment: this.desc,
        amount: this.price,
        returned: this.returned,
        refundAmount: this.price
      }

      saledApply(data).then(resp => {
        console.log(resp)
        this.$toast('申请成功')
        this.$router.replace({ name: 'saledDetail', params: { oid: this.oid, gid: this.gid }})
      }).catch(error => this.$toast(error.data.errmsg))
    }
  }
}
</script>
<style lang="scss" scoped>
.apply_btn {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 40px;
}
</style>
