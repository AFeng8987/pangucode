<template>
  <el-dialog title="添加工厂和库存" :visible.sync="dialogAddVisible" @open="dialogOpen" @close="close">
    <el-card>
      <div slot="header" class="clearfix">
        <span>编码规格</span>
      </div>
      <el-tag v-for="item in data" :key="item.specsCodeId" size="medium" style="margin-right: 10px;margin-bottom:10px">{{ item.specsDesc +'/'+ item.goodsCode }}</el-tag>
    </el-card>

    <el-form ref="factoryForm" :model="factoryForm" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:50px;margin-top:30px">
      <el-form-item label="选择工厂" prop="factoryId">
        <el-row>
          <el-col :span="16">
            <el-select v-model="factoryForm.factoryId" clearable style="width: 100%;" :filterable="true" :filter-method="remoteMethod" placeholder="工厂名称">
              <el-option v-for="item in factoryNameData" :key="item.id" :label="item.plantName" :value="item.id" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="库存数量" prop="goodsStock">
        <el-row>
          <el-col :span="16">
            <el-input v-model.number="factoryForm.goodsStock" clearable placeholder="请输入库存数量">
              <template slot="append">件</template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="库存预警" prop="warnStock">
        <el-row>
          <el-col :span="16">
            <el-input v-model.number="factoryForm.warnStock" clearable placeholder="不输入默认50">
              <template slot="append">件</template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="成本价" prop="costPrice">
        <el-row>
          <el-col :span="16">
            <el-input v-model="factoryForm.costPrice" clearable placeholder="请输入成本价">
              <template slot="append">元</template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="进货价" prop="buyPrice">
        <el-row>
          <el-col :span="16">
            <el-input v-model="factoryForm.buyPrice" clearable placeholder="请输入进货价">
              <template slot="append">元</template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button type="primary" @click="handleFactoryAdd">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { plantList } from '@/api/user/factory'
import { addFactory } from '@/api/stock/adjust'

export default {
  name: 'CreateFactory',
  props: {
    value: Boolean,
    data: { type: Array, default: () => {
      return []
    } }
  },
  data() {
    const validateNum = (rule, value, callback) => {
      const reg = /^[1-9]\d*$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('您输入的有误'))
      }
    }
    const validatePrice = (rule, value, callback) => {
      const reg = /^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('您输入的金额有误'))
      }
    }
    return {
      dialogAddVisible: false,
      factoryForm: {
        costPrice: null,
        factoryId: null,
        // goodsCodeId: null,
        goodsStock: null,
        // specCodeId: null,
        warnStock: '50',
        buyPrice: null
      },
      rules: {
        factoryId: [{ required: true, message: '请选择工厂', trigger: 'change' }],
        goodsStock: [{ required: true, message: '库存数量不能为空', trigger: 'blur' }, { validator: validateNum, trigger: 'blur' }],
        warnStock: [{ required: true, message: '库存预警不能为空', trigger: 'blur' }, { validator: validateNum, trigger: 'blur' }],
        costPrice: [{ required: true, message: '成本价不能为空', trigger: 'blur' }, { validator: validatePrice, trigger: 'blur' }],
        buyPrice: [{ required: true, message: '进货价不能为空', trigger: 'blur' }, { validator: validatePrice, trigger: 'blur' }]
      },
      factoryNameData: []
    }
  },
  watch: {
    value(val) {
      this.dialogAddVisible = val
    },
    dialogAddVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    dialogOpen() {
      this.resetFactoryForm()
      const data = { limit: 20, page: 1 }
      plantList(data).then(res => {
        this.factoryNameData = res.data.data.list
      })
    },
    close() {
      this.resetFactoryForm()
      this.$refs['factoryForm'].resetFields()
      this.dialogAddVisible = false
    },
    remoteMethod(row) {
      const name = row || undefined
      const data = { name: name, limit: 20, page: 1 }
      plantList(data).then(res => {
        this.factoryNameData = res.data.data.list
      })
    },
    warnStockDefault() {
      if (Number(this.factoryForm.warnStock) < 1) {
        this.factoryForm.warnStock = '50'
        this.$refs['factoryForm'].clearValidate(['warnStock'])
      }
    },
    handleFactoryAdd() {
      this.warnStockDefault()
      this.$refs.factoryForm.validate(valid => {
        if (valid) {
          const data = this.assembleData()
          if (data) {
            addFactory(data).then(res => {
              this.$notify.success({
                title: '成功',
                message: '添加成功',
                duration: 1500
              })
              this.close()
            }).catch(err => {
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          }
        }
      })
    },
    resetFactoryForm() {
      this.factoryForm = {
        costPrice: null,
        factoryId: null,
        // goodsCodeId: null,
        goodsStock: null,
        // specCodeId: null,
        warnStock: '50',
        buyPrice: null
      }
    },
    assembleData() {
      if (parseInt(this.factoryForm.goodsStock) > 1000000) {
        this.$message({
          showClose: true,
          message: '您输入的库存数量不能超过1000000件',
          type: 'warning',
          duration: 1500
        })

        return false
      }

      if (parseInt(this.factoryForm.warnStock) > parseInt(this.factoryForm.goodsStock)) {
        this.$message({
          showClose: true,
          message: '库存预警不能大于库存数量',
          type: 'warning',
          duration: 1500
        })

        return false
      }

      if (parseInt(this.factoryForm.buyPrice * 100) > parseInt(this.factoryForm.costPrice * 100)) {
        this.$message({
          showClose: true,
          message: '进货价不能大于成本价',
          type: 'warning',
          duration: 1500
        })

        return false
      }

      this.factoryForm.goodsStock = Number(this.factoryForm.goodsStock)
      this.factoryForm.warnStock = Number(this.factoryForm.warnStock)

      const specCodeIds = []
      this.data.forEach(item => {
        specCodeIds.push(item.specsCodeId)
      })

      return { specCodeIds, productFactory: this.factoryForm }
    }
  }
}
</script>
