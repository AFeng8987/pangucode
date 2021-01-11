<template>
  <div class="detail">
    <div class="header_pannel">
      <header-bar :border="false" :transparent="true" class="header" title="订单详情" left-arrow />
      <div class="detail_top">
        <van-row type="flex" gutter="10" class="detali_status">
          <van-col span="16">
            <h2>订单完成</h2>
            <div>订单编号：{{ orderInfo.orderSn }}</div>
            <div>下单时间：{{ orderInfo.addTime }}</div>
          </van-col>
          <van-col span="8" class="rigth">
            <img :src="finished" class="pic" >
          </van-col>
        </van-row>
      </div>
    </div>
    <van-card class="dateil_card">
      <img slot="thumb" :src="dw" class="dateil_img" >
      <div slot="title">
        <span>{{ orderInfo.consignee }}</span>
        <span style="margin-left: 20px;">{{ orderInfo.mobile }}</span>
      </div>
      <div slot="desc" class="dateil_address">
        <div class="dateil_address_name">{{ orderInfo.address }}</div>
      </div>
    </van-card>
    <div
      v-for="item in orderGoods"
      :key="item.id">
      <van-card
        :thumb="item.picUrl"
        :title="item.goodsName"
        :num="item.number"
        class="dateil_goods"
      >
        <template #tags>
          <van-tag v-for="(vv, zz) in item.specifications" :key="zz" class="dateil_goods_tag" color="#F5F5F5">{{ vv }}</van-tag>
        </template>
        <span slot="price" class="dateil_goods_price">{{ item.price | yuan }}</span>
      </van-card>
      <van-cell title="商品优惠">
        <span slot="default">{{ item.couponPrice | yuan }}</span>
      </van-cell>
    </div>
    <van-cell-group style="margin-top: 10px">
      <van-cell title="运费">
        <span slot="default">{{ orderInfo.freightPrice | yuan }}</span>
      </van-cell>
      <van-cell title="商品总优惠">
        <span slot="default">{{ orderInfo.couponPrice | yuan }}</span>
      </van-cell>
      <van-cell title="订单备注">
        <span slot="default">{{ orderInfo.message }}</span>
      </van-cell>
      <van-cell title="实付款(含运费)">
        <span slot="default" style="color:#F24546;font-size:16px">{{ orderInfo.actualPrice | yuan }}</span>
      </van-cell>
    </van-cell-group>

    <ios-danger-bottom />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import finished from '@/assets/images/order_detail_finished.png'
import dw from '@/assets/images/address_confirm.png'

import { allianceDetail } from '@/api/record'

import { Col, Row, Card, Tag, Cell, CellGroup } from 'vant'
import Vue from 'vue'

Vue.use(Col).use(Row).use(Card).use(Tag).use(Cell).use(CellGroup)

export default {
  name: 'Details',
  components: {
    HeaderBar,
    iosDangerHeader,
    iosDangerBottom
  },
  props: {
    itemId: {
      type: [String, Number],
      default: 0
    }
  },
  data() {
    return {
      finished,
      dw,
      orderInfo: {
        freightPrice: 0,
        goodsPrice: 0
      },
      orderGoods: []
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      allianceDetail(this.itemId).then(res => {
        console.log(res)
        this.orderInfo = res.orderInfo
        this.orderGoods = res.orderGoods
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.detail{
  background-color: #f7f8fa;
  .header_pannel{
    background-color: #F24446;
    .detail_top{
      .detali_status{
        align-items: center;
        padding: 20px 10px 20px 20px;
        color: #fff;
        h2 {
          margin: 10px 0;
        }
      }
      .rigth{
        text-align: center;
        .pic{
          width: 60px;
          height: 60px;
        }
      }
    }
  }
  .dateil_card{
    .van-card__thumb{
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .van-card__content{
      justify-content: center;
    }
    .dateil_img{
      width: 60px;
      height: 60px;
    }
    .dateil_address{
      margin-top: 10px;
      color: #343434;
      .dateil_address_name{
        white-space:nowrap;
        overflow:hidden;
        text-overflow:ellipsis;
      }
    }
  }
  .dateil_goods{
    .dateil_goods_tag{
      color: #9A9A9A;
      margin: 10px 0;
      margin-right: 10px;
    }
    .dateil_goods_price{
      font-size: 14px;
    }
  }
}
</style>
