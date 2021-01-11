<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>配置信息</span>
      </div>
      <el-form ref="configForm" :model="configForm" :rules="rules" status-icon label-position="left" label-width="150px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客服联系QQ" prop="litemall_customer_qq">
              <el-input v-model.trim="configForm.litemall_customer_qq" autocomplete="on" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客服联系微信" prop="litemall_customer_wx">
              <el-input v-model.trim="configForm.litemall_customer_wx" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客服联系电话" prop="litemall_customer_phone">
              <el-input v-model="configForm.litemall_customer_phone" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="客服联系邮箱" prop="litemall_customer_email">
              <el-input v-model.trim="configForm.litemall_customer_email" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="configAction === true">
          <el-col :span="12">
            <el-form-item label="客服联系拉起QQ">
              <el-input v-model.trim="configForm.litemall_customer_qq" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-row>
        <el-col :span="12">
          <div style="text-align: center">
            <el-button v-show="configAction === true" type="primary" @click="configAction = false">修改</el-button>
            <el-button v-show="configAction === false" type="primary" @click="handleSubmit">提交</el-button>
            <el-button v-show="configAction === false" @click="cancleSure">取消</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { configCustomer, configCustomerSubmit } from '@/api/config/client'

export default {
  name: 'Client',
  data() {
    const validateWx = (rule, value, callback) => {
      const reg = /^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$/
      if (value.match(reg)) {
        callback()
      } else {
        callback(new Error('您输入的微信号有误！'))
      }
    }
    return {
      configForm: {
        litemall_customer_phone: null,
        litemall_customer_qq: null,
        litemall_customer_wx: null
      },
      rules: {
        litemall_customer_qq: [{ required: true, message: '客服联系QQ不能为空', trigger: 'blur' }, { min: 5, max: 12, message: '长度在 5 到 12 个字符', trigger: 'blur' }],
        litemall_customer_wx: [{ required: true, message: '客服联系微信不能为空', trigger: 'blur' }, { validator: validateWx, trigger: 'blur' }],
        litemall_customer_phone: [{ required: true, message: '客服联系电话不能为空', trigger: 'blur' }],
        litemall_customer_email: [{ required: true, message: '客服联系邮箱不能为空', trigger: 'blur' }]
      },
      configAction: true
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      configCustomer().then(res => {
        this.configForm = res.data.data
      }).catch(err => {
        console.log(err)
      })
    },
    cancleSure() {
      this.configAction = true
      this.$refs['configForm'].resetFields()
      this.getData()
    },
    handleSubmit() {
      this.$refs['configForm'].validate(valie => {
        if (valie) {
          this.$confirm('是否提交?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            configCustomerSubmit(this.configForm).then(res => {
              this.$message({
                type: 'success',
                message: '修改成功!'
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
      })
    }
  }
}
</script>
