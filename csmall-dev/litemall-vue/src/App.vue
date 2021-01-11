<template>
  <div id="app">
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive" class="view-router"/>
    </keep-alive>
    <router-view v-if="!$route.meta.keepAlive" class="view-router"/>
    <router-view name="tabbar"/>
    <cs />
    <logistics />
    <loading />
    <payment />
    <update />
  </div>
</template>
<script>
import Cs from '@/components/common/cs'
import Logistics from '@/components/common/logistics'
import Loading from '@/components/common/loading'
import Payment from '@/components/payPopup'
import Update from '@/components/common/update'

export default {
  components: {
    Loading,
    Cs,
    Payment,
    Update,
    Logistics
  },
  data() {
    return {
      exitTimer: 0
    }
  },
  mounted() {
    const This = this
    // const url = process.env.VUE_APP_EXTERNAL_SERVER
    document.addEventListener('deviceready', onDeviceReady, false)

    function onDeviceReady(event) {
      // 监听返回按钮
      document.addEventListener('backbutton', function(event) {
        console.log('page is :', This.isMenuPage(), This.isHomePage())
        event.preventDefault()
        event.stopPropagation()
        if (This.$store.state.payment.paying) {
          This.$store.dispatch('payment/close', This)
          return
        }
        if (This.$store.state.cs.csPopup) {
          This.$store.dispatch('cs/close')
          return
        }
        if (This.isMenuPage()) {
          // 跳转到首页
          This.$router.push({ name: 'home' })
        } else if (This.isHomePage()) {
          // 两次退出
          if (This.exitTimer === 0) {
            This.exitTimer++
            This.$toast({
              message: '再点一次退出',
              position: 'bottom',
              duration: 2000,
              onClose() {
                This.exitTimer = 0
              }
            })
          } else {
            navigator.app.exitApp()
          }
        } else {
          history.back()
        }
      }, false)
    }
  },
  methods: {
    isHomePage() {
      return this.$route.name === 'home'
    },
    isMenuPage() {
      return ['cart', 'user', 'class', 'record'].includes(this.$route.name)
    }
  }
}
</script>
<style lang="scss" src="./assets/scss/global.scss" />
