<template>
  <div class="cs">
    <van-popup v-model="pop" position="bottom" safe-area-inset-bottom @close="close">
      <van-cell-group class="list">
        <van-cell :title="'电话：' + phone" is-link @click="copy(phone)"/>
        <van-cell :title="'QQ：' + qq" is-link @click="copy(qq)"/>
        <van-cell :title="'微信：' + wx" is-link @click="copy(wx)"/>
        <div class="cs_buttom">
          <van-row type="flex" justify="center">
            <van-col span="6">
              <van-button class="button" type="info" size="small" @click="UpQQ('3090432293')">客服1号</van-button>
            </van-col>
            <van-col span="6">
              <van-button class="button" type="info" size="small" @click="UpQQ('1043155153')">客服2号</van-button>
            </van-col>
            <van-col span="6">
              <van-button class="button" type="info" size="small" @click="UpQQ('2834099642')">客服3号</van-button>
            </van-col>
          </van-row>
        </div>
      </van-cell-group>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import { Cell, CellGroup, Popup } from 'vant'
Vue.use(Popup).use(CellGroup).use(Cell)

import { mapState } from 'vuex'

export default {
  components: {},
  props: {
    // value: {
    //   type: Boolean,
    //   default: false
    // }
  },
  data() {
    return {
      // pop: false
    }
  },
  computed: {
    ...mapState({
      loaded: state => state.cs.loaded,
      qq: state => state.cs.qq,
      wx: state => state.cs.wx,
      phone: state => state.cs.phone,
      csPopup: state => state.cs.csPopup
    }),
    pop: {
      get() { return this.csPopup },
      set(value) { this.$store.commit('cs/csPopup', value) }
    }
  },
  created() {},
  mounted() {
  },
  methods: {
    close() {
      this.$store.dispatch('cs/close')
    },
    copy(text) {
      cordova.plugins.clipboard.copy(text)
      this.$toast('您已经复制成功啦')
      cordova.plugins.clipboard.paste(text => {})
    },
    UpQQ(qq) {
      const _this = this

      let scheme
      if (navigator.platform === 'iPhone') {
        scheme = 'mqq://'
      } else {
        scheme = 'com.tencent.mobileqq'
      }

      window.appAvailability.check(scheme, function() {
        cordova.InAppBrowser.open('mqqwpa://im/chat?chat_type=wpa&uin=' + qq + '&version=1&src_type=web&web_src=oicqzone.com ', '_system', 'location=no')
      }, function() {
        _this.$toast('您还未安装QQ客户端')
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  text-align: right;
}
.list {
  text-align: center;
}
.cs_buttom{
  height: 60px;
  line-height: 60px;
  .button{
    border-radius: 10px;
  }
}
</style>
