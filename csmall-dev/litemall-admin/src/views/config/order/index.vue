<template>
  <div class="app-container">
    <el-form ref="dataForm" :model="dataForm" status-icon label-width="300px">
      <el-form-item label="用户下单后超时" prop="litemall_order_unpaid">
        <el-input v-model="dataForm.litemall_order_unpaid" maxlength="2" class="input-width" :disabled="orderStaute">
          <template slot="append">分钟</template>
        </el-input>
        <span class="info">用户未付款，则订单自动取消</span>
      </el-form-item>
      <el-form-item label="订单发货后超期" prop="litemall_order_unconfirm">
        <el-input v-model="dataForm.litemall_order_unconfirm" maxlength="2" class="input-width" :disabled="orderStaute">
          <template slot="append"> 天</template>
        </el-input>
        <span class="info">未确认收货，则订单自动确认收货</span>
      </el-form-item>
      <el-form-item>
        <el-button v-show="orderStaute === true" type="primary" size="medium" style="width:100px;" @click="nextFright">修改</el-button>
        <el-button v-show="orderStaute === false" type="primary" size="medium" style="width:100px;" @click="handleSubmit">提交</el-button>
        <el-button v-show="orderStaute === false" size="medium" style="width:100px;" @click="cancleSure">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listOrder, updateOrder } from '@/api/config/order'

export default {
  name: 'ConfigOrder',
  data() {
    return {
      dataForm: {},
      orderStaute: true
    }
  },
  created() {
    this.getInit()
  },
  methods: {
    // 初始化
    getInit() {
      listOrder().then(res => {
        this.dataForm = res.data.data
      })
    },
    // 修改
    nextFright() {
      this.orderStaute = false
    },
    // 取消
    cancleSure() {
      this.orderStaute = true
      this.getInit()
    },
    // 提交
    handleSubmit() {
      const data = this.checkData()
      if (data) {
        this.$confirm('是否提交?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          updateOrder(this.dataForm).then(res => {
            this.$message({
              type: 'success',
              message: '一经修改，需重启服务才能生效'
            })
            this.cancleSure()
          }).catch(err => {
            this.$notify.error({
              title: '失败',
              message: err.data.errmsg
            })
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消操作'
          })
        })
      }
    },
    checkData() {
      if (parseInt(this.dataForm.litemall_order_unpaid) < 2 || parseInt(this.dataForm.litemall_order_unpaid) > 60) {
        this.$message({
          message: '用户下单超时时间不能大于60分钟并且小于2分钟',
          type: 'warning'
        })
        return false
      }

      if (parseInt(this.dataForm.litemall_order_unconfirm) < 2 || parseInt(this.dataForm.litemall_order_unconfirm) > 30) {
        this.$message({
          message: '订单发货超期时间不能大于30天并且不能小于2天',
          type: 'warning'
        })
        return false
      }

      return {
        litemall_order_unpaid: Number(this.dataForm.litemall_order_unpaid),
        litemall_order_unconfirm: Number(this.dataForm.litemall_order_unconfirm)
      }
    }
  }
}
</script>

<style scoped>
.input-width {
  width: 50%;
}
.info {
  margin-left: 15px;
}
</style>
