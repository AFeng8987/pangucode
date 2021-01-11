<template>
  <el-dialog title="新增规格" :visible.sync="dialogVisible" :close-on-click-modal="false" width="55%" top="5vh" @close="dialogClose">
    <el-card class="recommand">
      推荐规格: <el-tag v-for="spec in specs" :key="spec.spec" class="recommand_spec" @click="addSpec(spec)">{{ spec.spec }}</el-tag>
    </el-card>
    <el-form ref="specForm" :model="specForm" :rules="rules" status-icon label-position="left" label-width="70px">
      <el-row :gutter="24">
        <el-col :span="10">
          <el-form-item label="规格名" prop="spec">
            <el-input v-model="specForm.spec" clearable placeholder="请输入规格名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="规格值" prop="value">
            <el-input v-model="specForm.value" clearable placeholder="请输入规格值(多个规格值请用，隔开)" @change="inputChange" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row>
      <el-col :span="22">
        <el-card v-for="item in specData" :key="item.spec" style="margin-bottom: 10px">
          <div>
            <label>规格名：</label>
            <el-tag effect="dark" type="success">{{ item.spec }}</el-tag>
            <el-button style="float: right;" class="el-icon-delete" type="danger" size="mini" @click="handleDelete(item.spec)" />
          </div>
          <div style="margin-top: 20px;">
            <label>规格值：</label>
            <el-tag v-for="row in item.value" :key="row" style="margin-right: 5px" closable type="success" @close="handleClose(item.spec, row)">{{ row }}</el-tag>
            <el-input v-if="item.state" ref="saveTagInput" v-model="inputValue" class="input-new-tag" size="mini" @change="handleInputConfirm(item.spec)" />
            <el-button v-else type="primary" size="mini" @click="showInput(item.spec)">新增</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取消</el-button>
      <el-button type="primary" @click="handleCreate">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { createSpecs } from '@/api/stock/code'

export default {
  name: 'AddSpec',
  props: {
    value: Boolean,
    id: { type: Number, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      specForm: {
        spec: '',
        value: ''
      },
      rules: {
        spec: [{ required: true, message: '规格名不能为空', trigger: 'blur' }],
        value: [{ required: true, message: '规格值不能为空', trigger: 'blur' }]
      },
      specs: [
        { spec: '颜色', value: ['红色', '蓝色', '白色', '黑色'], state: false, sort: 100 },
        { spec: '尺码', value: ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'], state: false }
      ],
      specData: [],
      inputValue: ''
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
    // 确定
    handleCreate() {
      const spacValue = this.assembleList()
      createSpecs({ goodsCodeId: this.id, specs: spacValue }).then(res => {
        this.$message({
          type: 'success',
          message: '添加成功!'
        })
        this.dialogClose()
        this.$emit('submit', { spacValue: spacValue, products: res.data.data })
      }).catch(err => {
        this.$notify.error({
          title: '失败',
          message: err.data.errmsg
        })
      })
    },
    // 取消
    dialogClose() {
      this.dialogVisible = false
      this.specData = []
      this.resetSpecFrom()
      this.inputValue = ''
    },
    // Tag标签删除
    handleClose(spec, value) {
      this.specData.forEach(item => {
        if (item.spec === spec) {
          item.value.splice(item.value.indexOf(value), 1)
        }
      })
    },
    handleDelete(spec) {
      this.specData.forEach((item, index) => {
        if (item.spec === spec) {
          this.specData.splice(index, 1)
        }
      })
    },
    showInput(spec) {
      this.specData.forEach(item => {
        if (item.spec === spec) {
          item.state = true
        }
      })
    },
    handleInputConfirm(spec) {
      this.specData.forEach(item => {
        if (item.spec === spec) {
          if (item.value.indexOf(this.inputValue) !== -1) {
            this.$message({
              message: '您输入的规格值有重复的值，请重新输入',
              type: 'warning'
            })
            this.inputValue = ''
            return
          }
          item.value.push(this.inputValue)
          item.state = false
        }
      })
      this.inputValue = ''
    },
    inputChange() {
      this.$refs.specForm.validate(valid => {
        if (valid) {
          const value = this.changedouhao(this.specForm.value)
          if (value === false) {
            this.$message({
              message: '您输入的规格值有重复的值，请重新输入',
              type: 'warning'
            })
            this.specForm.value = ''
            return
          }
          const spec = {
            spec: this.specForm.spec,
            value: value,
            state: false
          }
          this.specData.push(spec)
          this.resetSpecFrom()
        }
      })
    },
    addSpec(spec) {
      const current = this.specData.find(item => item.spec === spec.spec)
      if (!current) {
        this.specData.push(spec)
      }
    },
    changedouhao(str) {
      str = str.replace(/，/ig, ',')
      const row = str.split(',')
      if ((new Set(row)).size !== row.length) {
        return false
      }
      return row
    },
    resetSpecFrom() {
      this.specForm = {
        spec: null,
        value: null
      }
    },
    assembleList() {
      const data = []
      const compare = (key) => {
        return (item1, item2) => {
          return (item2[key] || 0) - (item1[key] || 0)
        }
      }
      this.specData = this.specData.sort(compare('sort'))
      this.specData.forEach(item => {
        item.value.forEach(row => {
          const spec = {
            goodsCodeId: this.id,
            spec: item.spec,
            value: row
          }
          data.push(spec)
        })
      })
      return data
    }
  }
}
</script>

<style scoped lang="scss">
.input-new-tag {
  width: 60px;
  vertical-align: bottom;
}
.recommand {
  margin-bottom: 10px;
}
.recommand_spec {
  cursor: pointer;
  margin: 0 5px;
  &:hover {
    opacity: 0.8;
  }
}
</style>
