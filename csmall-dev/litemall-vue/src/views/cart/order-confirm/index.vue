<template>
  <div class="order">
    <header-bar :fixed="true" title="确认订单" left-arrow />
    <ios-danger-header class="header_top"/>
    <div v-if="!checkedAddress" class="order_empty">
      <van-icon class="order_empty_icon" name="add-o" color="#FEBF00" @click="$router.push({ path: '/user/address', query: { type: 'add' }})" />
      <p class="order_empty_font">添加您的第一个地址</p>
    </div>
    <div v-else class="order_yes" @click="$router.push({path: '/user/addressList', query: { type: 'add' }})">
      <van-row type="flex" justify="center" align="center">
        <van-col :span="4">
          <div style="text-align: center;"><img :src="addressConfirm" class="order_yes_img" ></div>
        </van-col>
        <van-col :span="17">
          <div class="order_yes_address">
            <div class="order_address_information">
              <span class="order_address_name">{{ checkedAddress.name }}</span>&nbsp;&nbsp;
              <span class="order_address_tel">{{ checkedAddress.tel }}</span>
            </div>
            <div class="order_address_font">{{ checkedAddress.province + checkedAddress.city + checkedAddress.county + checkedAddress.street + checkedAddress.addressDetail }}</div>
          </div>
        </van-col>
        <van-col :span="3">
          <div style="text-align: center;"><van-icon name="arrow" class="order_yes_icon" /></div>
        </van-col>
      </van-row>
    </div>

    <order-coupon @submit="getCoupon" />

    <div class="order_goods">
      <div v-for="item in checkedGoodsList" :key="item.id">
        <van-row type="flex" justify="center" align="center">
          <van-col :span="8">
            <div style="text-align: center;"><img :src="item.picUrl" class="order_goods_pic"></div>
          </van-col>
          <van-col :span="16">
            <div class="order_goods_name">{{ item.goodsName }}</div>
            <van-tag v-for="(vv, index) in item.specifications" :key="index" class="order_goods_tag" color="#EFEFEF">{{ vv }}</van-tag>
            <div class="order_goods_price">{{ item.price | yuan }}</div>
          </van-col>
        </van-row>
        <van-cell :value="item.number" title="购买数量" />
      </div>
      <van-cell-group>
        <van-cell title="发货方式" value="普通快递" />
        <van-field v-model="value" label="订单备注" placeholder="选填" />
      </van-cell-group>
    </div>

    <div class="order_goods">
      <van-cell-group>
        <van-cell title="商品金额">
          <template #default>
            <span class="order_amount">{{ order.goodsTotalPrice | yuan }}</span>
          </template>
        </van-cell>
        <van-cell title="运费">
          <template #default>
            <span class="order_amount">{{ order.freightPrice | yuan }}</span>
          </template>
        </van-cell>
        <van-cell title="总计" size="large">
          <template #default>
            <span class="order_total">{{ orderTotalPrice | yuan }}</span>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <van-submit-bar :price="orderTotalPrice*100" :loading="isSubmit" text-align="left" button-text="提交订单" @submit="onSubmit" />
    <ios-danger-bottom />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import orderCoupon from './coupon'

import addressConfirm from '@/assets/images/address_confirm.png'

import { cartCheckout, orderSubmit } from '@/api/cart'

import { Row, Col, Tag, Cell, CellGroup, Field, SubmitBar } from 'vant'
import Vue from 'vue'

Vue.use(Row).use(Col).use(Tag).use(Cell).use(CellGroup).use(Field).use(SubmitBar)

export default {
  name: 'OrderConfirm',
  components: {
    HeaderBar,
    iosDangerBottom,
    orderCoupon,
    iosDangerHeader
  },
  data() {
    return {
      addressConfirm,
      value: '',
      checkedAddress: {},
      checkedGoodsList: [],
      order: {
        addressId: 0,
        goodsTotalPrice: 0,
        freightPrice: 0
      },
      orderTotalPrice: 0,
      isSubmit: false,
      userCouponId: undefined,
      cartIds: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      const data = {
        goodsId: this.$route.query.goodsId ? this.$route.query.goodsId : null,
        number: this.$route.query.number ? this.$route.query.number : null,
        specCodeId: this.$route.query.specCodeId ? this.$route.query.specCodeId : null,
        cartIds: this.$route.query.cartIds ? JSON.parse(this.$route.query.cartIds) : null
      }
      cartCheckout(data).then(res => {
        this.cartIds = []
        this.cartIds = res.data.data.cartIds
        this.checkedGoodsList = res.data.data.orderGoods
        this.order = res.data.data
        this.orderTotalPrice = res.data.data.orderTotalPrice
        this.judge(res.data.data.checkedAddress)
      })
    },
    judge(row) {
      if (this.$store.getters.address.id) {
        this.checkedAddress = this.$store.getters.address
        this.$store.dispatch('DelAddress')
      } else {
        this.checkedAddress = row
      }
    },
    onSubmit() {
      this.isSubmit = true
      if (!this.checkedAddress) {
        this.$toast.fail('请设置收货地址')
        return
      }

      let buyOrderGoods = null
      if (this.$route.query.goodsId && this.$route.query.number && this.$route.query.specCodeId) {
        buyOrderGoods = {
          goodsId: this.$route.query.goodsId,
          number: this.$route.query.number,
          specCodeId: this.$route.query.specCodeId
        }
      }

      const data = {
        addressId: Number(this.order.checkedAddress.id),
        message: this.value,
        userCouponId: (this.userCouponId ? this.userCouponId : undefined),
        cartIds: this.$route.query.cartIds ? JSON.parse(this.$route.query.cartIds) : null,
        buyOrderGoodsVo: buyOrderGoods === null ? null : buyOrderGoods
      }

      orderSubmit(data).then(res => {
        const money = this.orderTotalPrice
        const id = res.data.data
        this.$store.dispatch('payment/show', {
          id, money
        })
      }).catch(err => {
        this.isSubmit = false
        this.$toast(err.data.errmsg)
      })
    },
    getCoupon(msg) {
      if (JSON.stringify(msg) === '{}') {
        this.orderTotalPrice = this.order.goodsTotalPrice + this.order.freightPrice
        this.userCouponId = null
        return
      }
      const price = this.order.orderTotalPrice - msg.valueDesc
      if (price >= 0) {
        this.orderTotalPrice = price
        this.userCouponId = msg.id
        return
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.order{
  background-color: #f7f8fa;
  .header_top{
    background-color: #ffffff;
  }
  .order_empty{
    width: 95%;
    margin: auto;
    margin-top: 1.5rem;
    border-radius: 10px;
    background-color: #ffffff;
    text-align: center;
    .order_empty_icon{
      font-size: 25px;
      padding-top: 20px;
    }
    .order_empty_font{
      color: #BCBCBC;
      font-size: 14px;
      padding-bottom: 20px;
    }
  }
  .order_yes{
    width: 95%;
    margin: auto;
    margin-top: 1.5rem;
    border-radius: 10px;
    background-color: #ffffff;
    .order_yes_img{
      width: 35px;
      height: 35px;
    }
    .order_yes_icon{
      font-size: 20px;
    }
    .order_yes_address{
      height: 100px;
      padding-top: 20px;
      .order_address_information{
        font-size: 14px;
        .order_address_name{
          font-weight: bold;
          color: #000000;
        }
        .order_address_tel{
          font-weight: bold;
          color: #BCBCBC;
        }
      }
      .order_address_font{
        font-weight: 400;
        color: #343434;
        font-size: 14px;
        padding-top: 5px;
      }
    }
  }
  .order_goods{
    width: 95%;
    margin: auto;
    background-color: #ffffff;
    margin-top: 10px;
    border-radius: 10px;
    margin-bottom: 20px;
    .order_goods_pic{
      width: 100px;
      height: 100px;
    }
    .order_goods_name{
      height: 0.85333;
      text-overflow: -o-ellipsis-lastline;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      margin-bottom: 10px;
    }
    .order_goods_tag{
      color: #BCBCBC;
      margin-bottom: 10px;
      margin-right: 5px;
    }
    .order_goods_price{
      color: #F34444;
      font-size: 14px;
    }
    .order_amount{
      color: #000000;
    }
    .order_total{
      color: #F24546;
    }
  }
}
</style>
