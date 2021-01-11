<template>
  <div class="ios_padding"/>
</template>

<style lang="scss" scoped>
.ios_padding{
  /* Status bar height on iOS 11.0 */
  height: constant(safe-area-inset-top);
  /* Status bar height on iOS 11+ */
  height: env(safe-area-inset-top);
}
</style>
