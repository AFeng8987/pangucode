<template>
  <div class="class">
    <header-bar :fixed="true" :title="textMsg" left-arrow />
    <ios-danger-header/>
    <van-pull-refresh v-model="isLoading" success-text="刷新成功" @refresh="onRefresh">
      <div class="class_boxs">
        <van-empty
          v-if="!goodsList.length"
          class="custom-image"
          image="https://img.yzcdn.cn/vant/custom-empty-image.png"
          description="暂无商品"
        />

        <div v-for="item in goodsList" v-else :key="item.id" class="class_goods">
          <van-row gutter="8" type="flex" justify="center" align="center" @click.native="handleGoods(item.id)">
            <van-col :span="8">
              <div class="class_goods_div"><img v-lazy="item.picUrl" class="class_goods_img"></div>
            </van-col>
            <van-col :span="16">
              <div class="class_goods_name">{{ item.name }}</div>
              <div class="class_goods_num">
                <span class="class_goods_price">{{ item.retailPrice | yuan }}</span>
                <span class="class_goods_money">{{ item.counterPrice | yuan }}</span>
              </div>
            </van-col>
          </van-row>
        </div>
      </div>
    </van-pull-refresh>

    <ios-danger-bottom />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import { activityGoods } from '@/api/goods'

import Vue from 'vue'
import { Col, Row, Empty, PullRefresh, Lazyload } from 'vant'

Vue.use(Col).use(Row).use(Empty).use(PullRefresh).use(Lazyload)

export default {
  name: 'Activity',
  components: {
    HeaderBar,
    iosDangerBottom,
    iosDangerHeader
  },
  data() {
    return {
      textMsg: '',
      goodsList: [],
      isLoading: false
    }
  },
  created() {
    this.textMsg = this.$route.query.name
    this.getActivityGoods()
  },
  methods: {
    // 获取当前活动下的商品列表
    getActivityGoods() {
      activityGoods({ activityId: this.$route.query.id }).then(res => {
        this.goodsList = res
      })
    },

    // 下拉刷新
    onRefresh() {
      setTimeout(() => {
        this.isLoading = false
        this.getActivityGoods()
      }, 1000)
    },

    // 商品详情
    handleGoods(id) {
      this.$router.push({ path: '/items/goodsDetail/' + id })
    }
  }
}
</script>

<style lang="scss" scoped>
.class{
  background-color: #f7f8fa;
  .class_boxs{
    margin-top: 1.5rem;
    background-color: #ffffff;
    border-radius: 10px;
    .custom-image .van-empty__image {
      width: 90px;
      height: 90px;
    }
    .class_goods{
      border-bottom: 1px solid #F5F5F5;
      width: 94%;
      margin: auto;
      .class_goods_div{
        width: 100%;
        height: 120px;
        overflow: hidden;
      }
      .class_goods_img{
        width: 100%;
      }
      .class_goods_name{
        font-size: 14px;
        height: 1rem;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        margin-bottom: .7rem;
      }
      .class_goods_num{
        .class_goods_price{
          font-size: 16px;
          color: #F34444;
        }
        .class_goods_money{
          color: $font-color-gray;
          text-decoration: line-through;
          font-size: $font-size-small;
          margin-left: 15px;
        }
      }
    }
  }
}
</style>
