<template>
  <div class="search">
    <!-- <ios-danger-header class="header_top"/> -->
    <van-sticky>
      <ios-danger-header class="header_top"/>
      <van-search v-model="value" class="search_box" shape="round" show-action placeholder="请输入查询内容" @search="onSearch">
        <template #left>
          <van-icon name="arrow-left" class="search_icon" @click="onCancel" />
        </template>
        <template #action>
          <van-button type="info" color="linear-gradient(to right, #ff6034, #ee0a24)" size="small" class="search_button" @click="searchClick">搜索</van-button>
        </template>
      </van-search>
      <van-dropdown-menu v-if="list.length" class="search_dropdown">
        <van-dropdown-item v-model="value1" :options="option1" @change="onDropdown" />
        <van-dropdown-item v-model="value2" :options="option2" @change="onDropdown" />
      </van-dropdown-menu>
    </van-sticky>
    <div v-if="list.length" class="search_goods">
      <van-list v-model="loading" :immediate-check="false" :finished="finished" finished-text="没有更多了" @load="onLoad">
        <van-card
          v-for="item in list"
          :key="item.id"
          :thumb="item.picUrl"
          :title="item.name"
          class="search_goods_card"
          @click="onGoodsDetails(item.id)"
        >
          <template #price>
            <span class="search_goods_price">{{ item.retailPrice | yuan }}</span>
          </template>
          <template #origin-price>
            <span class="search_goods_origin">{{ item.counterPrice | yuan }}</span>
          </template>
          <template #num>
            <span>销量: {{ item.sales }}</span>
          </template>
        </van-card>
      </van-list>
    </div>
  </div>
</template>

<script>
import { Search, DropdownItem, DropdownMenu, Card, Sticky, List } from 'vant'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'

import { goodsList } from '@/api/goods'

export default {
  name: 'HomeSearch',
  components: {
    [Search.name]: Search,
    iosDangerHeader,
    [DropdownItem.name]: DropdownItem,
    [DropdownMenu.name]: DropdownMenu,
    [Card.name]: Card,
    [Sticky.name]: Sticky,
    [List.name]: List
  },
  data() {
    return {
      value: '',
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
      list: [],
      listQuery: {
        sort: 'synthesize',
        order: undefined,
        keyword: '',
        page: 1,
        limit: 10
      },
      loading: false,
      finished: false
    }
  },
  beforeRouteLeave(to, from, next) {
    // console.log(to)
    // console.log(from)
    if (to.name === 'home' || to.name === 'class') {
      this.changeKeepAlive(from.name, false)
    }
    if (to.name === 'goodsDetail') {
      this.changeKeepAlive(from.name, true)
    }
    next()
  },
  methods: {
    changeKeepAlive(parentName, keepAlive) {
      this.$router.options.routes.map((item) => {
        if (item.name === parentName) {
          item.meta.keepAlive = keepAlive
        }
      })
    },
    // 搜索
    onSearch(val) {
      this.listQuery.keyword = val
      this.getSearchGoodsList()
    },
    searchClick() {
      this.listQuery.keyword = this.value
      this.getSearchGoodsList()
    },
    onDropdown(val) {
      switch (val) {
        case 0:
          this.listQuery.sort = 'synthesize'
          this.listQuery.order = undefined
          this.value2 = 3
          break
        case 1:
          this.listQuery.sort = 'retail_price'
          this.listQuery.order = 'asc'
          this.value2 = 3
          break
        case 2:
          this.listQuery.sort = 'retail_price'
          this.listQuery.order = 'desc'
          this.value2 = 3
          break
        case 3:
          this.listQuery.sort = 'synthesize'
          this.listQuery.order = undefined
          this.value1 = 0
          break
        case 4:
          this.listQuery.sort = 'sales'
          this.listQuery.order = 'desc'
          this.value1 = 0
          break
      }
      this.listQuery.page = 1
      this.loading = false
      this.finished = false
      this.getSearchGoodsList()
    },
    // 调用搜索接口
    getSearchGoodsList() {
      goodsList(this.listQuery).then(res => {
        this.list = res.list
      })
    },
    onLoad() {
      this.listQuery.page++
      goodsList(this.listQuery).then(res => {
        this.loading = false

        this.list.push(...res.list)
        this.finished = res.page >= res.pages
      })
    },
    // 进入商品详情
    onGoodsDetails(id) {
      this.$router.push({ path: '/items/goodsDetail/' + id })
    },
    // 取消
    onCancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="scss" scoped>
.search{
  .header_top{
    background-color: #ffffff;
  }
  .search_box{
    .search_icon{
      font-size: 24px;
      width: 30px;
    }
    .search_button{
      line-height: 0.75rem;
      height: 0.75rem;
      border-radius: 15px;
      font-size: .36rem;
    }
    .van-search__action{
      line-height: inherit;
      &:active {
        background: none;
      }
    }
  }
  // .search_dropdown{
  //   /deep/ .van-dropdown-menu__bar{
  //     box-shadow: none;
  //     .van-dropdown-menu__item{
  //       flex: none;
  //       margin-left: 10px;
  //     }
  //   }
  // }
  .search_goods{
    margin-top: 5px;
    .search_goods_card{
      &:not(:first-child) {
        margin-top: 5px;
      }
    }
    .search_goods_price{
      font-size: 16px;
      color: #F34444;
      vertical-align: middle;
    }
    .search_goods_origin{
      font-size: 12px;
      color: #BBBBBB;
      margin-left: 10px;
      vertical-align: middle;
    }
  }
}
</style>
