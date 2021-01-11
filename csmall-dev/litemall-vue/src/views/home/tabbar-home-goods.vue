<template>
  <div class="home_goods">
    <div class="goods_title">
      <span>全部好货</span>
      <div class="goods_title_icon" />
    </div>
    <van-row class="goods_box" gutter="10">
      <van-list v-model="loading" :offset="10" :finished="finished" :immediate-check="false" direction="down" finished-text="没有更多了" @load="onLoad">
        <van-col v-for="(item, index) in goodsList" :span="12" :key="item.id + '' + index" class="goods_col">
          <div class="goods_box_goods">
            <div class="goods_box_img"><img v-lazy="item.picUrl + '?imageView2/1/w/200/h/200/q/95'" class="goods_box_left_img" @click="goDetail(item.id)"></div>
            <div class="goods_box_font">
              <div class="goods_box_name">{{ item.name }}</div>
              <div class="goods_box_num">
                <span class="goods_box_price">¥<span class="goods_box_price_font">{{ item.retailPrice }}</span></span>
                <span class="goods_box_money">¥{{ item.counterPrice }}</span>
              </div>
            </div>
          </div>
        </van-col>
      </van-list>
    </van-row>
  </div>
</template>

<script>
import { Col, Row, List } from 'vant'
import { homeGoodsPage } from '@/api/goods'

export default {
  name: 'HomeGoods',
  components: {
    [Col.name]: Col,
    [Row.name]: Row,
    [List.name]: List
  },
  props: {
    goods: {
      type: Array,
      default: () => []
    },
    page: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      goodsList: [],
      list: {
        page: 1,
        limit: 10
      },
      loading: false,
      finished: false
    }
  },
  watch: {
    goods(val) {
      this.goodsList = val
    },
    page(val) {
      if (val === true) {
        this.list.page = 1
        this.loading = false
        this.finished = false
        this.$emit('submit', false)
      }
    }
  },
  methods: {
    onLoad() {
      this.list.page = ++this.list.page
      homeGoodsPage(this.list).then(res => {
        const data = res.data.data
        const list = res.data.data.list

        if (list.length > 0) {
          this.goodsList = this.goodsList.concat(list)
        }

        this.loading = false
        if (this.list.page >= data.pages) {
          this.finished = true
        } else {
          this.list.page = data.page
        }
        this.loading = false
      }).catch(err => {
        console.log(err, 'shibai')
      })
    },
    goDetail(id) {
      this.$router.push({ path: '/items/goodsDetail/' + id })
    }
  }
}
</script>

<style lang="scss" scoped>
.home_goods{
  width: 95%;
  margin: 15px auto;
  .goods_title{
    font-weight: bold;
    color: #343434;
    font-size: 15px;
    text-align: center;
    .goods_title_icon{
      width: 15px;
      height: 2px;
      background-color: #FF4600;
      margin: auto;
      margin-top: 5px;
    }
  }
  .goods_box{
    margin-top: 15px;
    .goods_col{
      padding-bottom:10px;
      .goods_box_goods{
        background-color: #fff;
        border-radius: 10px;
        .goods_box_img {
          width: 100%;
          height: 175px;
          overflow: hidden;
          .goods_box_left_img{
            min-height: 175px;
            width: 100%;
          }
        }
        .goods_box_font{
          height: 60px;
          width: 92%;
          margin: auto;
          .goods_box_name{
            font-size: 15px;
            white-space:nowrap;
            overflow:hidden;
            text-overflow:ellipsis;
          }
          .goods_box_num{
            margin-top: 10px;
            .goods_box_price{
              color: #FF4600;
              vertical-align: middle;
              .goods_box_price_font{
                font-size: 20px;
              }
            }
            .goods_box_money{
              text-decoration:line-through;
              color: #7A7A7A;
              margin-left: 10px;
              vertical-align: middle;
            }
          }
        }
      }
    }
  }
}
</style>
