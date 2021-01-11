<template>
  <van-pull-refresh v-model="isLoading" success-text="刷新成功" @refresh="onRefresh">
    <div class="tabbar_user">
      <ios-danger-header class="header_top"/>
      <user-header :user="user" />
      <order-group :order="order" />
      <coupon-group />
      <information-center :code="invitationCode" />
      <user-module />
    </div>
  </van-pull-refresh>
</template>

<script>
import userHeader from './tabbar-user-header'
import orderGroup from './tabbar-user-order'
import couponGroup from './tabbar-user-coupon'
import informationCenter from './tabbar-user-information'
import userModule from './tabbar-user-module'

import iosDangerHeader from '@/components/iosCompatible/DangerHeader'

import { userIndex } from '@/api/user'
import { PullRefresh } from 'vant'

export default {
  name: 'User',
  components: {
    iosDangerHeader,
    [userHeader.name]: userHeader,
    [orderGroup.name]: orderGroup,
    [couponGroup.name]: couponGroup,
    informationCenter,
    [userModule.name]: userModule,
    [PullRefresh.name]: PullRefresh
  },
  data() {
    return {
      isLoading: false,
      user: {},
      order: {},
      invitationCode: ''
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      userIndex().then(res => {
        this.user = res.data.data.user
        this.order = res.data.data.order
        this.invitationCode = res.data.data.user.invitationCode
      })
    },
    // 下拉刷新
    onRefresh() {
      setTimeout(() => {
        userIndex().then(res => {
          this.isLoading = false
          this.user = res.data.data.user
          this.order = res.data.data.order
        })
      }, 500)
    }
  }
}
</script>

<style lang="scss" scoped>
.tabbar_user {
  background-color: #f7f8fa;
  .header_top{
    background-color: #FF4600;
  }
}
</style>
