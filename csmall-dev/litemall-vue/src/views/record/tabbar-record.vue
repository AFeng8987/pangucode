<template>
  <div class="record">

    <van-sticky>
      <ios-danger-header class="header_top"/>
      <van-nav-bar :border="false" title="销售数据" />
    </van-sticky>

    <div class="record_font">
      <div class="record_box">
        <van-row type="flex" justify="center" align="center">
          <van-col :span="9">
            <div class="record_box_font">总销售</div>
            <div class="record_box_cc">{{ salesInformation.totalSales }}</div>
          </van-col>
          <van-col :span="9">
            <div class="record_box_font">总利润</div>
            <div class="record_box_cc">{{ salesInformation.totalProfit }}</div>
          </van-col>
          <van-col :span="6">
            <div class="record_box_time">
              <span class="record_box_time_font" @click="show = true">{{ timeType }}</span>&nbsp;<van-icon class="record_box_time_icon" name="arrow-down" />
            </div>
          </van-col>
        </van-row>
      </div>
    </div>

    <div class="record_list">
      <van-row class="record_list_row">
        <van-col span="5">商品分类</van-col>
        <van-col span="4">销售数量</van-col>
        <van-col span="5">销售金额</van-col>
        <van-col span="5">成本价格</van-col>
        <van-col span="5">订单内容</van-col>
      </van-row>

      <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
        <van-row v-for="(item, index) in list" :key="index" class="record_box_list">
          <van-col span="5">{{ item.categoryName }}</van-col>
          <van-col span="4">{{ item.goodsNumber }}</van-col>
          <van-col span="5">{{ item.goodsPrice }}</van-col>
          <van-col span="5">{{ item.plantCostPrice }}</van-col>
          <van-col span="5" class="record_box_list_view" @click="viewDetailsClick(item)">查看详情</van-col>
        </van-row>
      </van-list>
    </div>

    <van-popup v-model="show" position="top">
      <div class="record_popup" @click="handleClick(1)">近一个月</div>
      <div class="record_popup" @click="handleClick(2)">近三个月</div>
      <div class="record_popup" @click="handleClick(3)">近六个月</div>
    </van-popup>

    <ios-danger-bottom />
  </div>
</template>

<script>
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'
import { NavBar, Col, Row, Popup, Sticky, Cell, CellGroup, List } from 'vant'

import { allianceTotal, allianceList } from '@/api/record'

export default {
  name: 'Record',
  components: {
    iosDangerHeader,
    iosDangerBottom,
    [NavBar.name]: NavBar,
    [Col.name]: Col,
    [Row.name]: Row,
    [Popup.name]: Popup,
    [Sticky.name]: Sticky,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [List.name]: List
  },
  data() {
    return {
      timeType: '近一个月',
      show: false,
      type: 0,
      salesInformation: {
        totalProfit: 0,
        totalSales: 0
      },
      loading: false,
      finished: false,
      page: 0,
      limit: 10,
      list: []
    }
  },
  created() {
    this.initData()
  },
  methods: {
    handleClick(msg) {
      switch (msg) {
        case 1:
          this.timeType = '近一个月'
          this.type = 0
          break
        case 2:
          this.timeType = '近三个月'
          this.type = 1
          break
        case 3:
          this.timeType = '近六个月'
          this.type = 2
          break
      }

      this.page = 0
      this.show = false
      this.loading = false
      this.finished = false
      this.list = []
      this.initData()
    },

    initData() {
      allianceTotal({ type: this.type }).then(res => {
        this.salesInformation = res.data.data || { totalProfit: 0, totalSales: 0 }
      })
    },

    onLoad() {
      this.page++
      allianceList({ page: this.page, limit: this.limit }).then(res => {
        this.loading = false

        this.list.push(...res.list)
        this.finished = res.page >= res.pages
      })
    },

    viewDetailsClick(obj) {
      this.$router.push({ path: '/record/details/' + obj.orderGoodsId })
    }
  }
}
</script>

<style lang="scss" scoped>
.record{
  background-color: #f7f8fa;
  .header_top{
    background-color: #ffffff;
  }
  .record_font{
    width: 95%;
    margin: 10px auto;
    background-color: #ffffff;
    padding-bottom: 20px;
    border-radius: 10px;
    .record_box{
      width: 95%;
      margin: auto;
      .record_box_font{
        color: #CCCCCC;
        margin-top: 20px;
      }
      .record_box_seles{
        color: #CCCCCC;
        margin-top: 20px;
      }
      .record_box_cc{
        font-size: 14px;
      }
      .record_box_num{
        color: #EFD109;
        font-size: 16px;
      }
      .record_box_time{
        text-align: center;
        .record_box_time_font{
          vertical-align: middle;
        }
        .record_box_time_icon{
          vertical-align: middle;
        }
      }
    }
  }
  .record_list{
    width: 95%;
    margin: 5px auto;
    background-color: #ffffff;
    border-radius: 10px;
    text-align: center;
    padding-bottom: 10px;
    .record_list_row{
      padding-top: 10px;
      font-size: 13px;
      color: #343434;
    }
    .record_box_list{
      margin-top: 10px;
      .record_box_list_view{
        color: #2888E0;
      }
    }
  }
  .record_popup{
    height: 40px;
    border-bottom: 1px solid #f7f8fa;
    font-size: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
