<template>
  <div class="module_header">
    <div class="module_titel">
      <div class="module_my">重要信息</div>
    </div>

    <div class="module_icon">
      <van-grid :border="false" class="module_grid">
        <van-grid-item :icon="jiemeng" @click="$router.push({path: '/user/merchants'})">
          <span slot="text" class="module_name">加入加盟商</span>
        </van-grid-item>
      </van-grid>
    </div>
  </div>
</template>

<script>
import { Grid, GridItem } from 'vant'
import jiemeng from '@/assets/images/jiameng.png'

export default {
  name: 'UserModule',
  components: {
    [Grid.name]: Grid,
    [GridItem.name]: GridItem
  },
  data() {
    return {
      jiemeng
    }
  }
}
</script>

<style lang="scss" scoped>
.module_header{
  background-color: #ffffff;
  margin-top: 10px;
  .module_titel{
    width: 90%;
    margin: auto;
    padding-top: 14px;
    .module_my{
      font-size: 15px;
    }
  }
  .module_icon{
    margin-top: 5px;
    padding-bottom: 12px;
    .module_grid{
      .module_name {
        padding-top: 6px;
        color: #000000;
      }
      /deep/ .van-icon{
        font-size: 36px;
      }
    }
  }
}
</style>
