<template>
  <van-card
    :num="good.number"
    :price="good.goodsPrice || good.price | price"
    :title="good.goodsName"
    :thumb="good.picUrl"
    class="orderGood"
  >
    <template #tags>
      <van-tag v-for="sku in good.specifications" :key="sku" class="good_tag" plain type="danger">{{ sku }}</van-tag>
    </template>
    <template #footer>
      <slot :good="good" name="footer" />
    </template>
  </van-card>
</template>

<script>
import Vue from 'vue'
import { Card, Tag } from 'vant'
Vue.use(Card).use(Tag)

export default {
  props: {
    good: {
      type: Object,
      default: () => {}
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
