<template>
  <el-dialog :title="textMap[dialogStatus]" width="60%" top="5vh" :visible.sync="dialogVisible" @open="dialogOpen" @close="dialogClose">
    <el-form ref="informationForm" :model="informationForm" :rules="rules" status-icon label-position="left" label-width="100px" style="margin-left:50px;">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="工厂地址" required>
            <address-select ref="factory" v-model.trim="factoryAddress" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工厂名称" prop="plantName">
            <el-input v-model.trim="informationForm.plantName" placeholder="请输入工厂名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工厂联系人" prop="plantContacts">
            <el-input v-model.trim="informationForm.plantContacts" placeholder="请输入工厂联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工厂电话" prop="plantPhone">
            <el-input v-model.trim="informationForm.plantPhone" placeholder="请输入工厂电话" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="发货地址" required>
            <address-select ref="sendAddr" v-model.trim="goodsAddress" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发货联系人" prop="sendContacts">
            <el-input v-model.trim="informationForm.sendContacts" placeholder="请输入发货联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发货电话" prop="sendPhone">
            <el-input v-model.trim="informationForm.sendPhone" placeholder="请输入发货电话" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="收货地址" required>
            <address-select ref="receive" v-model.trim="receiveAddress" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收货联系人" prop="receiveContacts">
            <el-input v-model.trim="informationForm.receiveContacts" placeholder="请输入收货联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收货电话" prop="receivePhone">
            <el-input v-model.trim="informationForm.receivePhone" placeholder="请输入收货电话" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button v-if="dialogStatus === 'create'" type="primary" @click="handleCreate">确定</el-button>
      <el-button v-else type="primary" @click="handleUpdate">修改</el-button>
    </div>
  </el-dialog>
</template>

<script>
import AddressSelect from '@/components/AddressSelect'
import { plantAdd, plantUpdate } from '@/api/user/factory'

export default {
  name: 'FactoryOperation',
  components: { AddressSelect },
  props: {
    data: {
      type: Object,
      default: null
    },
    value: Boolean,
    state: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑工厂',
        create: '新增工厂'
      },
      informationForm: {
        plantAddress: null,
        plantName: null,
        plantContacts: null,
        plantPhone: null,
        sendAddress: null,
        sendContacts: null,
        sendPhone: null,
        receiveAddress: null,
        receiveContacts: null,
        receivePhone: null
      },
      factoryAddress: {
        startProvice: null,
        startCity: null,
        startDistrict: null,
        startTown: null,
        startAddress: null
      },
      goodsAddress: {
        startProvice: null,
        startCity: null,
        startDistrict: null,
        startTown: null,
        startAddress: null
      },
      receiveAddress: {
        startProvice: null,
        startCity: null,
        startDistrict: null,
        startTown: null,
        startAddress: null
      },
      rules: {
        plantName: [
          { required: true, message: '工厂名称不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        plantContacts: [{ required: true, message: '工厂联系人不能为空', trigger: 'blur' },
          { max: 20, message: '20 个字符以内', trigger: 'blur' }
        ],
        plantPhone: [{ required: true, message: '工厂电话不能为空', trigger: 'blur' }],
        sendContacts: [{ required: true, message: '发货联系人不能为空', trigger: 'blur' },
          { max: 20, message: '20 个字符以内', trigger: 'blur' }
        ],
        sendPhone: [{ required: true, message: '发货电话不能为空', trigger: 'blur' }],
        receiveContacts: [{ required: true, message: '收货联系人不能为空', trigger: 'blur' },
          { max: 20, message: '20 个字符以内', trigger: 'blur' }
        ],
        receivePhone: [{ required: true, message: '收货电话不能为空', trigger: 'blur' }]
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
    dialogOpen() {
      this.dialogStatus = this.state
      if (this.dialogStatus !== 'create') {
        this.$nextTick(function() {
          this.getAddress()
        })
      }
    },
    dialogClose() {
      this.dialogVisible = false
      this.$refs['informationForm'].resetFields()
      this.$refs['factory'].resetForm()
      this.$refs['sendAddr'].resetForm()
      this.$refs['receive'].resetForm()
    },
    close() {
      this.dialogClose()
    },
    getAddress() {
      const factoryAddress = this.data.plantAddress.split('-')
      const address = this.data.sendAddress.split('-')
      const receiveAddress = this.data.receiveAddress.split('-')
      this.factoryAddress = {
        startProvice: factoryAddress[0],
        startCity: factoryAddress[1],
        startDistrict: factoryAddress[2],
        startTown: factoryAddress[3],
        startAddress: factoryAddress[4]
      }
      this.goodsAddress = {
        startProvice: address[0],
        startCity: address[1],
        startDistrict: address[2],
        startTown: address[3],
        startAddress: address[4]
      }
      this.receiveAddress = {
        startProvice: receiveAddress[0],
        startCity: receiveAddress[1],
        startDistrict: receiveAddress[2],
        startTown: receiveAddress[3],
        startAddress: receiveAddress[4]
      }
      this.informationForm = {
        plantAddress: this.data.plantAddress,
        plantName: this.data.plantName,
        plantContacts: this.data.plantContacts,
        plantPhone: this.data.plantPhone,
        sendAddress: this.data.sendAddress,
        sendContacts: this.data.sendContacts,
        sendPhone: this.data.sendPhone,
        receiveAddress: this.data.receiveAddress,
        receiveContacts: this.data.receiveContacts,
        receivePhone: this.data.receivePhone,
        id: this.data.id
      }
    },
    handleCreate() {
      const flagFactory = this.$refs['factory'].validateForm()
      const sendAddr = this.$refs['sendAddr'].validateForm()
      const recieveAddr = this.$refs['receive'].validateForm()
      this.$refs['informationForm'].validate(valid => {
        if (valid) {
          if (flagFactory && sendAddr && recieveAddr) {
            this.assembleData()
            plantAdd(this.informationForm).then(res => {
              this.$notify.success({
                title: '成功',
                message: '添加成功'
              })
              this.dialogVisible = false
              this.$refs['informationForm'].resetFields()
              this.$refs['factory'].resetForm()
              this.$refs['sendAddr'].resetForm()
              this.$emit('submit')
            }).catch(err => {
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg
              })
            })
          } else {
            this.$message({
              message: '您的地址信息还未输入完整，请继续输入完整',
              type: 'warning'
            })
          }
        }
      })
    },
    handleUpdate() {
      const flagFactory = this.$refs['factory'].validateForm()
      const sendAddr = this.$refs['sendAddr'].validateForm()
      const recieveAddr = this.$refs['receive'].validateForm()
      this.$refs['informationForm'].validate(valid => {
        if (valid) {
          if (flagFactory && sendAddr && recieveAddr) {
            this.assembleData()
            plantUpdate(this.informationForm).then(res => {
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
              this.dialogVisible = false
              this.$refs['informationForm'].resetFields()
              this.$refs['factory'].resetForm()
              this.$refs['sendAddr'].resetForm()
              this.$emit('submit')
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
    assembleData() {
      const factoryAddressData = this.factoryAddress.startProvice + '-' + this.factoryAddress.startCity + '-' + this.factoryAddress.startDistrict + '-' + this.factoryAddress.startTown + '-' + this.factoryAddress.startAddress
      const addressData = this.goodsAddress.startProvice + '-' + this.goodsAddress.startCity + '-' + this.goodsAddress.startDistrict + '-' + this.goodsAddress.startTown + '-' + this.goodsAddress.startAddress
      const receiveAddress = this.receiveAddress.startProvice + '-' + this.receiveAddress.startCity + '-' + this.receiveAddress.startDistrict + '-' + this.receiveAddress.startTown + '-' + this.receiveAddress.startAddress
      this.informationForm.plantAddress = factoryAddressData
      this.informationForm.sendAddress = addressData
      this.informationForm.receiveAddress = receiveAddress
    }
  }
}
</script>
