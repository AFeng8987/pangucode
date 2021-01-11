<template>
  <div class="orderGoods">
    <good
      v-for="good in goods"
      :key="good.id"
      :good="good"
    >
      <template #footer>
        <slot :good="good" name="footer" />
      </template>
    </good>
  </div>
</template>

<script>
import Good from './good'

export default {
  components: {
    Good
  },
  props: {
    data: {
      type: Array,
      default: () => []
    },
    gid: {
      type: [Number, String],
      default: 0
    }
  },
  data() {
    return {
    }
  },
  computed: {
    goods() {
      return this.gid ? this.data.filter(good => good.id === parseInt(this.gid)) : this.data
    }
  },
  watch: {},
  created() {},
  mounted() {
  },
  methods: {
    detail(id) {
      if (this.$route.name !== 'orderDetail') {
        this.$router.push({ name: 'orderDetail', params: { id: id }})
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.good {
  background: #f7f7f7;
  border-radius: 6px;
  &:not(:first-child) {
    margin-top: 5px;
  }
}
.good_tag {
  margin-right: 5px;
  margin-top: 8px;
}
</style>
