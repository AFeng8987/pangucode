<template>
  <div class="user_header">
    <van-row type="flex" align="center" class="user_header_content">
      <van-col :span="5" class="user_avatar">
        <van-image :src=" user.avatar || avatar_default" class="user_image" fit="cover" round />
      </van-col>
      <van-col :span="13">
        <div class="user_name">{{ user.nickName }}</div>
        <div class="user_member">{{ user.alliance === false ? "普通用户":"加盟商" }}</div>
        <div class="user_code"><span>邀请码：{{ user.invitationCode }}</span><van-icon :name="copys" class="user_code_icon" @click="copyInvitationClick" /></div>
      </van-col>
      <van-col :span="6" class="user_header_icon">
        <van-icon :name="scans" @click="scanClick"/>
        <van-icon name="setting-o" @click="setUpClick" />
      </van-col>
    </van-row>
  </div>
</template>

<script>
import { Row, Col, Image } from 'vant'
import avatar_default from '@/assets/images/avatar_default.png'
import copys from '@/assets/images/copy.png'
import scans from '@/assets/images/scan.png'

export default {
  name: 'UserHeader',
  components: {
    [Row.name]: Row,
    [Col.name]: Col,
    [Image.name]: Image
  },
  props: {
    user: {
      type: Object,
      default: null
    }
  },

  data() {
    return {
      scans,
      avatar_default,
      copys
    }
  },

  methods: {
    setUpClick() {
      this.$router.push({
        path: '/user/information'
      })
    },
    copyInvitationClick() {
      const that = this
      var text = this.user.invitationCode
      cordova.plugins.clipboard.copy(text)
      cordova.plugins.clipboard.paste(function(text) { that.$toast('您已经复制成功啦') })
    },
    scanClick() {
      this.$router.push('qrscanner')
    }
  }
}
</script>

<style lang="scss" scoped>
.user_header{
  background-color: #FF4600;
  height: 140px;
  color: #ffffff;
  .user_header_content{
    height: 110px;
    .user_avatar{
      text-align: center;
      .user_image{
        width: 45px;
        height: 45px;
      }
    }
    .user_name{
      font-size: 16px;
      display: inline-block;
      max-width: 120px;
      overflow: hidden;
      text-overflow:ellipsis;
      white-space: nowrap;
      vertical-align: middle;
    }
    .user_member{
      display: inline-block;
      transform: scale(.85);
      width: 70px;
      text-align: center;
      border: 1px solid #ffffff;
      border-radius: 10px;
      vertical-align: middle;
    }
    .user_code{
      margin-top: 5px;
      .user_code_icon{
        vertical-align: middle;
        margin-left: 5px;
      }
    }
    .user_header_icon{
      text-align: right;
      align-self: flex-start;
      padding: 15px 15px 0 0;
      font-size: 22px;
      .van-icon {
        font-size: 22px;
        margin-left: 10px;
      }
    }
  }
}
</style>
