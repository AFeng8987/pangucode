<template>
  <div class="information">
    <header-bar title="个人中心" left-arrow />
    <van-cell-group >
      <van-cell :center="true" title="个人头像">
        <template #default>
          <van-uploader :after-read="afterRead">
            <van-image :src="info.avatar || logo" fit="cover" class="information_cell_image" round>
              <template v-slot:error>加载失败</template>
            </van-image>
          </van-uploader>
        </template>
      </van-cell>
      <van-cell :value="info.nickName" title="昵称" is-link size="large" @click="isShow = true" />
      <van-cell :value="genderText" title="性别" is-link size="large" @click="sexShow = true" />
      <van-cell title="修改密码" is-link size="large" @click="$router.push({ path: '/user/modifyPassword', query: { mobile: info.mobile }})" />
      <van-cell title="关于我们" is-link size="large" @click="$router.push({ path: '/user/aboutUs'})"/>

    </van-cell-group>

    <van-button class="information_button" color="#F24444" block @click="onSelect">退出</van-button>

    <!-- 修改昵称 -->
    <van-dialog v-model="isShow" :before-close="beforeClose" title="亲爱的会员您好！" show-cancel-button>
      <div class="information_dialog">
        <van-field v-model="name" placeholder="请输入您想修改的名称，最多10个字符" />
      </div>
    </van-dialog>

    <!-- 修改性别 -->
    <van-action-sheet :round="false" v-model="sexShow" :actions="sexActions" cancel-text="取消" @select="onSexSelect" />
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Cell, CellGroup, Image, Uploader, ActionSheet, Dialog, Field } from 'vant'
import logo from '@/assets/images/login-logo.png'
import { authInfo, authProfile, authLogout } from '@/api/user'
import { imgUpLoad } from '@/api/upload'
import lrz from 'lrz'
import { removeLocalStorage } from '@/utils/local-storage'

export default {
  name: 'UserInformation',
  components: {
    HeaderBar,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Image.name]: Image,
    [Uploader.name]: Uploader,
    [ActionSheet.name]: ActionSheet,
    [Dialog.Component.name]: Dialog.Component,
    [Field.name]: Field
  },
  data() {
    return {
      logo,
      isShow: false,
      name: '',
      sexShow: false,
      sexActions: [{ name: '未知', type: 0 }, { name: '男', type: 1 }, { name: '女', type: 2 }],
      info: {}
    }
  },
  computed: {
    genderText() {
      const text = ['未知', '男', '女']
      return text[this.info.gender] || ''
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      authInfo().then(res => {
        this.info = res.data.data
      })
    },
    // 退出登录
    onSelect() {
      authLogout().then(res => {
        removeLocalStorage('Authorization')
        this.$router.replace({ name: 'login' })
      })
    },
    // 修改昵称
    beforeClose(action, done) {
      if (action === 'confirm') {
        if (this.name.length <= 10) {
          this.getAuthProfile({ nickname: this.name })
          done()
        } else {
          this.$toast({ message: '您输入的昵称超过10个汉字，请重新输入', position: 'top' })
          done(false)
        }
      } else {
        done()
      }
    },
    // 修改性别
    onSexSelect(item) {
      this.getAuthProfile({ gender: item.type })
      this.sexShow = false
    },
    // 上传
    afterRead(file) {
      if (file.file.size < 20971520) {
        lrz(file.file, {
          width: 1024,
          height: 768,
          quality: 0.9
        }).then(res => {
          imgUpLoad(res.formData).then(res => {
            this.getAuthProfile({ avatar: res.data.data.url })
          }).catch(() => {
            this.$toast('照片超出2M以上')
          })
        })
      } else if (file.file.size >= 20971520) {
        this.$toast('您上传的图片超过20M，请压缩后再次上传')
      }
    },
    // 调用修改用户信息接口
    getAuthProfile(row) {
      authProfile(row).then(res => {
        this.$toast({ message: '修改成功', position: 'top' })
        this.init()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.information{
  background-color: #f7f8fa;
  .information_button{
    border-radius: 10px;
    width: 90%;
    margin: 30px auto;
  }
  .information_header_icon{
    font-size: 24px;
  }
  .information_cell_image{
    width: 40px;
    height: 40px;
  }
  .exit{
    background-color: #F24444;
  }
  .information_dialog{
    width: 90%;
    margin: 15px auto;
    .van-cell{
      background-color: #F5F5F5;
      border-radius: 10px;
    }
  }
}
</style>
