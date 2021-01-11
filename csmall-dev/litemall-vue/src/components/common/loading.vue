<template>
  <div v-show="loading" class="loading">
    <van-loading type="spinner" size="38px" />
  </div>
</template>

<script>
import Vue from 'vue'
import { Loading } from 'vant'
Vue.use(Loading)

import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState({
      loading: state => state.loading.loading
    })
  }
}
</script>
<style lang="scss" scoped>
.loading {
  position: fixed;
  top: 40px;
  width: 100%;
  height: calc(100% - 40px);
  z-index: 99;
  display: flex;
  align-items: center;
    /* Status bar height on iOS 11.0 */
  margin-top: constant(safe-area-inset-top);
  /* Status bar height on iOS 11+ */
  margin-top: env(safe-area-inset-top);
    /* Status bar height on iOS 11.0 */
  margin-bottom: constant(safe-area-inset-bottom);
  /* Status bar height on iOS 11+ */
  margin-bottom: env(safe-area-inset-bottom);
}
</style>
