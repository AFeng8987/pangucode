<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>配置信息</span>
      </div>
      <el-radio-group v-model="radioFright">
        <el-row>
          <el-col :span="12">
            <el-radio :disabled="freeShipping" :label="1"><span>全场包邮</span></el-radio>
          </el-col>
        </el-row>
        <!--<el-row>
          <el-col>
            <el-radio :disabled="noFreeShipping" :label="2" style="margin-top: 30px">
              满 <el-input v-model="dataForm.litemall_express_freight_min" type="number" :disabled="controInput" style="width:30%;" placeholder="1000" /> 元包邮，不满包邮金额补
              <el-input v-model="dataForm.litemall_express_freight_value" type="number" :disabled="controInput" style="width:30%;" placeholder="100" /> 元邮费
            </el-radio>
          </el-col>
        </el-row>-->
      </el-radio-group>
      <el-row>
        <el-col :span="12">
          <div style="text-align: center;margin-top: 30px;display:none">
            <el-button v-show="controlButton === false" type="primary" size="medium" style="width:200px;" @click="nextFright">修改</el-button>
            <el-button v-show="controlButton === true" type="primary" size="medium" style="width:100px;" @click="handleSubmit">提交</el-button>
            <el-button v-show="controlButton === true" size="medium" style="width:100px;" @click="cancleSure">取消</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { configExpress, configExpressSubmit } from '@/api/config/postage'

export default {
  name: 'Postage',
  data() {
    return {
      radioFright: '',
      dataForm: {
        litemall_express_freight_min: '0',
        litemall_express_freight_value: '0'
      },
      freeShipping: false,
      noFreeShipping: false,
      controlButton: false,
      controInput: true
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      configExpress().then(res => {
        this.dataForm = res.data.data
        if (Number(res.data.data.litemall_express_freight_min) === 0 && Number(res.data.data.litemall_express_freight_value) === 0) {
          this.radioFright = 1
          this.freeShipping = false
          this.noFreeShipping = true
        } else {
          this.radioFright = 2
          this.freeShipping = true
          this.noFreeShipping = false
        }
      })
    },
    nextFright() {
      this.freeShipping = false
      this.noFreeShipping = false
      this.controlButton = true
      this.controInput = false
    },
    handleSubmit() {
      this.$confirm('是否提交?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.radioFright === 1) {
          this.restaFrom()
        } else {
          if (Number(this.dataForm.litemall_express_freight_min) <= 0 && Number(this.dataForm.litemall_express_freight_value) <= 0) {
            this.$message({
              message: '您不能输入小于等于0一下的数字',
              type: 'warning'
            })
            return
          }
        }

        configExpressSubmit(this.dataForm).then(res => {
          this.$message({
            type: 'success',
            message: '提交成功!'
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
    },
    cancleSure() {
      this.controlButton = false
      this.controInput = true
      this.getData()
    },
    restaFrom() {
      this.dataForm = {
        litemall_express_freight_min: 0,
        litemall_express_freight_value: 0
      }
    }
  }
}
</script>
