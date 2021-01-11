<template>
  <header :class="{'header_fixed': fixed, 'transparent': transparent}" class="headerbar">
    <ios-danger-header/>

    <van-nav-bar
      :title="title"
      :left-text="leftText"
      :right-text="rightText"
      :left-arrow="leftArrow"
      :border="border"
      :z-index="zIndex"
      @click-right="clickRight()"
      @click-left="clickLeft()"
    >
      <!-- <template v-if="leftArrow" #left>
      <slot name="left">
        <van-icon class="van-nav-bar__arrow" name="arrow-left"></van-icon>
      </slot>
    </template> -->
      <!-- <template #title> -->
      <!-- <slot name="title"></slot> -->
      <!-- </template> -->
      <template #right>
        <slot name="right"/>
      </template>
    </van-nav-bar>

  </header>
</template>

<script>
import iosDangerHeader from '@/components/iosCompatible/DangerHeader'
import { NavBar } from 'vant'

export default {
  components: {
    iosDangerHeader,
    [NavBar.name]: NavBar
  },
  props: {
    transparent: { type: Boolean, default: false },
    leftArrow: { type: Boolean, default: false },
    leftText: { type: String, default: '' },
    preventLeftDefault: { type: Boolean, default: false },
    title: { type: String, default: '' },
    rightText: { type: String, default: '' },
    fixed: { type: Boolean, default: false },
    border: { type: Boolean, default: true },
    zIndex: { type: Number, default: 1 }
  },
  mounted() {
    const This = this
    document.addEventListener('deviceready', onDeviceReady, false)

    function onDeviceReady(event) {
      // 监听返回按钮
      document.addEventListener('backbutton', function(event) {
        if (This.preventLeftDefault) {
          event.preventDefault()
          This.$emit('clickLeft')
        }
      })
    }
  },
  methods: {
    clickLeft() {
      // console.log('clickLeft')
      if (!this.preventLeftDefault) {
        this.$router.back()
      }
      this.$emit('clickLeft')
    },
    clickRight() {
      // console.log('clickRight')
      this.$emit('clickRight')
    }
  }
}
</script>
<style lang="scss" scoped>
.header_fixed {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  z-index: 9999;
}

.headerbar{
  background-color: #ffffff;
  /deep/ .van-icon{
    color: black;
    font-size: 22px;
  }
}
.transparent {
  background: none;
  .van-nav-bar {
    background: none;
  }
  /deep/ .van-icon, /deep/ .van-nav-bar__title{
    color: #fff;
  }
}
</style>
