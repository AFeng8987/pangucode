<template>
  <div class="user_information">
    <header-bar title="联系我们" left-arrow/>
    <div class="contact_box">
      <div class="box_item"><div class="font_box">QQ</div>
        <div class="mailbox"><span class="mailbox_span">{{ QQ }}</span> <van-icon :name="copys" class="copy-icon" @click="copyClick(QQ)" /></div>
      </div>
      <div class="box_item"><div class="font_box">微信</div>
        <div class="mailbox"><span class="mailbox_span">{{ Wx }}</span> <van-icon :name="copys" class="copy-icon" @click="copyClick(Wx)" /></div>
      </div>
      <div class="box_item"><div class="font_box">手机</div>
        <div class="mailbox"><span class="mailbox_span">{{ Phone }}</span> <van-icon :name="copys" class="copy-icon" @click="copyClick(Phone)"/></div>
      </div>
      <div class="box_item"><div class="font_box">邮箱</div>
        <div class="mailbox"><span class="mailbox_span">{{ Email }}</span> <van-icon :name="copys" class="copy-icon" @click="copyClick(Email)" /></div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import copys from '@/assets/images/copy-red.png'
import { aftersaleCustomer } from '@/api/user'

export default {
  name: 'AfterSales',
  components: { HeaderBar },
  data() {
    return {
      copys,
      Email: '',
      Phone: '',
      QQ: '',
      Wx: ''
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      aftersaleCustomer().then(res => {
        this.Email = res.data.data.Email
        this.Phone = res.data.data.Phone
        this.QQ = res.data.data.QQ
        this.Wx = res.data.data.Wx
      })
    },
    copyClick(msg) {
      const that = this
      cordova.plugins.clipboard.copy(msg)
      cordova.plugins.clipboard.paste(function(msg) { that.$toast('您已经复制成功啦') })
    }
  }
}
</script>

<style scoped>
.user_information {
  background: #ffffff;
}
.coupon_icons {
  color: #ffffff;
  font-size: 18px;
}
.contact_box{
  height: 200px;
  width: 90%;
  margin: 15vh auto;
}
.box_item {
  margin-bottom: 15px;
}
.font_box{
  display: inline-block;
  font-size: 18px;
  color: #909090;
  vertical-align: middle;
  width: 20%;
  text-align: center;
}
.mailbox{
  display: inline-block;
  height: 44px;
  width: 75%;
  line-height: 44px;
  padding-left: 20px;
  vertical-align: middle;
  font-size: 16px;
  background-color: #f2f2f2;
}
.mailbox_span{
  display: inline-block;
  vertical-align: middle;
  width: 85%;
}
.copy-icon{
  display: inline-block;
  vertical-align: middle;
}
.copy-icon img {
  font-size: 16px;
}
</style>
