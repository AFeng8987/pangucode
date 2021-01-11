<template>
  <div class="join">
    <header-bar title="加入加盟商" left-arrow />

    <div class="join_box">
      <!-- <div class="join_box_conditions">
        <div class="join_bar"/>
        <div class="join_name">金主用户：</div>
        <div class="join_font">1、注册条件：下载注册财神商城APP免费注册即可</div>
        <div class="join_font">2、享有自购、推广产品佣金补贴</div>
        <div class="join_font">3、直推服务中心奖励</div>
        <div class="join_font">4、享受直推用户佣金收益</div>
      </div>
      <div class="join_box_conditions">
        <div class="join_bar"/>
        <div class="join_name">富翁用户：</div>
        <div class="join_font">1、注册条件：下载注册财神商城APP，一次性消费一定金额。</div>
        <div class="join_font">2、享有自购、推广产品佣金补贴</div>
        <div class="join_font">3、直推服务中心奖励</div>
        <div class="join_font">4、享受直推用户的佣金收益</div>
        <div class="join_font">5、享受业绩奖励、享受团队管理奖励</div>
      </div> -->
      <div class="join_box_conditions">
        <div class="join_bar"/>
        <div class="join_name">财神服务中心：</div>
        <div class="join_font">1、注册条件：①交纳货品保证金 &nbsp;&nbsp; ②设立有限责任公司或个体工商户</div>
        <div class="join_font">2、享有自购、推广产品佣金补贴</div>
        <div class="join_font">3、直推服务中心奖励</div>
        <div class="join_font">4、享受区域用户的佣金收益</div>
        <div class="join_font">5、享受业绩和团队管理奖励、享受服务中心分红奖</div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '@/components/HeaderNavBar/index'

export default {
  name: 'UserMerchants',
  components: {
    HeaderBar
  }
}
</script>

<style lang="scss" scoped>
.join{
  background-color: #f7f8fa;
  .join_box{
    width: 95%;
    margin: 30px auto;
    .join_box_conditions{
      width: 95%;
      margin: auto;
      margin-bottom: 20px;
      .join_bar{
        background-color: red;
        width: 5px;
        height: 18px;
        display: inline-block;
        vertical-align: middle;
      }
      .join_name{
        display: inline-block;
        font-size: 16px;
        vertical-align: middle;
        margin-left: 5px;
      }
      .join_font{
        margin-top: 10px;
        font-size: 13px;
      }
    }
  }
}
</style>
