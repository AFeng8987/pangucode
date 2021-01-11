<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>热卖配置</span>
      </div>
      <el-form ref="sellingFrom" :model="sellingFrom" :rules="rules" label-position="left" label-width="200px" class="demo-ruleForm">
        <el-row>
          <el-col :span="8">
            <el-form-item label="首页热卖商品展示数量" prop="litemall_wx_index_hot">
              <el-input v-model.trim="sellingFrom.litemall_wx_index_hot" :disabled="configAction" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-row>
        <el-col :span="8">
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
import { hotNumber, hotNumberSubmit } from '@/api/config/selling'

export default {
  name: 'Selling',
  data() {
    const number = (rule, value, callback) => {
      const reg = /^[1-9]\d*$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('请输入正整数'))
      }
    }
    return {
      sellingFrom: {
        litemall_wx_index_hot: ''
      },
      rules: {
        litemall_wx_index_hot: [{ required: true, message: '不能为空', trigger: 'blur' }, { validator: number, trigger: 'blur' }]
      },
      configAction: true
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      hotNumber().then(res => {
        this.sellingFrom = res.data.data
      })
    },
    // 取消
    cancleSure() {
      this.configAction = true
      this.$refs['sellingFrom'].resetFields()
      this.init()
    },
    // 提交
    handleSubmit() {
      this.$refs['sellingFrom'].validate(valie => {
        if (valie) {
          this.$confirm('是否提交?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            hotNumberSubmit(this.sellingFrom).then(res => {
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
