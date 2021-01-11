<template>
  <van-tabbar v-model="active" class="tab-bar" style="z-index: 1999">
    <van-tabbar-item
      v-for="tab in alliance"
      :to="tab.path"
      :dot="tab.dot"
      :info="tab.info"
      :key="tab.pathName"
      :name="tab.pathName">
      {{ tab.name }}
      <template slot="icon">
        <van-icon :name="active === tab.pathName ? (tab.iconActive || tab.icon) : tab.icon" class="my_icon" class-prefix="my-icon" />
      </template>
    </van-tabbar-item>
  </van-tabbar>
</template>

<script>
import { Tabbar, TabbarItem } from 'vant'

import cartIcon from '../../assets/images/icon_cart.png'
import cartIconActive from '../../assets/images/icon_cart_hover.png'

export default {

  components: {
    [Tabbar.name]: Tabbar,
    [TabbarItem.name]: TabbarItem
  },
  data() {
    return {
      active: 'home',
      tabbar: [
        {
          name: '首页',
          path: '/',
          pathName: 'home',
          icon: 'iconshouye',
          dot: false,
          info: ''
        },
        {
          name: '分类',
          path: '/items',
          pathName: 'class',
          icon: 'iconfenlei1',
          dot: false,
          info: ''
        },
        {
          name: '乾坤袋',
          path: '/cart',
          pathName: 'cart',
          icon: cartIcon,
          iconActive: cartIconActive,
          dot: false,
          info: ''
        },
        {
          name: '我的',
          path: '/user',
          pathName: 'user',
          icon: 'iconwode1',
          dot: false,
          info: ''
        }
      ]
    }
  },

  computed: {
    alliance() {
      const tab = this.tabbar
      if (this.$store.state.identity.alliance === true) {
        tab.splice(3, 0, {
          name: '数据中心',
          path: '/record',
          pathName: 'record',
          icon: 'iconshuju',
          dot: false,
          info: ''
        })
      }
      return tab
    }
  },

  watch: {
    $route: 'changeActive'
  },

  created() {
    this.$store.dispatch('getInfo')
    const toName = this.$route.name
    this.setActive(toName)
  },

  methods: {
    changeActive({ name }) {
      this.setActive(name)
    },
    setActive(name) {
      this.active = name
    }
  }
}
</script>

<style lang="scss" scoped>
.my_icon{
  font-size: 22px;
}
.tab-bar {
    /* Status bar height on iOS 11.0 */
  padding-bottom: constant(safe-area-inset-bottom);
  /* Status bar height on iOS 11+ */
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
