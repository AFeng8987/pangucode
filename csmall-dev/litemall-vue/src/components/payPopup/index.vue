<template>
  <van-popup v-model="paying" :close-on-click-overlay="false" :safe-area-inset-bottom="true" position="bottom" round>
    <div class="pay_box">
      <div class="pay_titel">
        <van-row type="flex" justify="center" align="center">
          <van-col :span="8" />
          <van-col :span="8">
            <div class="pay_titel_font"><span>支付订单</span></div>
          </van-col>
          <van-col :span="8">
            <div class="pat_titel_icon"><van-icon name="cross" @click="cloesPopup" /></div>
          </van-col>
        </van-row>
      </div>
      <div class="pay_price">
        <span>{{ money | yuan }}</span>
      </div>
      <div class="pay_way">
        <span class="pay_way_font">支付方式</span>
        <van-radio-group v-model="radio" class="pay_way_select">
          <van-cell-group>
            <van-cell :center="true" @click="radio = 'alipay'">
              <template #title>
                <img :src="aliPic" class="pay_img">
              </template>
              <template #right-icon>
                <van-radio name="alipay" />
              </template>
            </van-cell>
            <van-cell :center="true" @click="radio = 'wx'">
              <template #title>
                <img :src="wxPic" class="pay_img">
              </template>
              <template #right-icon>
                <van-radio name="wx" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>
      </div>

      <van-button color="#FEBF00" class="pay_button" type="primary" block @click="handlePay">立即付款</van-button>
    </div>
  </van-popup>
</template>

<script>
import aliPic from '@/assets/images/ali_pay.png'
import wxPic from '@/assets/images/wx_pay.png'

import { alipayPay, wxPay } from '@/api/pay'

import Vue from 'vue'
import { Popup, Col, Row, Cell, CellGroup, RadioGroup, Radio, Dialog } from 'vant'
import { mapState } from 'vuex'

Vue.use(Popup).use(Col).use(Row).use(Cell).use(CellGroup).use(RadioGroup).use(Radio).use(Dialog)

export default {
  name: 'Pay',
  data() {
    return {
      show: false,
      radio: 'alipay',
      aliPic,
      wxPic
    }
  },
  computed: {
    ...mapState('payment', {
      id: state => state.id,
      money: state => state.money
    }),
    paying: {
      get() {
        return this.$store.state.payment.paying
      },
      set(paying) {
        if (!paying) {
          this.$store.dispatch('payment/close', this)
        }
      }
    }
  },
  watch: {
    money(val) {
      this.show = !!val
    }
  },
  created() {},
  methods: {
    handlePay() {
      const _this = this
      if (this.radio) {
        if (this.radio === 'alipay') {
          alipayPay({ payOrderSn: this.id }).then(res => {
            console.log(res)

            cordova.plugins.alipay.payment(res.data, function success(e) {
              console.log(e, 'chenggong')
              _this.$store.dispatch('payment/closeNow')
              _this.$router.replace({ path: '/user/success' })
            }, function error(w) {
              console.log(w, 'shibai')
            })
          })
        } else {
          wxPay({ payOrderSn: this.id }).then(res => {
            const payWechat = {}

            payWechat.appid = res.data.payParams.appId
            payWechat.partnerid = res.data.payParams.partnerId
            payWechat.prepayid = res.data.payParams.prepayId
            payWechat.package = res.data.payParams.packageValue
            payWechat.noncestr = res.data.payParams.nonceStr
            payWechat.timestamp = res.data.payParams.timeStamp
            payWechat.sign = res.data.payParams.sign
            window.Wechat.sendPaymentRequest(payWechat, function() {
              _this.$store.dispatch('payment/closeNow')
              _this.$router.replace({ path: '/user/success' })
            }, function(reason) {
              console.log(reason)
            })
          })
        }
      } else {
        _this.$toast.fail('请选择支付方式')
      }
    },
    cloesPopup() {
      this.paying = false
    }
  }
}
</script>

<style lang="scss" scoped>
.pay_box{
  width: 95%;
  margin: auto;
  margin-top: 15px;
  .pay_titel{
    .pay_titel_font{
      font-size: 16px;
      text-align: center;
      font-weight: 400;
      color: #343434;
    }
    .pat_titel_icon{
      text-align: right;
      i {
        font-size: 18px;
        padding-top: 3px;
        color: #BCBCBC;
      }
    }
  }
  .pay_price{
    text-align: center;
    font-size: 25px;
    margin-top: 30px;
  }
  .pay_way{
    margin-top: 10px;
    .pay_way_font{
      font-size: 14px;
    }
    .pay_way_select{
      margin-top: 10px;
      .pay_img{
        height: 35px;
      }
    }
  }
  .pay_button{
    margin-top: 30px;
    border-radius: 10px;
    margin-bottom: 20px;
  }
}
</style>
