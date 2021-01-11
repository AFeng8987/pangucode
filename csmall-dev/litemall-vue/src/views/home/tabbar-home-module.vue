<template>
  <div class="module">
    <van-grid :border="false" class="module_grid">
      <van-grid-item v-for="item in activity" :key="item.id" @click.native="getGoodsList(item)">
        <img :src="item.picUrl" class="module_icon">
        <span class="module_zi">{{ item.activityName }}</span>
      </van-grid-item>
    </van-grid>
    <div v-if="hot != false" class="home_selling">
      <div class="home_selling_title">
        <div class="home_title_icon" /><div class="home_title_font">热卖商品</div>
      </div>
      <div class="home_sellling_swipe">
        <van-swipe :loop="false" :width="90" :show-indicators="false">
          <van-swipe-item v-for="item in hot" :key="item.id" @click.native="goDetail(item.id)">
            <div class="home_selling_goods">
              <div class="home_goods_img"><img v-lazy="item.picUrl" class="home_selling_img"></div>
              <span class="home_selling_span">¥{{ item.retailPrice }}</span>
            </div>
          </van-swipe-item>
        </van-swipe>
      </div>
    </div>
  </div>
</template>

<script>
import { Grid, GridItem, Swipe, SwipeItem } from 'vant'

export default {
  name: 'HomeModule',
  components: {
    [Grid.name]: Grid,
    [GridItem.name]: GridItem,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem
  },
  props: {
    activity: {
      type: Array,
      default: () => []
    },
    hot: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {}
  },
  methods: {
    goDetail(id) {
      this.$router.push({ path: '/items/goodsDetail/' + id })
    },
    getGoodsList(row) {
      this.$router.push({ path: '/home/activityGoods', query: { id: row.id, name: row.activityName }})
    }
  }
}
</script>

<style lang="scss" scoped>
.module{
  .module_grid{
    .module_icon{
      width: 50px;
      height: 50px;
    }
    .module_zi{
      color: #7A7A7A;
      margin-top: 5px;
    }
    /deep/ .van-grid-item__content{
      background-color: #f7f8fa;
    }
    /deep/ .van-icon{
      font-size: 50px;
    }
  }
  .home_selling{
    width: 95%;
    margin: auto;
    background-color: #ffffff;
    border-radius: 10px;
    padding-bottom: 10px;
    .home_selling_title{
      width: 95%;
      margin: auto;
      padding-top: 10px;
      .home_title_icon{
        width: 5px;
        height: 20px;
        background-color: #FF4600;
        display: inline-block;
        vertical-align: middle;
      }
      .home_title_font{
        font-size: 15px;
        font-weight: bold;
        color: #343434;
        display: inline-block;
        vertical-align: middle;
        padding-left: 5px;
      }
    }
    .home_sellling_swipe{
      width: 95%;
      margin: auto;
      padding-top: 10px;
      .home_selling_goods{
        text-align: center;
        height: 95px;
        width: 80px;
        background: #FAF5F1;
        border-radius: 10px;
        .home_goods_img{
          width: 80px;
          height: 70px;
          overflow: hidden;
          border-top-left-radius: 10px;
          border-top-right-radius: 10px;
        }
        .home_selling_img{
          width: 80px;
        }
        .home_selling_span{
          color: #FF4700;
        }
      }
    }
  }
}
</style>
