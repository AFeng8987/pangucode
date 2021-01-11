<template>
  <div class="catalog">
    <ios-danger-header class="header_top"/>
    <van-sticky>
      <van-row type="flex" justify="space-between" align="center" class="catalog_row">
        <van-col span="3">
          <van-icon :name="scan" class="catalog_row_icon" @click="scanClick" />
        </van-col>
        <van-col span="16">
          <input type="text" class="catalog_row_input" @click="$router.push({ path: '/home/search' })" >
        </van-col>
        <van-col span="5" @click.native="$router.push({path: '/user/merchants'})">
          <van-icon name="friends-o" class="catalog_row_jia" />
          <span class="catalog_row_span">加盟</span>
        </van-col>
      </van-row>
    </van-sticky>

    <div class="class_tree">
      <ul class="class_tree_nav">
        <li
          v-for="(item, index) in categoryList"
          :key="index"
          :class="{active_nav: currentCategory.id == item.id}"
          @click="onClick(item.id)"
        >{{ item.name }}</li>
      </ul>
      <div class="class_grid">
        <van-row>
          <van-col v-for="item in currentSubCategory" :key="item.id" span="8" @click.native="onGoodsList(item)">
            <div class="class_box">
              <img v-lazy="item.iconUrl" class="catalog_image" >
              <div>{{ item.name }}</div>
            </div>
          </van-col>
        </van-row>
      </div>
    </div>
    <ios-danger-bottom />
  </div>
</template>

<script>
import { Sidebar, SidebarItem, Col, Row, Grid, GridItem, Image, Lazyload, Sticky } from 'vant'
import scan from '@/assets/images/scan_black.png'

import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import { catalogList, catalogCurrent } from '@/api/goods'

export default {
  name: 'Catalog',
  components: {
    iosDangerHeader,
    iosDangerBottom,
    [Sidebar.name]: Sidebar,
    [SidebarItem.name]: SidebarItem,
    [Col.name]: Col,
    [Row.name]: Row,
    [Grid.name]: Grid,
    [GridItem.name]: GridItem,
    [Image.name]: Image,
    [Lazyload.name]: Lazyload,
    [Sticky.name]: Sticky
  },
  data() {
    return {
      activeKey: 0,
      scan,
      categoryList: [],
      currentCategory: {},
      currentSubCategory: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      catalogList().then(res => {
        this.categoryList = res.data.data.categoryList
        this.currentCategory = res.data.data.currentCategory
        this.currentSubCategory = res.data.data.currentSubCategory
      })
    },
    onChage(index) {
      this.activeKey = index
    },
    onClick(id) {
      catalogCurrent({ id: id }).then(res => {
        this.currentSubCategory = res.data.data.currentSubCategory
        this.currentCategory = res.data.data.currentCategory
      })
    },
    onGoodsList(row) {
      this.$router.push({ path: '/items/goodsClass', query: { id: row.id, name: row.name }})
    },
    scanClick() {
      this.$router.push('qrscanner')
    }
  }
}
</script>

<style lang="scss" scoped>
.catalog{
  background-color: #f7f8fa;
  .header_top{
    background-color: #ffffff;
  }
  .catalog_row{
    background-color: #ffffff;
    height: 50px;
    text-align: center;
    .catalog_row_icon{
      font-size: 26px;
      vertical-align: middle;
    }
    .catalog_row_input{
      width: 100%;
      border-radius: 20px;
      height: 30px;
      border: none;
      box-shadow: 0px 0px 0px 0px;
      background-color: #EFEFEF;
    }
    .catalog_row_jia{
      font-size: 26px;
      vertical-align: middle;
    }
    .catalog_row_span{
      vertical-align: middle;
      color: #F34444;
      text-decoration:underline;
      font-size: 14px;
    }
  }
  .class_tree {
    position: relative;
    background-color: #fff;
    overflow-x: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    height: calc(100% - 60px);
    box-sizing: border-box;
    margin-top: 10px;
    .class_tree_nav {
      float: left;
      width: 100px;
      height: 100%;
      background-color: #fff;
      overflow: scroll;
      > li {
        @include one-border;
        height: 40px;
        line-height: 40px;
        text-align: center;
        border-left: 2px solid $bg-color;
      }
      > li.active_nav {
        background-color: #fff;
        border-left: 2px solid $red;
        color: $red;
      }
    }
    .class_grid{
      margin-left: 10px;
      height: 100%;
      overflow: scroll;
      width: calc(100% - 100px);
      .class_box{
        text-align: center;
        padding-bottom: 20px;
        margin-top: 5px;
        .catalog_image{
          width: 50px;
          height: 50px;
        }
      }
    }
  }
}
</style>
