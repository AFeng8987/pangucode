<template>
  <div class="share">
    <header-bar :fixed="true" title="分享" left-arrow />
    <ios-danger-header class="header_top"/>

    <div ref="image" class="share_image">
      <div class="share_box">
        <div class="share_img"><img :src="goodsPic" alt=""></div>
        <van-row class="share_box_goods">
          <van-col span="12">
            <div class="share_goods">
              <div class="name">{{ goodsName }}</div>
              <div class="price">{{ retailPrice | yuan }}</div>
              <div class="share_nick"><div class="nick">{{ userName }}</div><div class="font">为你推荐</div></div>
            </div>
          </van-col>
          <van-col span="12">
            <div class="share_qr">
              <div class="qrcode"><div ref="qrCodeUrl" /></div>
              <div class="font">扫描或长按二维码</div>
            </div>
          </van-col>
        </van-row>
      </div>
      <van-divider :style="{ color: '#999999', borderColor: '#999999', padding: '0 30px', width: '100%' }" style="text-align: center">轻松购好物就上财神商城</van-divider>
    </div>

    <div class="share_bottom_box">
      <div class="share_bottom_flex">
        <div class="share_bottom_photo" @click="showShare = true">立即分享</div>
      </div>
    </div>

    <van-share-sheet v-model="showShare" :options="options" title="立即分享" @select="onSelect" />
    <!-- <ios-danger-bottom /> -->
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'

import { Divider, ShareSheet, Col, Row } from 'vant'

import QRCode from 'qrcodejs2'

import { shareGoods } from '@/api/goods'

import html2canvas from 'html2canvas'

import friend from '@/assets/images/friend.png'

import logo from '!url-loader!@/assets/images/logo.png'

export default {
  name: 'Share',
  components: {
    HeaderBar,
    iosDangerHeader,
    iosDangerBottom,
    [Divider.name]: Divider,
    [ShareSheet.name]: ShareSheet,
    [Col.name]: Col,
    [Row.name]: Row
  },
  data() {
    return {
      logo,
      retailPrice: 0,
      userName: '',
      goodsName: '',
      goodsPic: '',
      invitationCode: '',
      link: process.env.VUE_APP_EXTERNAL_SERVER,
      url: 'a/d/',
      showShare: false,
      options: [
        { name: '微信', icon: 'wechat', type: 'wx' },
        { name: '朋友圈', icon: friend, type: 'py' },
        { name: '保存图片', icon: 'poster', type: 'ph' }
      ]
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      shareGoods(this.$route.query.id).then(res => {
        this.retailPrice = res.retailPrice
        this.userName = res.userName
        this.goodsName = res.goodsName
        this.goodsPic = res.goodsPic
        this.invitationCode = res.invitationCode
        this.creatQrCode()
        console.log(this.link + this.url + '?i=' + this.invitationCode + '&d=' + this.$route.query.id)
      }).catch(err => {
        console.log(err)
      })
    },
    creatQrCode() {
      new QRCode(this.$refs.qrCodeUrl, {
        text: this.link + this.url + '?i=' + this.invitationCode + '&d=' + this.$route.query.id,
        width: 130,
        height: 130,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H
      })
    },

    onSelect(option) {
      if (option.type === 'ph') {
        this.savePhotoClick()
      } else if (option.type === 'wx') {
        this.isInstalled().then(success => {
          if (success) {
            this.share('wx')
          }
        })
      } else if (option.type === 'py') {
        this.isInstalled().then(success => {
          if (success) {
            this.share('py')
          }
        })
      }
    },
    // 检测微信客户端是否安装
    isInstalled() {
      const _this = this
      return new Promise((resolve, reject) => {
        window.Wechat.isInstalled(function(res) {
          if (res) {
            resolve(true)
          } else {
            _this.$toast('您未安装微信客户端')
            resolve(false)
          }
        }, function(err) {
          _this.$toast(err)
          reject()
        })
      })
    },
    // wx分享到好友
    share(vv) {
      const wechat = window.Wechat || {}
      wechat.share({
        message: {
          title: '财神商城分享',
          description: '商品分享二维码链接',
          thumb: logo,
          media: {
            type: wechat.Type.WEBPAGE,
            webpageUrl: this.link + this.url + '?i=' + this.invitationCode + '&d=' + this.$route.query.id
          }
        },
        scene: vv === 'wx' ? wechat.Scene.SESSION : wechat.Scene.TIMELINE
      })
      this.showShare = false
    },

    savePhotoClick() {
      const that = this
      const permissions = cordova.plugins.permissions
      permissions.checkPermission(permissions.WRITE_EXTERNAL_STORAGE, function(s) {
        if (!s.hasPermission) {
          permissions.requestPermission(permissions.WRITE_EXTERNAL_STORAGE, function(h) {
            if (h.hasPermission) {
              that.onDeviceReady()
            } else {
              that.$toast('取消保存')
            }
          }, function(err) {
            console.log(err)
          })
        } else {
          that.onDeviceReady()
        }
      }, function(err) {
        console.log(err)
      })
    },
    onDeviceReady() {
      const that = this
      html2canvas(this.$refs.image, { useCORS: true }).then(canvas => {
        var params = { data: canvas.toDataURL(), prefix: 'img_', format: 'PNG', quality: 90, mediaScanner: true }
        window.imageSaver.saveBase64Image(params, function(filePath) {
          that.$toast({ message: '保存成功', position: 'bottom' })
          that.showShare = false
        }, function(err) {
          console.log(err)
          that.$toast(err)
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.share{
  background-color: #f7f8fa;
  .header_top{
    background-color: #ffffff;
  }
  .share_image{
    padding-bottom: .2rem;
  }
  .share_box{
    background-color: #ffffff;
    width: 90%;
    margin: auto;
    margin-top: 2.3rem;
    border-radius: 5px;
    padding-bottom: 20px;
    .share_img{
      text-align: center;
      img {
        width: 60%;
      }
    }
    .share_box_goods{
      margin-top: 30px;
    }
    .share_goods{
      margin-left: .3rem;
      .name{
        font-size: 15px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2; //多行在这里修改数字即可
        overflow:hidden;
        -webkit-box-orient: vertical;
        height: 1.12rem;
      }
      .price{
        color: #F45A38;
        font-size: 18px;
        height: 1.86667rem;
        line-height: 1.86667rem;
      }
      .share_nick{
        height: 1.46667rem;
        line-height: 1.46667rem;
        .nick{
          font-weight: bold;
          color: #000000;
          display: inline-block;
          overflow: hidden;
          text-overflow:ellipsis;
          white-space: nowrap;
          vertical-align: middle;
          max-width: 70px;
        }
        .font{
          margin-left: .5rem;
          color: #999999;
          display: inline-block;
          vertical-align: middle;
        }
      }
    }
    .share_qr{
      text-align: center;
      .qrcode{
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .font{
        color: #999999;
        margin-top: 10px;
      }
    }
  }
  .share_bottom_box{
    position: fixed;
    bottom: 0;
    z-index: 100;
    width: 100%;
    /* Status bar height on iOS 11.0 */
    padding-bottom: constant(safe-area-inset-top);
    /* Status bar height on iOS 11+ */
    padding-bottom: env(safe-area-inset-top);
    background-color: #2F2E2E;
    user-select: none;
    .share_bottom_flex{
      height: 40px;
      display: flex;
      align-items: center;
      text-align: center;
      color: #ffffff;
      font-size: 15px;
      .share_bottom_photo{
        width: 100%;
        height: 40px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
