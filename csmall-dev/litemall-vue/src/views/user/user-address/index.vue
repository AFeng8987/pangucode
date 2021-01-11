<template>
  <div class="userAddress">
    <header-bar :title="textStatus[textMsg]" left-arrow>
      <template #right>
        <span v-if="textMsg === 'update'" class="userAddress_span" @click="deleteClick">删除</span>
      </template>
    </header-bar>

    <div class="userAddress_input">
      <van-form ref="form" @submit="save">
        <van-field v-model.trim="name" :rules="[{required: true, message: '收货人不能为空'}]" required clearable placeholder="请填写收货人" minlength="2" maxlength="20" />
        <van-field v-model.trim="tel" :rules="[{ pattern: phoneReg, message: '手机号码格式不正确'}]" required type="tel" clearable placeholder="请填写手机号码">
          <template #right-icon>
            <span>+86</span>
          </template>
        </van-field>

        <van-field v-model="regionLabel" :rules="[{required: true, message: '所在地区不能为空'}]" required readonly clickable placeholder="请选择所在地区" @click="selectClick">
          <template #right-icon>
            <van-icon name="arrow" />
          </template>
        </van-field>

        <van-field v-model.trim="addressDetail" :rules="[{required: true, message: '详细地址不能为空'}]" required clearable placeholder="详细地址" maxlength="100" />
        <van-field v-model.trim="postalCode" :rules="[{ pattern: postCodeReg, message: '请输入正确邮政编码,例: 586555' }]" clearable placeholder="请填写邮政编码" />

        <van-cell title="设为默认地址" class="userAddress_cell">
          <template #right-icon>
            <van-checkbox v-model="isDefault" shape="square" icon-size=".5rem" />
          </template>
        </van-cell>

        <div class="save_btn"><van-button type="primary" native-type="submit" block round>保存收货地址</van-button></div>

        <!-- 省市区街道选择 -->
        <address-choose :popup.sync="config.show" :region="config.data" @submit="getData" />
      </van-form>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import HeaderBar from '@/components/HeaderNavBar/index'
import { Field, Cell, Checkbox, Popup, Area, Dialog, Picker, Col, Row, Form } from 'vant'
import { addressSave, addressDetail, addressDelete } from '@/api/user'

import positioning from '@/assets/images/addresspo-sitioning.png'

import addressChoose from '@/components/common/address'

Vue.use(Form).use(Field).use(Cell).use(Checkbox).use(Popup).use(Area).use(Dialog).use(Picker).use(Col).use(Row)

export default {
  name: 'UserAddress',
  components: {
    HeaderBar,
    addressChoose
  },
  data() {
    return {
      id: '',
      name: '',
      tel: '',
      region: [],
      addressDetail: '',
      areaCode: '',
      postalCode: '',
      isDefault: true,
      textMsg: '',
      positioning,
      textStatus: {
        add: '添加收货地址',
        update: '编辑收货地址'
      },
      postCodeReg: /^($|\d{6})$/,
      phoneReg: /^1\d{10}$/,
      config: {
        show: false,
        data: null
      }
    }
  },
  computed: {
    regionLabel() {
      return this.region.join('/')
    }
  },
  created() {
    this.textMsg = this.$route.query.type
    this.id = this.$route.query.id
    if (this.textMsg === 'update') {
      this.getDetailsData()
    }
  },
  methods: {
    // 获取详情数据
    getDetailsData() {
      addressDetail({ id: this.id }).then(res => {
        const data = res.data.data
        this.name = data.name
        this.tel = data.tel
        this.region = [data.province, data.city, data.county, data.street]
        this.addressDetail = data.addressDetail
        this.areaCode = data.areaCode
        this.postalCode = data.postalCode
        this.isDefault = data.isDefault
      })
    },
    // 保存
    save() {
      this.$toast.loading({
        message: '加载中...',
        forbidClick: true,
        duration: 0
      })
      const data = this.getSaveData()
      console.log(data)
      if (this.$refs['form'].validate()) {
        addressSave(data).then(res => {
          this.$router.go(-1)
          this.$toast.clear()
        }).catch(err => {
          this.$toast(err.data.errmsg)
        })
      }
    },
    getSaveData() {
      const name = this.name
      const tel = this.tel
      const province = this.region[0]
      const city = this.region[1]
      const county = this.region[2]
      const street = this.region[3]
      const addressDetail = this.addressDetail
      const areaCode = this.areaCode
      const postalCode = this.postalCode
      const isDefault = this.isDefault
      const id = this.id ? this.id : undefined

      return {
        id: id,
        name: name,
        tel: tel,
        province: province,
        city: city,
        county: county,
        street: street,
        addressDetail: addressDetail,
        areaCode: areaCode,
        postalCode: postalCode,
        isDefault: isDefault
      }
    },
    // 删除
    deleteClick() {
      Dialog.confirm({
        message: '确定需要删除该地址吗?',
        beforeClose: this.beforeClose
      }).catch(() => {})
    },
    beforeClose(action, done) {
      if (action === 'confirm') {
        addressDelete({ id: this.id }).then(res => {
          this.$router.go(-1)
          done()
        }).catch(err => {
          this.$toast(err.data.errmsg)
        })
      } else {
        done()
      }
    },
    // 选择地址
    selectClick() {
      this.config.show = true
      if (this.textMsg === 'update') {
        this.config.data = this.region
      }
    },

    // 返回选择的地址
    getData(data) {
      this.region = data.region
    }
  }
}
</script>

<style lang="scss" scoped>
.userAddress{
  background-color: #f7f8fa;
  .userAddress_add{
    font-size: 14px;
    color: #F24546;
  }
  .userAddress_input{
    .userAddress_cell{
      margin-top: 10px;
    }
  }
}
.save_btn {
  margin: 20px 16px;
}
.userAddress_span{
  color: #F24546;
}
</style>
