<template>
  <div class="cart">
    <van-sticky>
      <ios-danger-header class="header_top"/>
      <van-nav-bar title="乾坤袋" />
    </van-sticky>

    <van-pull-refresh v-model="isLoading" success-text="刷新成功" style="margin-bottom: 50px;" @refresh="onRefresh">
      <div class="cart_box">
        <div class="cart_head">
          <van-row>
            <van-col :span="20">
              <van-checkbox v-model="checked" :disabled="goods.length === 0" checked-color="#F34444" @click="AllCheckClick" ><span class="cart_head_font">所有商品（共{{ checkedGoodsCount }}件商品）</span></van-checkbox>
            </van-col>
            <van-col :span="4">
              <div class="cart_head_font_name" @click="setDelete">{{ isEditor ? '管理' : '完成' }}</div>
            </van-col>
          </van-row>

        </div>
        <div v-if="goods.length === 0" class="cart_empty">
          <img :src="shopping" class="cart_empty_pic">
          <div class="cart_empty_name">您还未有商品存在于乾坤袋</div>
          <van-button class="cart_empty_button" size="small" type="danger" @click="$router.replace({ path: '/'})">去购物</van-button>
        </div>
        <div v-else class="cart_goods">
          <van-checkbox-group ref="checkboxGroup" v-model="result">
            <div v-for="item in goods" :key="item.id" class="cart_goods_box">
              <van-row type="flex" justify="center" align="center">
                <van-col :span="2">
                  <van-checkbox :name="item.id" checked-color="#F34444" @click="checkedClick(item)" />
                </van-col>
                <van-col :span="8">
                  <div class="cart_goods_img"><img :src="item.picUrl" class="cart_goods_card_img" @click="$router.push({ path: '/items/goodsDetail/' + item.goodsId })"></div>
                </van-col>
                <van-col :span="14">
                  <div class="cart_goods_card">
                    <div class="cart_goods_card_name">{{ item.goodsName }}</div>
                    <div>
                      <van-tag v-for="vv in item.specifications" :key="vv" class="cart_goods_card_tag" color="#EFEFEF">{{ vv }}</van-tag>
                    </div>
                    <div class="cart_goods_card_price">{{ item.price | yuan }}</div>
                    <div class="cart_goods_card_stepper"><van-stepper v-model.number="item.number" :max="10000" input-width="50" integer button-size="25" @plus="stepperPlus(item)" @minus="stepperMinus(item)" @focus="stepperFocus(item)" @blur="stepperBlur(item)" /></div>
                  </div>
                </van-col>
              </van-row>
            </div>
          </van-checkbox-group>
        </div>
      </div>
    </van-pull-refresh>

    <van-submit-bar v-if="isEditor" :price="checkedGoodsAmount" :disabled="!result.length" :loading="isSubmit" class="cart_bottom" button-text="结算" text-align="left" @submit="cartSubmit" />
    <div v-if="!isEditor" class="cart_bottom_box">
      <div class="cart_boottom_box-box">
        <van-button :disabled="!result.length" class="cart_bottom_button" round color="linear-gradient(to right, #ff6034, #ee0a24)" @click="handleDelete">删除</van-button>
      </div>
    </div>

    <ios-danger-bottom />
  </div>
</template>

<script>
import { NavBar, Checkbox, CheckboxGroup, Button, Card, Tag, Col, Row, Stepper, SubmitBar, Dialog, PullRefresh, Sticky } from 'vant'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import shopping from '@/assets/images/icon_moneybag.png'

import { cartList, cartChecked, cartUpdate, cartDelete, cartCheckout } from '@/api/cart'

import _ from 'lodash'

export default {
  name: 'Cart',
  components: {
    iosDangerHeader,
    iosDangerBottom,
    [NavBar.name]: NavBar,
    [Checkbox.name]: Checkbox,
    [CheckboxGroup.name]: CheckboxGroup,
    [Button.name]: Button,
    [Card.name]: Card,
    [Tag.name]: Tag,
    [Col.name]: Col,
    [Row.name]: Row,
    [Stepper.name]: Stepper,
    [SubmitBar.name]: SubmitBar,
    [Dialog.name]: Dialog,
    [PullRefresh.name]: PullRefresh,
    [Sticky.name]: Sticky
  },
  data() {
    return {
      shopping,
      checked: false,
      goods: [],
      result: [],
      stepperValue: 1,
      checkedGoodsAmount: 0,
      checkedGoodsCount: 0,
      isEditor: true,
      isSubmit: false,
      isLoading: false,
      goodsNumber: null,
      goodsId: null,
      cartIds: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      cartList().then(res => {
        this.goods = res.data.data.cartList
        this.result = this.getselectGoods()
        this.checkedGoodsCount = res.data.data.cartTotal.checkedGoodsCount
        this.getSelectAll(res.data.data.cartTotal)
        this.checkedGoodsAmount = this.totalPrice()
      })
    },
    // 判断是否全选
    getSelectAll(row) {
      if (row.goodsCount === row.checkedGoodsCount) {
        this.checked = true
      } else {
        this.checked = false
      }
    },
    // 获取全部商品的id
    getGoodsAll() {
      const goodsIds = []
      _.each(this.goods, item => {
        goodsIds.push(item.id)
      })
      return goodsIds
    },
    // 获取选中的商品
    getselectGoods() {
      const selectData = []
      _.each(this.goods, item => {
        if (item.checked === true) {
          selectData.push(item.id)
        }
      })
      return selectData
    },
    // 复选框选中/取消
    checkedClick(row) {
      let isChecked = 0
      const ids = []
      if (row.checked === false) {
        isChecked = 1
      }
      ids.push(row.id)

      this.callCheckApi({ ids, isChecked })
    },
    // 全选选中/曲线
    AllCheckClick() {
      let isChecked = 0
      const ids = this.getGoodsAll()
      this.$refs.checkboxGroup.toggleAll(false)
      if (this.checked === true) {
        isChecked = 1
        this.$refs.checkboxGroup.toggleAll(true)
      }

      this.callCheckApi({ ids, isChecked })
    },
    // 调用选择或取消选择商品的接口
    callCheckApi(row) {
      cartChecked(row).then(res => {
        this.goods = res.data.data.cartList
        this.result = this.getselectGoods()
        this.checkedGoodsCount = res.data.data.cartTotal.checkedGoodsCount
        this.getSelectAll(res.data.data.cartTotal)
        this.checkedGoodsAmount = this.totalPrice()
      })
    },
    // 切换结算/删除
    setDelete() {
      if (this.isEditor === true) {
        this.isEditor = false
      } else {
        this.isEditor = true
      }
    },
    // 步进器的加库存
    stepperPlus(item) {
      this.setInventory({ id: item.id, number: item.number + 1, goodsId: item.goodsId })
      this.goodsNumber = item.number
      this.goodsId = item.id
    },
    // 步进器的减库存
    stepperMinus(item) {
      this.setInventory({ id: item.id, number: item.number - 1, goodsId: item.goodsId })
      this.goodsNumber = item.number
      this.goodsId = item.id
    },
    stepperFocus(item) {
      this.goodsNumber = item.number
      this.goodsId = item.id
    },
    // 步进器的输入值
    stepperBlur(arg) {
      if (arg.number !== this.goodsNumber) {
        if (arg.number > 10000) {
          this.$toast({
            message: '该商品不能购买更多哟',
            duration: 1500,
            position: 'bottom'
          })
          this.reduction()
        } else if (arg.number < 1) {
          this.$toast({
            message: '该商品不能减少了哟',
            duration: 1500,
            position: 'bottom'
          })
          this.reduction()
        } else {
          this.setInventory({ id: arg.id, number: arg.number, goodsId: arg.goodsId })
        }
      }
    },
    // 修改库存数量的接口
    setInventory(obj) {
      this.$toast.loading({
        message: '加载中...',
        forbidClick: true
      })
      cartUpdate(obj).then(res => {
        this.checkedGoodsAmount = this.totalPrice()
        this.$toast.clear()
      }).catch(err => {
        this.$toast({
          message: err.data.errmsg,
          duration: 1500
        })
        this.reduction()
        this.$toast.clear()
      })
    },
    reduction() {
      this.goods.forEach(item => {
        if (item.id === this.goodsId) {
          item.number = this.goodsNumber
        }
      })
    },
    // 删除购物车勾选的商品
    handleDelete() {
      Dialog.confirm({
        message: '确定删除所选商品吗?',
        cancelButtonText: '再想想'
      }).then(() => {
        cartDelete({ ids: this.result }).then(res => {
          this.goods = res.data.data.cartList
          this.result = this.getselectGoods()
          this.checkedGoodsCount = res.data.data.cartTotal.checkedGoodsCount
          this.checkedGoodsAmount = this.totalPrice()
          this.getSelectAll(res.data.data.cartTotal)
        })
      }).catch(() => {})
    },
    // 提交订单
    cartSubmit() {
      this.isSubmit = true
      cartCheckout({ goodsId: null, number: null, specCodeId: null, cartIds: this.cartIds }).then(res => {
        this.$router.push({ path: '/cart/confirm', query: { cartIds: JSON.stringify(this.cartIds) }})
      }).catch(err => {
        this.$toast({
          message: err.data.errmsg,
          duration: 1500
        })
      })
    },
    // 下拉刷新
    onRefresh() {
      setTimeout(() => {
        this.isLoading = false
        this.init()
      }, 500)
    },
    // 计算选中商品的总价格
    totalPrice() {
      let price = null
      this.cartIds = []
      this.goods.forEach(item => {
        if (item.checked) {
          price = price + (item.price * item.number)
          this.cartIds.push(item.id)
        }
      })
      return price * 100
    }
  }
}
</script>

<style lang="scss" scoped>
.cart{
  background-color: #f7f8fa;
  .header_top{
    background-color: #ffffff;
  }
  .cart_bottom{
    bottom: 50px;
  }
  .cart_box{
    width: 95%;
    margin: 0.3rem auto;
    background-color: #ffffff;
    border-radius: 10px;
    .cart_head{
      width: 95%;
      margin: auto;
      padding-top: 10px;
      .cart_head_font{
        font-size: 14px;
      }
      .cart_head_font_name{
        font-size: 14px;
        text-align: right;
        color: #f34444;
      }
    }
    .cart_empty{
      height: 50vh;
      background-color: #ffffff;
      text-align: center;
      padding-top: 15vh;
      .cart_empty_pic{
        width: 50px;
        // height: 50px;
      }
      .cart_empty_name{
        color: #BCBCBC;
        font-size: 14px;
        margin-top: 20px;
      }
      .cart_empty_button{
        margin-top: 20px;
        border-radius: 10px;
        width: 100px;
      }
    }
    .cart_goods{
      width: 95%;
      margin: auto;
      padding-top: 10px;
      .cart_goods_box{
        padding-top: 10px;
        border-bottom: 1px solid #F5F5F5;
        padding-bottom: 10px;
        .cart_goods_img{
          width: 100px;
          height: 90px;
          overflow: hidden;
        }
        .cart_goods_card_img{
          width: 100px;
        }
        .cart_goods_card{
          .cart_goods_card_name{
            height: 0.85333;
            text-overflow: -o-ellipsis-lastline;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-bottom: 10px;
          }
          .cart_goods_card_tag{
            color: #BCBCBC;
            margin-bottom: 10px;
            margin-right: 5px;
          }
          .cart_goods_card_price{
            color: #F34444;
            font-size: 14px;
            display: inline-block;
            vertical-align: middle;
            width: 40%;
          }
          .cart_goods_card_stepper{
            display: inline-block;
            vertical-align: middle;
            text-align: right;
            width: 60%;
          }
        }
      }
    }
  }
  .cart_bottom_box{
    position: fixed;
    bottom: 50px;
    z-index: 100;
    width: 100%;
    /* Status bar height on iOS 11.0 */
    padding-bottom: constant(safe-area-inset-top);
    /* Status bar height on iOS 11+ */
    padding-bottom: env(safe-area-inset-top);
    background-color: #fff;
    user-select: none;
    .cart_boottom_box-box{
      height: 50px;
      padding: 0 16px;
      display: flex;
      align-items: center;
      // flex-direction: row-reverse;
      justify-content: flex-end;
      .cart_bottom_button{
        width: 110px;
        height: 40px;
      }
    }
  }
}
</style>
