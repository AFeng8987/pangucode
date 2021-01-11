<template>
  <div class="address">
    <header-bar title="收货地址" left-arrow>
      <template #right>
        <span class="address_add" @click="onClickRight">添加新地址</span>
      </template>
    </header-bar>
    <div v-if="isAddress" class="address_empty" @click="onClickRight">
      <van-icon class="address_empty_icon" name="add-o" color="#FEBF00" />
      <p class="address_empty_font">添加您的第一个地址</p>
    </div>
    <div v-else>
      <div v-for="item in list" :key="item.id" class="address_yes">
        <van-row>
          <van-col :span="5">
            <div class="address_yes_tou">
              <div class="address_yes_tou_name">{{ item.name.substring(0,1) }}</div>
            </div>
          </van-col>
          <van-col :span="15">
            <div class="address_information" @click="selectedAddress(item)">
              <div class="address_information_box">
                <span class="address_information_box_name">{{ item.name }}</span>
                <span class="address_information_box_phone">{{ item.tel }}</span>
              </div>
              <div class="address_information_address">{{ item.province + item.city + item.county + item.street + item.addressDetail }}</div>
            </div>
          </van-col>
          <van-col :span="4">
            <div class="address_edit" @click="handleUpdate(item)"><span>编辑</span></div>
            <div v-if="item.isDefault" class="address_default" >「默认」</div>
          </van-col>
        </van-row>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'
import { Col, Row } from 'vant'
import { addressList } from '@/api/user'

export default {
  name: 'UserAddressList',
  components: {
    HeaderBar,
    [Col.name]: Col,
    [Row.name]: Row
  },
  data() {
    return {
      isAddress: true,
      list: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      addressList().then(res => {
        console.log(res)
        if (res.data.data.list.length === 0) {
          this.isAddress = true
        } else {
          this.isAddress = false
          this.list = res.data.data.list
        }
      })
    },
    // 添加新地址
    onClickRight() {
      this.$router.push({ path: '/user/address', query: { type: 'add' }})
    },
    // 编辑
    handleUpdate(item) {
      this.$router.push({ path: '/user/address', query: { type: 'update', id: item.id }})
    },
    // 选中地址
    selectedAddress(row) {
      if (this.$route.query.type) {
        this.$store.dispatch('GetAddress', row)
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.address{
  background-color: #f7f8fa;
  .address_add{
    font-size: 14px;
  }
  .address_empty{
    width: 95%;
    margin: 10px auto;
    border-radius: 10px;
    background-color: #ffffff;
    text-align: center;
    .address_empty_icon{
      font-size: 25px;
      padding-top: 20px;
    }
    .address_empty_font{
      color: #BCBCBC;
      font-size: 14px;
      padding-bottom: 20px;
    }
  }
  .address_yes{
    width: 95%;
    margin: 10px auto;
    border-radius: 10px;
    background-color: #ffffff;
    .address_yes_tou{
      width: 100%;
      height: 100px;
      position: relative;
      .address_yes_tou_name{
        width: 40px;
        height: 40px;
        background-color: #FEC000;
        border-radius: 50%;
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        margin: auto;
        text-align: center;
        line-height: 40px;
        font-size: 18px;
        color: #ffffff;
      }
    }
    .address_information{
      width: 100%;
      margin-top: 15px;
      .address_information_box{
        .address_information_box_name{
          font-size: 14px;
          color: #000000;
          vertical-align: middle;
          font-weight: bold;
        }
        .address_information_box_phone{
          color: #BCBCBC;
          font-size: 13px;
          vertical-align: middle;
          margin-left: 20px;
        }
      }
      .address_information_address{
        margin-top: 10px;
        color: #343434;
        font-size: 13px;
        height: 38px;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
    .address_edit{
      width: 100%;
      height: 100px;
      line-height: 100px;
      text-align: center;
      font-size: 14px;
      color: #999999;
    }
    .address_default{
      color: #F24546;
      text-align: center;
      margin-top: -25px;
    }
  }
}
</style>
