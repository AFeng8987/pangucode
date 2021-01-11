<template>
  <div class="success">
    <ios-danger-header class="header_top"/>
    <div class="success_top">
      <van-row type="flex" justify="center" align="center" class="success_row">
        <van-col :span="12">
          <div class="success_col">
            <div class="success_titel">购买成功</div>
            <div class="success_font">您的订单将在卖家确认后发货</div>
          </div>
        </van-col>
        <van-col :span="12">
          <div class="success_pic"><img :src="buy" class="success_img"></div>
        </van-col>
      </van-row>
    </div>
    <div class="success_button">
      <van-row>
        <van-col :span="12">
          <div class="success_button_van"><van-button color="#FEBF00" size="small" @click="$router.replace({ path: '/' })">继续逛逛</van-button></div>
        </van-col>
        <van-col :span="12">
          <div class="success_button_van"><van-button color="#FEBF00" size="small" @click="$router.replace({ name: 'order', params: { active: 2 }})">查看订单</van-button></div>
        </van-col>
      </van-row>
    </div>
  </div>
</template>

<script>
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import buy from '@/assets/images/buy-success.png'

import { Col, Row, Button } from 'vant'

export default {
  name: 'Success',
  components: {
    iosDangerHeader,
    iosDangerBottom,
    [Col.name]: Col,
    [Row.name]: Row,
    [Button.name]: Button
  },
  data() {
    return {
      buy
    }
  }
}
</script>

<style lang="scss" scoped>
.success{
  .header_top{
    background-color: #F24445;
  }
  .success_top{
    height: 110px;
    background-color: #F24445;
    .success_row{
      .success_col{
        margin-left: 20px;
        .success_titel{
          margin-top: 40px;
          font-size: 16px;
          color: #ffffff;
        }
        .success_font{
          color: #ffffff;
          margin-top: 10px;
        }
      }
      .success_pic{
        margin-top: 40px;
        text-align: right;
        margin-right: 20px;
        .success_img{
          width: 50px;
          height: 50px;
        }
      }
    }
  }
  .success_button{
    margin-top: 40px;
    .success_button_van{
      text-align: center;
      button {
        border-radius: 10px;
        width: 100px;
      }
    }
  }
}
</style>
