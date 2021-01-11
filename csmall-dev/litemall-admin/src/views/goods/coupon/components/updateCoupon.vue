<template>
  <el-dialog title="编辑" :visible.sync="dialogVisible" :close-on-click-modal="false" top="1vh" @open="dialogOpen" @close="dialogClose">
    <el-form ref="couponFrom" :model="couponFrom" :rules="rules" status-icon label-position="left" label-width="130px" style="margin-left:35px;">
      <el-form-item label="优惠券名称" prop="name">
        <el-row>
          <el-col :span="14">
            <el-input v-model="couponFrom.name" disabled />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="减免金额" prop="discount">
        <el-row>
          <el-col :span="14">
            <el-input v-model.number="couponFrom.discount">
              <template slot="append">元</template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取消</el-button>
      <el-button type="primary" @click="updateData">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { couponUpdate } from '@/api/goods/coupon'

export default {
  name: 'UpdateCoupon',
  props: {
    data: {
      type: Object,
      default: null
    },
    value: Boolean
  },
  data() {
    return {
      dialogVisible: false,
      couponFrom: {
        name: '',
        discount: '',
        id: ''
      },
      rules: {
        discount: [
          { required: true, message: '减免金额不能为空' },
          { type: 'number', message: '减免金额必须为数字值' }
        ]
      }
    }
  },
  watch: {
    value(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    // 打开dialog
    dialogOpen() {
      this.couponFrom.name = this.data.name
      this.couponFrom.discount = this.data.discount
      this.couponFrom.id = this.data.id
    },
    // 关闭dialog
    dialogClose() {
      this.dialogVisible = false
      this.couponFrom = {
        name: '',
        discount: '',
        id: ''
      }
    },
    // 修改减免金额
    updateData() {
      this.$refs.couponFrom.validate(valie => {
        if (valie) {
          if (Number(this.couponFrom.discount) < 1000) {
            couponUpdate({ discount: this.couponFrom.discount, id: this.couponFrom.id }).then(res => {
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
              this.dialogClose()
              this.$emit('submit')
            }).catch(err => {
              this.$notify.error({
                title: '错误',
                message: err.data.errmsg
              })
            })
          } else {
            this.$message({
              message: '不能超过1000元',
              type: 'warning'
            })
          }
        }
      })
    }
  }
}
</script>
