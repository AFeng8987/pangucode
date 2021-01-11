<template>
  <div class="extension">
    <header-bar :fixed="true" :border="false" :transparent="true" title="分享领优惠" left-arrow />
    <ios-danger-header class="top_header"/>

    <div ref="allImg" class="bcred">
      <img :src="fx" class="bcred_img" alt="">
      <h2 class="title">邀好友 领优惠</h2 >
      <div class="qrc">
        <div class="qrcode_container"><div ref="qrCodeUrl" :style="{ 'background-image': `url(${qrcodeBg})` }" class="qrcode" /></div>
        <p class="code_container">您的邀请码：{{ $route.query.code }}</p >
        <div class="btns">
          <van-button class="copy_btn" type="warning" color="#f60" size="small" round @click="copyQrCode()">复制</van-button>
          <van-button class="share_btn" type="primary" color="#1677ff" size="small" round @click="showShare = true">分享</van-button>
        </div>
      </div>
    </div>
    <ios-danger-bottom />

    <!-- <div class="extension_bottom_box">
      <div class="extension_bottom_flex">
        <div class="extension_bottom_photo" @click="showShare = true">立即分享</div>
      </div>
    </div> -->

    <van-share-sheet v-model="showShare" :options="options" title="立即分享" @select="onSelect" />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import iosDangerBottom from '@/components/iosCompatible/DangerBottom'
import fx from '!url-loader!@/assets/images/share_bg.png'
import qrcodeBg from '!url-loader!@/assets/images/qrcode_bg.png'
import friendIcon from '@/assets/images/friend.png'
import wechatIcon from '@/assets/images/wechat.png'
import saveIcon from '@/assets/images/share_save.png'
import logo from '!url-loader!@/assets/images/logo.png'

import iosDangerHeader from '@/components/iosCompatible/DangerHeader'

import html2canvas from 'html2canvas'

import QRCode from 'qrcodejs2'

import { ShareSheet } from 'vant'

export default {
  name: 'Extension',
  components: {
    iosDangerHeader,
    HeaderBar,
    iosDangerBottom,
    [ShareSheet.name]: ShareSheet
  },
  data() {
    return {
      logo,
      fx,
      qrcodeBg,
      link: process.env.VUE_APP_EXTERNAL_SERVER,
      url: 'a/r/',
      showShare: false,
      options: [
        { name: '微信', icon: wechatIcon, type: 'wx' },
        { name: '朋友圈', icon: friendIcon, type: 'py' },
        { name: '保存图片', icon: saveIcon, type: 'ph' }
      ]
    }
  },
  mounted() {
    this.creatQrCode()
  },
  methods: {
    copyQrCode() {
      const text = this.$route.query.code
      cordova.plugins.clipboard.copy(text)
      this.$toast('复制邀请码成功')
      cordova.plugins.clipboard.paste(text => {})
    },
    creatQrCode() {
      new QRCode(this.$refs.qrCodeUrl, {
        text: this.link + this.url + '?i=' + this.$route.query.code,
        width: 400,
        height: 400,
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
          description: '这是您的专属分享二维码',
          thumb: logo,
          media: {
            type: wechat.Type.WEBPAGE,
            webpageUrl: this.link + this.url + '?i=' + this.$route.query.code
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
      html2canvas(this.$refs.allImg, { useCORS: true }).then(canvas => {
        var params = { data: canvas.toDataURL(), prefix: 'img_', format: 'PNG', quality: 80, mediaScanner: true }
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
.extension{
  background-color: #f60;
  .top_header{
    background-color: #f60;
  }
  .bcred{
    margin-top: 0.8rem;
    position: relative;
    .bcred_img{
      width: 100%;
    }
    .qrc {
      margin: 0 8vw;
      padding: 40px 40px 20px;
      background: #fff;
      border-radius: 12px;
      text-align: center;
      .qrcode_container {
        display: flex;
        align-items: center;
        justify-content: center;
        .qrcode {
          padding: 2.5vw;
          background-repeat: no-repeat;
          background-size: contain;
          width: 100%;
          height: 100%;
          z-index: 2;
          // left: calc(50% - 1.3rem);
          // top: 8.34rem;
          /deep/ img, /deep/ canvas {
            width: 100%;
          }
        }
      }
    }
    .code_container {
      margin: 20px;
      // font-size: 16px;
    }
    .btns {
      display: flex;
      justify-content: space-between;
    }
    .copy_btn, .share_btn {
      padding: 0 0.8rem;
      font-size: 16px;
    }
  }
}

.title {
  text-align: center;
  margin: 20px 0;
  font-size: 0.8rem;
  font-weight: normal;
  color: #fff;
  text-shadow: 2px 2px 5px #2F2E2E;
}
</style>
