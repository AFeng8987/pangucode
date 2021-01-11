<template>
  <div class="home_header">
    <van-sticky>
      <van-row type="flex" justify="space-between" align="center" class="home_row">
        <van-col span="3">
          <van-icon :name="scan" class="home_row_icon" @click="scanClick" />
        </van-col>
        <van-col span="16">
          <input type="text" class="home_row_input" @click="$router.push({ path: '/home/search' })" >
        </van-col>
        <van-col span="5" @click.native="$router.push({path: '/user/merchants'})">
          <van-icon name="friends-o" class="home_row_jia" />
          <span class="home_row_span">加盟</span>
        </van-col>
      </van-row>
    </van-sticky>
    <van-swipe :autoplay="5000" :lazy-render="true" :stop-propagation="false" indicator-color="#FF4600">
      <van-swipe-item v-for="item in banner" :key="item.id" @click="swipeClick(item)">
        <img v-lazy="item.url" class="home_img" >
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script>
import { Col, Row, Swipe, SwipeItem, Image, Sticky } from 'vant'
import scan from '@/assets/images/scan.png'

export default {
  name: 'HomeHeader',
  components: {
    [Col.name]: Col,
    [Row.name]: Row,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [Image.name]: Image,
    [Sticky.name]: Sticky
  },
  props: {
    banner: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      scan
    }
  },
  methods: {
    swipeClick(obj) {
      if (obj.status) {
        this.$router.push({ path: '/external', query: { link: obj.link }})
      } else {
        this.$router.push({ path: '/items/goodsDetail/' + obj.link })
      }
    },
    scanClick() {
      this.$router.push('qrscanner')
    }
  }
}
</script>

<style lang="scss" scoped>
.home_header{
  .home_row{
    background-color: #FF4600;
    height: 50px;
    text-align: center;
    .home_row_icon{
      font-size: 26px;
      vertical-align: middle;
    }
    .home_row_input{
      width: 100%;
      border-radius: 20px;
      height: 30px;
      border: none;
      box-shadow: 0px 0px 0px 0px;
    }
    .home_row_jia{
      color: white;
      font-size: 26px;
      vertical-align: middle;
    }
    .home_row_span{
      vertical-align: middle;
      color: #F2D048;
      text-decoration:underline;
      font-size: 14px;
    }
  }
  .home_img{
    width: 100%;
    height: 53.333vw;
  }
}
</style>
