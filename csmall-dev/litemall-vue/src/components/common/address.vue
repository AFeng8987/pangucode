<template>
  <van-popup v-model="show" :close-on-click-overlay="false" :safe-area-inset-bottom="true" position="bottom" @open="openPopup">
    <div class="user_popup_top">
      <van-row type="flex" justify="center" align="center">
        <van-col span="8" />
        <van-col span="8">
          <div class="user_popup_top_title">请选择所在地区</div>
        </van-col>
        <van-col span="8">
          <div class="user_popup_top_icon"><van-icon name="close" @click="closePopup" /></div>
        </van-col>
      </van-row>
      <div class="user_popup_address">
        <div v-for="(item, index) in addlist" :key="index" :style="{ 'color': actionIndex === index ? 'red':'' }" class="user_popup_name" @click="naemClick(index, item)" >{{ item.name }}</div>
      </div>

      <div class="user_popup_addressList">
        <div v-for="(item, index) in addlist[actionIndex].list" :key="index" class="list_name" @click="addClick(item)">{{ item.name }}</div>
      </div>
    </div>
  </van-popup>
</template>

<script>
import arerList from './pcas-code.json'

export default {
  props: {
    popup: {
      type: Boolean,
      default: false
    },
    region: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      show: false,
      addlist: [
        { list: arerList, code: '', name: '请选择' }
      ],
      actionIndex: 0
    }
  },
  watch: {
    popup(val) {
      this.show = val
    }
  },
  methods: {
    openPopup() {
      this.operationData()
    },
    closePopup() {
      this.show = false
      this.$emit('update:popup', false)
    },
    addClick(item) {
      this.addlist[this.actionIndex].code = item.code
      this.addlist[this.actionIndex].name = item.name
      this.addlist.splice(this.actionIndex + 1, 4)
      if (item.children) {
        this.addlist.push({ code: '', name: '请选择', list: item.children })
        this.actionIndex++
      } else {
        const region = this.addlist.map(res => {
          return res.name
        })
        this.closePopup()
        this.$emit('submit', { region })
      }
    },

    naemClick(index, item) {
      this.actionIndex = index
    },

    findAddr(list, index) {
      index = index || 0
      const current = list.find((item) => item.name === this.region[index])
      this.addlist.push({
        name: current.name,
        code: current.code,
        list: list
      })
      if (this.region[++index]) {
        this.findAddr(current.children, index)
      }
    },
    operationData() {
      if (this.region && this.region.length) {
        this.addlist = []
        this.findAddr(arerList)
        this.actionIndex = 3
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.user_popup_top{
    width: 90%;
    margin: 0 auto;
    margin-top: 15px;
    .user_popup_top_title{
      text-align: center;
      font-size: 16px;
    }
    .user_popup_top_icon{
      text-align: right;
      padding-right: 10px;
      i {
        font-size: 16px;
        padding-top: 3px;
        color: #BCBCBC;
      }
    }
    .user_popup_address{
      margin-top: 20px;
      font-size: 15px;
      .user_popup_name{
        text-align: center;
        display: inline-block;
        width: 25%;
        overflow: hidden;/*超出部分隐藏*/
        white-space: nowrap;/*不换行*/
        text-overflow:ellipsis;/*超出部分文字以...显示*/
      }
    }
    .user_popup_addressList{
      margin: 0 auto;
      margin-top: 20px;
      height: 350px;
      overflow-x: hidden;
      overflow-y: scroll;
      .list_name{
        font-size: 14px;
        height: 50px;
        line-height: 50px;
      }
    }
  }
</style>
