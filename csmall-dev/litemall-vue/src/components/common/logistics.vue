<template>
  <van-popup v-model="pop" :style="{ height: '70%' }" class="logistics" position="bottom" safe-area-inset-bottom @close="close">
    <van-cell :title="info.shipperName" :value="info.loCode" icon="location-o"/>
    <van-cell-group v-show="list.length" ref="list" class="lo_list">
      <van-cell v-for="item in list" :key="item.id" :title="item.AcceptStation" value-class="time" title-class="title">
        <template #default>
          <span v-html="lotime(item.AcceptTime)"/>
        </template>
      </van-cell>
    </van-cell-group>
    <van-empty
      v-show="!list.length"
      :description="info.reason"
    />
  </van-popup>
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
      info: state => state.logistics.info,
      list: state => state.logistics.list
    }),
    pop: {
      get() { return this.$store.state.logistics.loPopup },
      set(loPopup) {
        this.$store.commit('logistics/loPopup', loPopup)
      }
    }
  },
  created() {},
  mounted() {
  },
  methods: {
    lotime(time) {
      return time.replace(' ', '<br/>')
    },
    close() {
      this.$store.dispatch('logistics/close')
    }
  }
}
</script>
<style lang="scss" scoped>
.title {
  font-size: 12px;
}
.time {
  flex: 0.4;
}
</style>
