<template>
  <div class="goodsDetail">
    <header-bar :fixed="true" title="商品详情" left-arrow>
      <template #right>
        <van-icon name="share" @click="shareClick" />
      </template>
    </header-bar>
    <ios-danger-header/>
    <van-swipe :loop="false" style="margin-top: 1.22667rem;" @change="swipeChange">
      <van-swipe-item v-if="videoUrl">
        <video
          id="myVideo"
          playsinline="true"
          webkit-playsinline="true"
          class="video-js vjs-big-play-centered"
          style="width:100%;height: 100vw;object-fit: contain">
          <source :src="videoUrl" type="video/mp4" >
        </video>
      </van-swipe-item>
      <van-swipe-item v-for="(image, index) in goods.info.gallery" :key="index" @click="imgClick(goods.info.gallery, index)">
        <div class="goods_swipe_img"><img v-lazy="image" width="100%"></div>
      </van-swipe-item>
    </van-swipe>

    <van-cell-group class="goods_cell_group">
      <van-cell>
        <div class="goods_box">
          <van-row>
            <van-col span="14">
              <span class="goods_price">{{ goods.info.retailPrice | yuan }}</span>
              <span class="goods_market_price">{{ goods.info.counterPrice | yuan }}</span>
            </van-col>
            <van-col span="10">
              <div class="sales">销量：{{ goods.info.sales }}</div>
            </van-col>
          </van-row>
        </div>
        <div class="goods_title">
          {{ goods.info.name }}
        </div>
        <div class="goods_intro">{{ goods.info.brief }}</div>
      </van-cell>
    </van-cell-group>

    <div class="goods_cell_group">
      <van-cell-group>
        <van-cell
          title="规格"
          is-link
          value="请选择"
          @click.native="skuClick"
        />
        <van-cell title="属性" is-link @click.native="propsPopup = true"/>
        <van-cell v-if="goods.freight === 0" value="全场包邮" title="运费"/>
        <van-cell v-else :value="'不满'+ goods.freightLimit +'需收取' + goods.freight +'元'" title="运费"/>
      </van-cell-group>
      <van-sku
        v-model="showSku"
        :sku="sku"
        :hide-stock="sku.hide_stock"
        :goods="skuGoods"
        :goods-id="goods.info.id"
        add-cart-text="加入乾坤袋"
        @buy-clicked="buyGoods"
        @add-cart="addCart"
      />
    </div>

    <van-popup v-model="propsPopup" position="bottom">
      <popup-props :props-str="props_str"/>
    </van-popup>

    <div class="goods_desc">
      <div class="goods_desc_title">商品详情</div>
      <div v-if="goods.info.detail" class="item_desc_wrap" v-html="goods.info.detail"/>
      <div v-else class="item_desc_wrap" style="text-align: center;">
        <p>无详情</p>
      </div>
    </div>

    <van-goods-action>
      <van-goods-action-icon icon="phone-o" text="客服" @click="popCS"/>
      <van-goods-action-icon :info="(cartInfo > 0) ? cartInfo : ''" icon="cart-o" text="乾坤袋" @click="toCart"/>
      <van-goods-action-button type="warning" text="加入乾坤袋" @click="skuClick"/>
      <van-goods-action-button type="danger" text="立即购买" @click="skuClick"/>
    </van-goods-action>

    <div v-show="showAnimate" class="add_cart_animate">
      <van-icon :name="addCartIcon" class="add_cart_icon" />
    </div>

    <ios-danger-bottom />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'
import popupProps from './popup-props'
import addCartIcon from '@/assets/images/icon_gold.png'

import { Swipe, SwipeItem, Cell, CellGroup, Sku, Popup, GoodsAction, GoodsActionIcon, GoodsActionButton, ImagePreview, Col, Row } from 'vant'

import { goodsDetail, cartGoodsCount, cartAdd } from '@/api/goods'

import { cartCheckout } from '@/api/cart'

import _ from 'lodash'

export default {
  name: 'GoodsDetail',
  components: {
    HeaderBar,
    iosDangerBottom,
    iosDangerHeader,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Sku.name]: Sku,
    [popupProps.name]: popupProps,
    [Popup.name]: Popup,
    [GoodsAction.name]: GoodsAction,
    [GoodsActionIcon.name]: GoodsActionIcon,
    [GoodsActionButton.name]: GoodsActionButton,
    [ImagePreview.name]: ImagePreview,
    [Col.name]: Col,
    [Row.name]: Row
  },
  props: {
    itemId: {
      type: [String, Number],
      default: 0
    }
  },
  data() {
    return {
      addCartIcon,
      showAnimate: false,
      goods: {
        freight: 0,
        freightLimit: 0,
        info: {
          gallery: [],
          retailPrice: 0,
          counterPrice: 0
        }
      },
      sku: {
        tree: [],
        list: [],
        price: '1.00' // 默认价格（单位元）
      },
      skuGoods: {
        // 商品标题
        title: '',
        // 默认商品 sku 缩略图
        picture: ''
      },
      cartInfo: 0,
      selectSku: {
        selectedNum: 1,
        selectedSkuComb: {
          sku_str: 'aa'
        }
      },
      propsPopup: false,
      showSku: false,
      videoUrl: '',
      myVideo: ''
    }
  },
  computed: {
    props_str() {
      const props_arr = []
      _.each(this.goods.attribute, json => {
        props_arr.push([json['attribute'], json['value']])
      })

      return props_arr || []
    }
  },
  created() {
    this.initData()
  },
  methods: {
    skuClick() {
      this.showSku = true
    },
    initData() {
      goodsDetail({ id: this.itemId }).then(res => {
        res.data.data.info.gallery = JSON.parse(res.data.data.info.gallery)
        this.goods = res.data.data
        this.videoUrl = res.data.data.info.shareUrl
        if (this.videoUrl) {
          this.$nextTick(() => {
            this.myVideo = this.$video(document.getElementById('myVideo'), {
              controls: true,
              autoplay: false,
              poster: res.data.data.info.picUrl,
              preload: 'auto',
              language: 'zh-CN',
              muted: true
            })
          })
        }
        this.skuAdapter()
      })

      cartGoodsCount().then(res => {
        this.cartInfo = res.data.data
      })
    },
    swipeChange(res) {
      if (res !== 0) {
        this.myVideo.pause()
      }
    },
    toCart() {
      this.$router.replace({
        name: 'cart'
      })
    },
    popCS() {
      this.$store.dispatch('cs/show')
    },
    getProductId(s1, s2) {
      var productId
      var s1_name
      var s2_name
      _.each(this.goods.specificationList, specification => {
        _.each(specification.valueList, specValue => {
          if (specValue.id === s1) {
            s1_name = specValue.value
          } else if (specValue.id === s2) {
            s2_name = specValue.value
          }
        })
      })

      _.each(this.goods.productList, v => {
        const result = _.without(v.specifications, s1_name, s2_name)
        if (result.length === 0) {
          productId = v.id
        }
      })
      return productId
    },
    getProductIdByOne(s1) {
      var productId
      var s1_name
      _.each(this.goods.specificationList, specification => {
        _.each(specification.valueList, specValue => {
          if (specValue.id === s1) {
            s1_name = specValue.value
            return
          }
        })
      })

      _.each(this.goods.productList, v => {
        const result = _.without(v.specifications, s1_name)
        if (result.length === 0) {
          productId = v.id
        }
      })
      return productId
    },
    addCart(data) {
      const that = this
      const params = {
        goodsId: data.goodsId,
        number: data.selectedNum,
        specCodeId: data.selectedSkuComb.specCodeId,
        goodsCodeId: this.goods.info.goodsCodeId
      }
      // if (_.has(data.selectedSkuComb, 's3')) {
      //   this.$toast({
      //     message: '目前仅支持两规格',
      //     duration: 1500
      //   })
      //   return
      // } else 
      if (_.has(data.selectedSkuComb, 's2')) {
        params.productId = this.getProductId(
          data.selectedSkuComb.s1,
          data.selectedSkuComb.s2
        )
      } else {
        params.productId = this.getProductIdByOne(data.selectedSkuComb.s1)
      }

      cartAdd(params).then(res => {
        this.cartInfo = res.data.data.cartCount
        this.$toast({
          message: '已添加至乾坤袋',
          duration: 1500
        })
        that.showSku = false
        this.showAddCartAnimate()
      }).catch(err => {
        console.log(err)
        this.$toast({
          message: err.data.errmsg,
          duration: 1500
        })
      })
    },
    buyGoods(data) {
      // if (_.has(data.selectedSkuComb, 's3')) {
      //   this.$toast({
      //     message: '目前仅支持两规格',
      //     duration: 1500
      //   })
      //   return
      // }
      cartCheckout({ goodsId: data.goodsId, number: data.selectedNum, specCodeId: data.selectedSkuComb.specCodeId, cartIds: null }).then(res => {
        this.$router.push({ path: '/cart/confirm', query: { goodsId: data.goodsId, number: data.selectedNum, specCodeId: data.selectedSkuComb.specCodeId }})
      }).catch(err => {
        this.$toast({
          message: err.data.errmsg,
          duration: 1500
        })
      })
    },
    skuAdapter() {
      const tree = this.setSkuTree()
      const list = this.setSkuList()
      const total = this.getTotalNum()
      const skuInfo = {
        price: this.goods.info.retailPrice, // 未选择规格时的价格
        stock_num: total, // TODO 总库存
        collection_id: '', // 无规格商品skuId取collection_id，否则取所选sku组合对应的id
        none_sku: false, // 是否无规格商品
        hide_stock: false // 是否隐藏剩余库存
      }
      this.sku = {
        tree,
        list,
        ...skuInfo
      }

      this.skuGoods = {
        title: this.goods.info.name,
        picture: this.goods.info.picUrl
      }
    },
    getTotalNum() {
      let num = null
      _.each(this.goods.specodeProductList, v => {
        num = Number(num) + Number(v.totalStock)
      })
      return num
    },
    setSkuList() {
      var sku_list = []
      _.each(this.goods.specodeProductList, v => {
        var sku_list_obj = {}
        _.each(v.specsDesc, (specificationName, index) => {
          sku_list_obj['s' + (~~index + 1)] = this.findSpecValueIdByName(
            specificationName
          )
        })

        sku_list_obj.price = v.shopPrice * 100
        sku_list_obj.stock_num = v.totalStock
        sku_list_obj.specCodeId = v.id
        sku_list.push(sku_list_obj)
      })

      return sku_list
    },
    findSpecValueIdByName(name) {
      let id = 0
      _.each(this.goods.goodsSpecList, specification => {
        _.each(specification, specValue => {
          _.each(specValue, cc => {
            if (cc.value === name) {
              id = cc.id
              return
            }
          })
        })
        if (id !== 0) {
          return
        }
      })
      return id
    },
    setSkuTree() {
      const specifications = []
      _.each(this.goods.goodsSpecList, (v, k) => {
        _.each(v, (vv, z) => {
          const values = []
          _.each(vv, cc => {
            const imgs = this.goods.specodeProductList.reduce((list, curr) => {
              if (list.indexOf(curr.url) === -1) {
                if (z === '颜色') {
                  list.push({ slist: curr.specsDesc, url: curr.url })
                } else {
                  list.push({ slist: curr.specsDesc })
                }
              }
              return list
            }, [])
            const img = imgs.find(spec => {
              if (spec.slist.indexOf(cc.value) !== -1 && !spec.selected) {
                spec.selected = true
                return true
              }
            }) || {}
            values.push({
              id: cc.id,
              imgUrl: img.url || '',
              name: cc.value
            })
          })
          specifications.push({
            k: z,
            v: values,
            k_s: 's' + (~~k + 1)
          })
        })
      })

      return specifications
    },
    showAddCartAnimate() {
      this.showAnimate = false
      this.$nextTick(() => { this.showAnimate = true })
    },

    shareClick() {
      this.$router.push({ path: '/items/goodsShare', query: { id: this.itemId }})
    },

    imgClick(obj, index) {
      ImagePreview({ images: obj, startPosition: index, closeable: true })
    }
  }
}
</script>

<style lang="scss" scoped>

@keyframes drop_cart-keyframes {
  0% {transform:translate(50vw, 50vh) scale(1) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  3.45% {transform:translate(49.3931vw, 47.3759vh) scale(1.1034) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  6.9% {transform:translate(48.7862vw, 44.9392vh) scale(1.2069) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  10.34% {transform:translate(48.1793vw, 42.6899vh) scale(1.3103) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  13.79% {transform:translate(47.5724vw, 40.6281vh) scale(1.4138) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  17.24% {transform:translate(46.9655vw, 38.7537vh) scale(1.5172) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  20.69% {transform:translate(46.3586vw, 37.0668vh) scale(1.6207) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  24.14% {transform:translate(45.7517vw, 35.5673vh) scale(1.7241) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  27.59% {transform:translate(45.1448vw, 34.2552vh) scale(1.8276) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  31.03% {transform:translate(44.5379vw, 33.1306vh) scale(1.931) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  34.48% {transform:translate(43.9310vw, 32.1934vh) scale(2.0345) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  37.93% {transform:translate(43.3241vw, 31.4436vh) scale(2.1379) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  41.38% {transform:translate(42.7173vw, 30.8813vh) scale(2.2414) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  44.83% {transform:translate(42.1103vw, 30.5064vh) scale(2.3448) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  48.28% {transform:translate(41.5034vw, 30.3190vh) scale(2.4483) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  51.72% {transform:translate(40.4368vw, 30.3931vh) scale(2.4483) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  55.17% {transform:translate(38.9103vw, 31.1716vh) scale(2.3448) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  58.62% {transform:translate(37.3839vw, 32.7196vh) scale(2.2414) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  62.07% {transform:translate(35.8575vw, 35.0188vh) scale(2.1379) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  65.52% {transform:translate(34.3310vw, 38.0423vh) scale(2.0345) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  68.97% {transform:translate(32.8046vw, 41.7547vh) scale(1.931) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  72.41% {transform:translate(31.2782vw, 46.1124vh) scale(1.8276) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  75.86% {transform:translate(29.7517vw, 51.0643vh) scale(1.7241) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  79.31% {transform:translate(28.2253vw, 56.5525vh) scale(1.6207) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  82.76% {transform:translate(26.6989vw, 62.5124vh) scale(1.5172) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  86.21% {transform:translate(25.1724vw, 68.8744vh) scale(1.4138) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  89.66% {transform:translate(23.6460vw, 75.5637vh) scale(1.3103) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  93.1% {transform:translate(22.1195vw, 82.5020vh) scale(1.2069) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  96.55% {transform:translate(20.5931vw, 89.6078vh) scale(1.1034) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  99.00% {transform:translate(19.0667vw, 96.7980vh) scale(1) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  99.9999% {transform:translate(19.0667vw, 96.7980vh) scale(1) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
  100% {transform:translate(-139.33333vw,  449.75369vh) scale(1) rotateX(0deg) rotateY(0deg) rotateZ(0deg) translate(-50%, -50%);}
}

.add_cart_animate {
  position: absolute;
  left: 0;
  top: 0;
  transform: translate(50vw, 50vh) translate(-50%, -50%);
  animation-name: drop_cart-keyframes;
  animation-duration: 1500ms;
  animation-delay: 0ms;
  animation-fill-mode: forwards;
  animation-timing-function: linear;
  animation-iteration-count: 1;
  transform-origin: 0 0;
  z-index: 2;
}
.add_cart_icon img {
  width: 24px;
  height: auto;
}
.goodsDetail{
  .goods_swipe_img{
    height: 100vw;
    width: 100%;
    overflow: hidden;
    img {
      max-width: 100%;
    }
  }

  .goods_cell_group{
    margin-bottom: 15px;

    .goods_price{
      font-size: 20px;
      color: $red;
      margin-right: 10px;
    }

    .goods_market_price{
      color: $font-color-gray;
      text-decoration: line-through;
      font-size: $font-size-small;
    }

    .sales{
      text-align: right;
      color: $font-color-gray;
    }

    // .goods_title{
    //   line-height: 1.4;
    // }

    .goods_intro{
      line-height: 18px;
      margin: 5px 0;
      font-size: $font-size-small;
      color: $font-color-gray;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 3;
    }
  }

  .goods_desc{
    background-color: #fff;
    /deep/ p {
      padding: 0 10px;
      margin-block-start: 0 !important;
      margin-block-end: 0 !important;
    }
    /deep/ img {
      max-width: 100%;
      display: block;
      height: auto;
    }

    .goods_desc_title{
      @include one-border;
      padding: 10px 0;
      text-align: center;
    }
  }
}
</style>
<style>
.vjs-paused .vjs-big-play-button,
.vjs-paused.vjs-has-started .vjs-big-play-button {
    display: block;
}
.video-js .vjs-big-play-button{
    font-size: 2.5em;
    line-height: 2.3em;
    height: 2.5em;
    width: 2.5em;
    -webkit-border-radius: 2.5em;
    -moz-border-radius: 2.5em;
    border-radius: 2.5em;
    background-color: #73859f;
    background-color: rgba(115,133,159,.5);
    border-width: 0.15em;
    margin-top: -1.25em;
    margin-left: -1.75em;
}
/* 中间的播放箭头 */
.vjs-big-play-button .vjs-icon-placeholder {
    font-size: 1.63em;
}
/* 加载圆圈 */
.vjs-loading-spinner {
    font-size: 2.5em;
    width: 2em;
    height: 2em;
    border-radius: 1em;
    margin-top: -1em;
    margin-left: -1.5em;
}
.video-js.vjs-playing .vjs-tech {
    pointer-events: auto;
}
</style>
