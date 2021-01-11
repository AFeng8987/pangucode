<template>
  <el-dialog title="变更为加盟商" :visible.sync="dialogVisible" top="2vh" :close-on-click-modal="false" @open="dialogOpen" @close="dialogClose">
    <el-form ref="merchantsForm" :model="merchantsForm" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:30px;">
      <el-form-item label="地址" required>
        <address-select ref="memberAddress" v-model="memberAddress" :state="true" />
      </el-form-item>
      <el-form-item label="真实姓名" prop="allianceName">
        <el-row>
          <el-col :span="16">
            <el-input v-model.trim="merchantsForm.allianceName" clearable placeholder="请输入真实姓名" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="身份证号码" prop="allianceCardId">
        <el-row>
          <el-col :span="16">
            <el-input v-model="merchantsForm.allianceCardId" clearable placeholder="请输入身份证号码" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="身份证正面(信息面)" prop="IDPositive">
        <el-upload
          :headers="headers"
          :action="uploadPath"
          :show-file-list="false"
          :on-success="handleAvatarSuccessPositive"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
          accept=".jpg,.jpeg,.png,.gif"
        >
          <img v-if="merchantsForm.IDPositive" :src="merchantsForm.IDPositive" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon" />
        </el-upload>
      </el-form-item>
      <el-form-item label="身份证反面(国徽面)" prop="IDBack">
        <el-upload
          :headers="headers"
          :action="uploadPath"
          :show-file-list="false"
          :on-success="handleAvatarSuccessBack"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
          accept=".jpg,.jpeg,.png,.gif"
        >
          <img v-if="merchantsForm.IDBack" :src="merchantsForm.IDBack" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon" />
        </el-upload>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取消</el-button>
      <el-button type="primary" @click="handleChengaMemberAdd">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import lrz from 'lrz'
import { allianceAdd } from '@/api/user/member'
import AddressSelect from '@/components/AddressSelect'

export default {
  name: 'ChengaMerchants',
  components: { AddressSelect },
  props: {
    data: {
      type: Object,
      default: null
    },
    value: Boolean
  },
  data() {
    const validateIDCard = (rule, value, callback) => {
      const reg = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
      if (value.match(reg)) {
        callback()
      } else {
        callback(new Error('您输入的身份证号有误！'))
      }
    }
    return {
      uploadPath,
      dialogVisible: false,
      merchantsForm: {
        allianceName: undefined,
        allianceCardId: undefined,
        userId: undefined,
        IDPositive: undefined,
        IDBack: undefined
      },
      rules: {
        allianceName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }],
        allianceCardId: [
          { required: true, message: '身份证号码不允许为空', trigger: 'blur' },
          { validator: validateIDCard, trigger: 'blur' }
        ],
        IDPositive: [{ required: true, message: '身份证正面不能为空', trigger: 'blur' }],
        IDBack: [{ required: true, message: '身份证反面不能为空', trigger: 'blur' }]
      },
      memberAddress: {
        startProvice: null,
        startCity: null,
        startDistrict: null,
        startTown: null,
        startAddress: null
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
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
    // 打开dialog前
    dialogOpen() {
      this.resetmerchantsForm()
    },
    // 上传图片前超过2M压缩
    beforeAvatarUpload(file) {
      return new Promise((resolve, reject) => {
        const isLt2M = file.size / 1024 / 1024 < 2 // 判定图片大小是否小于2MB
        if (!isLt2M) {
          lrz(file, { quality: 0.8 }).then(res => {
            resolve(res.file)
          })
        } else {
          resolve(file)
        }
      })
    },
    // 上传图片成功之后
    handleAvatarSuccessPositive(res, file) {
      if (res.errno === 0) {
        this.merchantsForm.IDPositive = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
    handleAvatarSuccessBack(res, file) {
      if (res.errno === 0) {
        this.merchantsForm.IDBack = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
    resetmerchantsForm() {
      this.merchantsForm = {
        allianceName: undefined,
        allianceCardId: undefined,
        userId: this.data.id,
        IDPositive: undefined,
        IDBack: undefined
      }
    },
    // 关闭dialog后
    dialogClose() {
      this.$refs['merchantsForm'].resetFields()
      this.dialogVisible = false
      this.resetmerchantsForm()
    },
    // 确定新增
    handleChengaMemberAdd() {
      const reg = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
      this.$refs['merchantsForm'].validate(valid => {
        if (valid) {
          if (reg.test(this.merchantsForm.allianceCardId)) {
            const memberAddress = this.$refs['memberAddress'].validateForm()
            const data = this.assembleData()
            if (memberAddress) {
              allianceAdd(data).then(res => {
                this.$notify.success({
                  title: '成功',
                  message: '变更成功'
                })
                this.dialogClose()
                this.$emit('submit')
                this.$refs['memberAddress'].resetForm()
              }).catch(err => {
                this.$notify.error({
                  title: '失败',
                  message: err.data.errmsg
                })
              })
            }
          } else {
            this.$message({
              message: '您输入的身份证号有误，请重新输入',
              type: 'warning'
            })
          }
        }
      })
    },
    assembleData() {
      return {
        allianceCardId: this.merchantsForm.allianceCardId,
        allianceName: this.merchantsForm.allianceName,
        alliancePic: this.merchantsForm.IDPositive + ';' + this.merchantsForm.IDBack,
        userId: this.merchantsForm.userId,
        address: this.memberAddress.startProvice + '-' + this.memberAddress.startCity + '-' + this.memberAddress.startDistrict + '-' + this.memberAddress.startTown
      }
    }
  }
}
</script>

<style scoped>
.avatar-uploader /deep/ .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader /deep/ .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader /deep/ .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>
