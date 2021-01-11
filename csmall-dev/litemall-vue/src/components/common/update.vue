
<template>
  <div/>
</template>
<script>
import { mapState } from 'vuex'

export default {
  components: {},
  computed: {
    ...mapState({
      version: state => state.update.version,
      needUpdate: state => state.update.needUpdate,
      force: state => state.update.force
    })
  },
  watch: {
    needUpdate(need) {
      if (need) {
        this.$dialog.confirm({
          title: '版本升级',
          message: `最新版本为${this.version},是否需要更新`,
          cancelButtonText: '暂不更新',
          showCancelButton: !this.force,
          confirmButtonText: '下载更新'
        }).then(() => {
          this.$toast('后台静默下载，请稍后')
          this.$store.dispatch('update/download')
        }).catch(() => {})
      }
    }
  },
  mounted() {
    document.addEventListener('deviceready', (event) => {
      this.$store.dispatch('update/check')
    }, false)
  }
}
</script>
