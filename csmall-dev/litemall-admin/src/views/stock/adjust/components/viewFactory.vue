<template>
  <el-dialog :title="specificationNumber" :visible.sync="dialogSeeVisible" top="5vh" width="65%" :close-on-click-modal="false" @open="dialogOpen">
    <el-card>
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>
      <el-table :data="factoryData" border>
        <el-table-column align="center" label="工厂名称" property="plantName" />
        <el-table-column align="center" label="库存数量(件)" property="goodsStock" />
        <el-table-column align="center" label="库存预警(件)" property="warnStock" />
        <el-table-column align="center" label="成本价(元)" property="costPrice" />
        <el-table-column align="center" label="进货价(元)" property="buyPrice" />
        <el-table-column label="操作" align="center" width="300">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/stock/updateStock']" type="warning" size="mini" @click="adjustClick(scope.row, 'subtract')">减库存</el-button>
            <el-button v-permission="['POST /admin/stock/updateStock']" type="primary" size="mini" @click="adjustClick(scope.row, 'add')">加库存</el-button>
            <el-button v-permission="['POST /admin/stock/delFactory']" type="danger" size="mini" @click="handleFactoryDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </el-dialog>
</template>

<script>
import { listFactory, delectFactory, updateStock } from '@/api/stock/adjust'

export default {
  name: 'ViewFactory',
  props: {
    value: Boolean,
    data: { type: Object, default: null }
  },
  data() {
    return {
      dialogSeeVisible: false,
      specificationNumber: '',
      factoryData: []
    }
  },
  watch: {
    value(val) {
      this.dialogSeeVisible = val
    },
    dialogSeeVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    dialogOpen() {
      this.specificationNumber = '查看已有工厂与库存（规格编号：' + this.data.specsCode + '）'
      this.getList()
    },
    getList() {
      listFactory({ specsCodeId: this.data.specsCodeId }).then(res => {
        this.factoryData = res.data.data
      })
    },
    // 删除工厂
    handleFactoryDelete(row) {
      this.$confirm('此操作将删除该工厂, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delectFactory(row.id).then(res => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        }).catch(err => {
          this.$notify.error({
            title: '失败',
            message: err.data.errmsg
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 加减库存
    adjustClick(row, type) {
      this.$prompt(this.getStock(type), '工厂名称：' + row.plantName, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[1-9]\d*$/,
        inputErrorMessage: '不能为空并且为整数',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            if (this.stockCompare(row.goodsStock, instance.inputValue, type)) {
              const data = { type: type, number: Number(instance.inputValue), id: row.id }
              updateStock(data).then(res => {
                done()
                this.$notify.success({
                  title: '成功',
                  message: '调整成功'
                })
                this.getList()
              }).catch(err => {
                this.$notify.error({
                  title: '失败',
                  message: err.data.errmsg
                })
              })
            }
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        })
      })
    },
    stockCompare(stockNum, newStock, type) {
      if (type === 'subtract') {
        if (Number(newStock) > Number(stockNum)) {
          this.$message({
            message: '您需要减少的库存数量大于你现有的库存数量，请从新输入',
            type: 'warning'
          })
          return false
        }
      } else {
        if (parseInt(newStock) > 1000000) {
          this.$message({
            message: '您增加库存数量不能超过1000000件',
            type: 'warning'
          })
          return false
        }
      }
      return true
    },
    getStock(type) {
      if (type === 'add') {
        return '请输入需要增加的库存件数'
      } else {
        return '请输入需要减少的库存件数'
      }
    }
  }
}
</script>

