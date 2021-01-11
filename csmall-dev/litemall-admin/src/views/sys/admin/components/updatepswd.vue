<template>
  <el-dialog title="修改密码" :visible.sync="dialogPswVisible" @open="dialogOpen" @close="close">
    <el-form ref="pswForm" :model="pswForm" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:50px;">
      <el-form-item label="用户名">
        <el-row>
          <el-col :span="16">
            <div>{{ pswForm.username }}</div>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-row>
          <el-col :span="16">
            <el-input v-model.trim="pswForm.password" show-password clearable placeholder="请输入密码" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="确认密码" prop="password">
        <el-row>
          <el-col :span="16">
            <el-input v-model.trim="pswForm.confirmPassword" show-password clearable placeholder="请确认密码" />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button type="primary" @click="handleUpdate">修改</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { updatePswd } from '@/api/admin'

export default {
  props: {
    value: Boolean,
    data: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      dialogPswVisible: false,
      pswForm: {
        id: undefined,
        username: undefined,
        password: undefined,
        confirmPassword: undefined
      },
      rules: {
        password: [{ required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }]
      }
    }
  },
  watch: {
    value(val) {
      this.dialogPswVisible = val
    },
    dialogPswVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    dialogOpen() {
      this.pswForm.id = this.data.id
      this.pswForm.username = this.data.username
    },
    close() {
      this.dialogPswVisible = false
      this.pswForm.id = undefined
      this.pswForm.username = undefined
      this.pswForm.password = undefined
      this.pswForm.confirmPassword = undefined
      this.$refs['pswForm'].resetFields()
    },
    handleUpdate() {
      this.$refs['pswForm'].validate((valid) => {
        if (valid) {
          const data = this.checkData()
          if (data) {
            updatePswd(data).then(res => {
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
              this.close()
            }).catch(err => {
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg
              })
            })
          }
        }
      })
    },
    checkData() {
      if (this.pswForm.password !== this.pswForm.confirmPassword) {
        this.$message({
          message: '您两次输入的密码不一致，请重新输入',
          type: 'warning'
        })

        return false
      }

      return {
        adminId: this.pswForm.id,
        username: this.pswForm.username,
        password: this.$md5(this.pswForm.password)
      }
    }
  }
}
</script>
