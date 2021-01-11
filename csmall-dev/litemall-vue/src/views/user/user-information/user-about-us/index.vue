<template>
  <div class="about">
    <header-bar title="关于我们" left-arrow/>
    <div class="about_box">
      <van-image :src="logo" class="about_image" />
      <div class="about_version">版本: {{ version }}</div>
      <div class="about_use">感谢使用</div>
    </div>
    <van-cell-group class="about_group">
      <van-cell title="售后客服" is-link @click="$router.push({ path: '/user/after-sales'})" />
      <van-cell title="服务协议" is-link @click="$router.push({ path: '/user/service'})" />
      <van-cell title="隐私政策" is-link @click="$router.push({ path: '/user/privacy'})" />
      <van-cell v-if="latest" title="检查更新" is-link @click="checkUpdate" />
    </van-cell-group>

    <footer class="about_agreement">
      <div class="about_copyright">
        <p>深圳市盘古网络信息科技有限公司 版权所有</p>
        <p>Copyright &copy;2020 PG. All Rights Reserved</p>
      </div>
    </footer>

  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import logo from '@/assets/images/login-logo.png'
import { Image, Cell, CellGroup } from 'vant'
import { mapState } from 'vuex'

export default {
  name: 'AboutUs',
  components: {
    HeaderBar,
    [Image.name]: Image,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup
  },
  data() {
    return {
      logo
    }
  },
  computed: {
    ...mapState({
      latest: state => state.update.version,
      version: state => state.update.current
    })
  },
  methods: {
    checkUpdate() {
      this.$store.dispatch('update/check')
      if (this.version === this.latest) {
        this.$toast('当前已是最新版本')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.about{
  background-color: #f7f8fa;
  .about_box{
    margin-top: 20px;
    text-align: center;
    .about_image{
      width: 100px;
      height: 100px;
    }
    .about_version{
      color: #999;
      margin-top: 10px;
    }
    .about_use{
      color: #999;
      margin-top: 10px;
    }
  }
  .about_group{
    margin-top: 25px;
  }
  .about_agreement{
    position: fixed;
    bottom: 20px;
    width: 100%;
    .about_copyright{
      text-align: center;
      color: #999;
    }
  }
}
</style>
