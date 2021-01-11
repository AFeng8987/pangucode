<template>
  <div class="saledDetail">
    <div class="header_pannel">
      <header-bar :border="false" :transparent="true" class="header" title="售后详情" left-arrow />
      <status :value="saled.status" :reject="saled.reject_reason" type="saled" />
    </div>
    <saled-status :status="saled.status" :addr="returnedAddr" />
    <div class="btns">
      <van-button v-show="validReApply" type="danger" plain size="mini" @click="reApply">重新申请</van-button>
      <van-button v-show="validCancel" type="danger" plain size="mini" @click="cancel">取消申请</van-button>
      <van-button v-show="validLogis" type="danger" plain size="mini" @click="showLog = true">填写物流</van-button>
    </div>
    <div class="good_panel">
      <goods :data="goods" />
    </div>
    <div class="order_info">
      <van-cell-group>
        <van-cell title="订单信息" icon="info" />
        <van-cell :value="info.orderSn" title="订单编号" title-class="info_title" />
        <van-cell :value="info.payId" title="支付交易号" title-class="info_title" />
        <van-cell :value="info.addTime" title="创建时间" title-class="info_title" />
      </van-cell-group>
    </div>
    <van-popup v-model="showLog" position="bottom">
      <van-form @submit="addLogistics">
        <van-field
          :value="company"
          :rules="[{ required: true, message: '请选择物流公司' }]"
          name="company"
          label="物流公司"
          readonly
          clickable
          placeholder="点击选择"
          @click="showCompany = true" />
        <van-field
          v-model="logNo"
          :rules="[{ required: true, message: '请填写物流单号' }]"
          name="no"
          label="物流单号"
          placeholder="请输入" />
        <div style="margin: 16px;">
          <van-button round block type="info" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
    </van-popup>
    <van-popup v-model="showCompany" position="bottom">
      <van-picker
        :columns="companies"
        show-toolbar
        @confirm="choseCompany"
        @cancel="showCompany = false"
      />
    </van-popup>
  </div>
</template>

<script>
import { saledDetail, cancelSaled, updateSaled } from '@/api/order'

import Vue from 'vue'
import { Row, Col, Card, Button, Field, Form, Picker, Popup } from 'vant'
Vue.use(Button).use(Form).use(Field).use(Popup).use(Picker).use(Row).use(Col).use(Card)

import HeaderBar from '@/components/HeaderNavBar'
import Goods from '../common/goods'
import Status from '../common/status'
import SaledStatus from './status'

export default {
  components: {
    Status, Goods, HeaderBar, SaledStatus
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
      goods: [],
      showCompany: false,
      company: '',
      companies: ['顺丰快递', '圆通快递', '申通快递', '韵达快递', '百世快递', '中通快递', '中国邮政', '其他快递'],
      logNo: '',
      showLog: false,
      saled: {
        status: 0
      },
      returnedAddr: {},
      hasAddress: false,
      info: {}
    }
  },
  computed: {
    validReApply() {
      return [2, 4, 7].indexOf(this.saled.status) !== -1
    },
    validLogis() {
      return [1].indexOf(this.saled.status) !== -1 && this.hasAddress
    },
    validCancel() {
      return [1, 11, 12].indexOf(this.saled.status) !== -1
    }
  },
  watch: {},
  mounted() {
    this.load()
  },
  methods: {

    reApply() {
      this.$router.push({ name: 'saledApply', params: { oid: this.oid, gid: this.gid }})
    },
    cancel() {
      cancelSaled(this.saled.id).then(resp => { this.saled.status = 7 })
    },
    addLogistics() {
      const data = {
        id: this.saled.id,
        company: this.company,
        no: this.logNo
      }

      updateSaled(data).then(resp => {
        // console.log(resp)
        this.showLog = false
      })
    },
    choseCompany(company) {
      this.company = company
      this.showCompany = false
    },
    load() {
      saledDetail(this.gid).then(({ order, orderGoods, afterSale, returnAddress }) => {
        // console.log(order)
        this.goods = [orderGoods]
        this.hasAddress = !!afterSale.courierCompany
        Object.keys(afterSale).forEach((key) => this.$set(this.saled, key, afterSale[key]))
        Object.keys(returnAddress).forEach((key) => this.$set(this.returnedAddr, key, returnAddress[key]))
        Object.keys(order).forEach((key) => this.$set(this.info, key, order[key]))
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.header_pannel {
  background-color: #f24445;
}
.btns {
  margin: 5px 20px;
  text-align: right;
}
.good_panel {
  margin: 10px 0;
  padding: 10px;
  background-color: #fff;
}
.order_info {
  margin: 10px 0;
}
.info_title {
  flex: 0.5;
}
</style>
