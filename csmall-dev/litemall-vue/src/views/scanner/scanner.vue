<template>
  <div class="scanner">
    <header-bar title="扫一扫" left-arrow >
      <template #right>
        <span @click="selectImg()">相册</span>
      </template>
    </header-bar>
    <div class="scroll-container scan-container">
      <div class="scan-box-container">
        <div class="scan-box">
          <div class="scan-box-area">
            <div class="top-left"/>
            <div class="top-right"/>
            <div class="bottom-left"/>
            <div class="bottom-right"/>
            <div class="light" @click="toogleLight()">
              <van-icon :name="light ? 'fire' : 'fire-o'"/>
              <br >
              <span>轻点{{ light?'关闭':'照亮' }}</span>
            </div>
          </div>
        </div>
        <!-- <div class="">
        放入框内，自动扫描
        </div>-->
      </div>
      <p class="tip">对准二维码，耐心等待自动扫描</p>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { userStatus } from '@/api/user'

import { Notify, Dialog } from 'vant'

import { isInternal } from '@/utils/configHelper'

export default {
  name: 'Scanner',
  components: {
    HeaderBar,
    [Notify.name]: Notify
  },
  data() {
    return {
      light: false
    }
  },
  mounted() {
    this.start()
  },
  beforeDestroy() {
    window.QRScanner.hide(status => {
      // console.log('[Scan.vue] 关闭扫描' + JSON.stringify(status))
    })
    window.QRScanner.destroy(function(status) {
      // console.log('[Scan.vue] 销毁扫码' + JSON.stringify(status))
    })
  },
  methods: {
    start() {
      const that = this
      window.QRScanner.prepare((err, status) => {
        console.log('qrscanner prepare: ', err, status)
        if (err) {
          console.log(err)
        }
        if (status) {
          if (status.authorized) {
            that.scan()
          } else if (status.denied) {
            that.$dialog.alert({ message: '权限已被拒绝，无法正常使用扫一扫功能。请开启权限后重试' }).then(() => {
              that.openSettings()
            })
          } else {
            that.openSettings()
          }
        }
      })
    },
    scan() {
      window.QRScanner.scan((err, text) => {
        if (err) {
          console.log(err)
        } else {
          this.successHandler(text)
        }
      })
      // 打开摄像头
      window.QRScanner.show()
    },
    // 处理二维码扫描结果
    successHandler(url) {
      console.log(url, 'uuu')
      if (isInternal(url)) {
        const typeMap = {
          '/a/r/': 'invite',
          '/a/d/': 'productDetail'
        }
        // type = invite && detail
        const path = url.replace(/^https?:\/\/[^\/]*([^\?]*)\?.*$/, '$1')
        const type = typeMap[path]
        const query = this.queryString(url)

        if (type === 'invite') {
          return userStatus().then(res => {
            Notify({ type: 'primary', message: '用户已登录' })
            this.$router.back()
          }).catch(err => {
            if (err.errno === 501) {
              // 未登录跳转到注册界面，把推荐码传过去
              this.$router.replace({
                path: '/login/registered',
                query: {
                  inviteCode: query.i,
                  title: 'register'
                }
              })
            }
          })
        } else {
          return this.$router.replace({
            path: '/items/goodsDetail/' + query.d
          })
        }
      }

      // 不是链接或外链，报错处理
      Dialog
        .alert({ message: '二维码不合法，请重新扫描' })
        .then(() => this.scan())
    },
    queryString(url) {
      url = url || ''
      const query = {}
      if (url.indexOf('?') !== -1) {
        const str = url.split('?')[1] || ''
        const strs = str.split('&')
        for (var i = 0; i < strs.length; i++) {
          const item = strs[i].split('=')
          query[item[0]] = unescape(item[1])
        }
      }
      return query
    },
    openSettings() {
      this.$dialog.alert({
        title: '没有权限',
        message: '没有摄像头权限，请前往设置中开启'
      }).then(() => {
        this.qrscanner.openSettings()
      })
    },
    toogleLight() {
      this.light ? window.QRScanner.disableLight() : window.QRScanner.enableLight()
      this.light = !this.light
    },

    selectImg() {
      const success = (imageUri) => {
        imageUri = imageUri.replace(/file:\/\/([^\?]*)(\?.*)?/, '$1')
        console.log('img uri: ' + imageUri)
        window.decodeQRImage.decode((result) => {
          console.log('decode result: ' + result)
          this.successHandler(result)
        }, (err) => {
          console.error(err)
        }, imageUri)
      }
      const failed = err => {
        console.log(err)
      }
      const options = {
        sourceType: window.Camera.PictureSourceType.PHOTOLIBRARY,
        destinationType: window.Camera.DestinationType.NATIVE_URI
      }
      navigator.camera.getPicture(success, failed, options)
    }
  }
}
</script>

<style lang="scss">
$header-height: 48px;
$primary: #ccc;
html,
body,
#app {
  height: 100%;
}
.scanner {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
}
//可滚动内容区域
.scroll-container {
  width: 100%;
  height: 70%;
  padding-top: $header-height;
  overflow: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    display: none;
  }
  background: transparent none !important;
}

.scan-container {
  background: rgba(0, 0, 0, 0);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .scan-box-container {
    display: flex;
    .scan-box {
      width: 6rem;
      height: 6rem;
      border: 1px solid $primary;
      background: rgba(0, 0, 0, 0);

      .scan-box-area {
        width: 6rem;
        height: 6rem;
        position: relative;

        .light {
          width: 6rem;
          position: absolute;
          color: rgba(255, 255, 255, 0.8);
          text-align: center;
          flex-direction: column;
          bottom: 0.32rem;

          i {
            font-size: 0.8rem;
            line-height: 0.8rem;
          }
          span {
            font-size: 13px;
          }
        }

        .top-left {
          position: absolute;
          top: -3px;
          left: -3px;
          width: 1rem;
          height: 1rem;
          border-top: 6px solid $primary;
          border-left: 6px solid $primary;
        }

        .top-right {
          position: absolute;
          top: -3px;
          right: -3px;
          width: 1rem;
          height: 1rem;
          border-top: 6px solid $primary;
          border-right: 6px solid $primary;
        }

        .bottom-left {
          position: absolute;
          bottom: -3px;
          left: -3px;
          width: 1rem;
          height: 1rem;
          border-bottom: 6px solid $primary;
          border-left: 6px solid $primary;
        }

        .bottom-right {
          position: absolute;
          bottom: -3px;
          right: -3px;
          width: 1rem;
          height: 1rem;
          border-bottom: 6px solid $primary;
          border-right: 6px solid $primary;
        }
      }
    }
  }
}
.tip {
  margin-top: 30px;
  color: #eee;
  font-size: 14px;
}
</style>
