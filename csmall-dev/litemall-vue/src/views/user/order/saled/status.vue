<template>
  <div class="saledStatus">
    <div v-if="isApply" class="applyed">
      <h3 class="title">您已成功发起退款申请，请耐心等待商家处理</h3>
      <p>商家同意申请，系统将退款给您。</p>
      <p>如果商家拒绝，您可以重新发起申请。</p>
    </div>
    <div v-if="isReturning" class="return_addr">
      <h3>请按以下地址退货：</h3>
      <p>姓名：{{ addr.receiveContacts }}</p>
      <p>电话： {{ addr.receivePhone }}</p>
      <p>详细地址：{{ addr.receiveAddress }}</p>
    </div>
  </div>
</template>

<script>

export default {
  props: {
    status: {
      type: Number,
      default: () => {}
    },
    addr: {
      type: Object,
      default: () => {}
    }
  },
  computed: {
    isApply() {
      return [1, 11, 12, 2, 3, 4].indexOf(this.status) !== -1
    },
    isReturning() {
      return this.addr.receiveContacts && [1].indexOf(this.status) !== -1
    }
  }
}
</script>
<style lang="scss" scoped>
.saledStatus {
  padding: 10px 20px;
  h3 {
    margin: 10px 0;
  }
  p {
    line-height: 150%;
    text-indent: 1em;
  }
}
</style>
