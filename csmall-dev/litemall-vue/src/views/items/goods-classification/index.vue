<template>
  <div class="class">
    <header-bar :fixed="true" :title="textMsg" left-arrow />
    <ios-danger-header/>
    <div class="class_keyword">
      <van-dropdown-menu>
        <van-dropdown-item v-model="value1" :options="option1" @change="onDropdown" />
        <van-dropdown-item v-model="value2" :options="option2" @change="onDropdown" />
      </van-dropdown-menu>
      <div v-for="(item, index) in getKeyword()" :key="index" class="class_tag"><div :class="{ hide:sh1(index), show:sh2(index) }" class="class_name" @click="selectClick(item, index)">{{ item }}</div></div>
    </div>
    <van-pull-refresh v-model="isLoading" success-text="刷新成功" @refresh="onRefresh">
      <div class="class_boxs">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <div v-for="item in goodsList" :key="item.id" class="class_goods">
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
                <div class="class_goods_sales">
                  <span>销量: {{ item.sales }}</span>
                </div>
              </van-col>
            </van-row>
          </div>
        </van-list>
      </div>
    </van-pull-refresh>

    <ios-danger-bottom />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import { goodsList } from '@/api/goods'

import Vue from 'vue'
import { Col, Row, Empty, PullRefresh, Lazyload, List, Tag, DropdownMenu, DropdownItem } from 'vant'

Vue.use(Col).use(Row).use(Empty).use(PullRefresh).use(Lazyload).use(List).use(Tag).use(DropdownMenu).use(DropdownItem)

export default {
  name: 'Classification',
  components: {
    HeaderBar,
    iosDangerBottom,
    iosDangerHeader
  },
  data() {
    return {
      textMsg: '',
      goodsList: [],
      isLoading: false,
      loading: false,
      finished: false,
      page: 0,
      limit: 10,
      keywordData: '',
      activeIndex: -1,
      keyword: undefined,
      value1: 0,
      value2: 3,
      option1: [
        { text: '综合排序', value: 0 },
        { text: '价格低到高', value: 1 },
        { text: '价格高到低', value: 2 }
      ],
      option2: [
        { text: '默认', value: 3 },
        { text: '销量降序', value: 4 }
      ],
      sort: 'synthesize',
      order: undefined
    }
  },
  created() {
    this.textMsg = this.$route.query.name
  },
  methods: {
    getKeyword() {
      return this.keywordData.split(';')
    },
    sh1(index) {
      return index !== this.activeIndex
    },
    sh2(index) {
      return index === this.activeIndex
    },
    selectClick(item, index) {
      if (this.activeIndex === index) {
        this.activeIndex = -1
        this.keyword = undefined
      } else {
        this.activeIndex = index
        this.keyword = item
      }
      this.isLoading = true
      this.onRefresh()
    },
    onDropdown(val) {
      switch (val) {
        case 0:
          this.sort = 'synthesize'
          this.order = undefined
          this.value2 = 3
          break
        case 1:
          this.sort = 'retail_price'
          this.order = 'asc'
          this.value2 = 3
          break
        case 2:
          this.sort = 'retail_price'
          this.order = 'desc'
          this.value2 = 3
          break
        case 3:
          this.sort = 'synthesize'
          this.order = undefined
          this.value1 = 0
          break
        case 4:
          this.sort = 'sales'
          this.order = 'desc'
          this.value1 = 0
          break
      }

      this.page = 0
      this.loading = false
      this.finished = false
      this.goodsList = []
      this.onLoad()
    },
    onLoad() {
      this.page++
      goodsList({ categoryId: this.$route.query.id, page: this.page, limit: this.limit, keyword: this.keyword, sort: this.sort, order: this.order }).then(res => {
        this.keywordData = res.keyword
        this.loading = false

        if (this.isLoading) {
          this.goodsList = []
          this.isLoading = false
        }

        this.goodsList.push(...res.list)
        this.finished = res.page >= res.pages
      })
    },

    // 下拉刷新
    onRefresh() {
      this.loading = true
      this.page = 0
      this.onLoad()
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
  .class_keyword{
    margin-top: 1.2rem;
    width: 100%;
    background-color: #ffffff;
    // padding: 10px 0;
    .class_tag{
      display: inline-block;
      width: 25%;
      padding: 8px 0;
      text-align: center;
      .class_name{
        width: 88%;
        height: 24px;
        line-height: 24px;
        margin: auto;
        border-radius: 10px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .hide{
        background-color: #e4e4e4;
        color: #7b7b7b;
      }
      .show{
        background-color: #FF4600;
        color: #ffffff;
      }
    }
  }
  .class_boxs{
    margin-top: .3rem;
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
        display: inline-block;
        width: 60%;
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
      .class_goods_sales{
        display: inline-block;
        width: 40%;
        text-align: center;
        color: #999;
      }
    }
  }
}
</style>
