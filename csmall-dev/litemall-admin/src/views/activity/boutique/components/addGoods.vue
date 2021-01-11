<template>
  <el-dialog title="添加商品" width="60%" :visible.sync="dialogVisible" append-to-body :close-on-click-modal="false" @open="dialogOpen" @close="dialogClose">
    <div class="filter-container">
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称" />
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编码" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleGoodsFilter">查找</el-button>
    </div>
    <el-card :body-style="{ padding: '0px' }">
      <el-table ref="goodsTable" v-loading="listLoading" :row-key="tableKey" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="tableChange">
        <el-table-column :reserve-selection="true" align="center" :selectable="selectable" type="selection" width="55" />
        <el-table-column align="center" label="商品ID" prop="id" />
        <el-table-column align="center" label="商品编码" prop="goodsCode" />
        <el-table-column align="center" label="商品名称" prop="name" />
        <el-table-column align="center" label="商品图片" prop="picUrl">
          <template slot-scope="scope">
            <el-image style="width: 50px; height: 50px" :src="scope.row.picUrl" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="是否在售" prop="onSale">
          <template slot-scope="scope">
            <el-tag :type="scope.row.onSale ? 'success' : 'error' ">{{ scope.row.onSale ? '在售' : '未售' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="dialogOpen" />
    </el-card>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { activityGoodsAdd, activityGoodsListData } from '@/api/activity/boutique'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'AddGoods',
  components: { Pagination },
  props: {
    value: Boolean,
    id: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      listQuery: {
        activityId: undefined,
        goodsName: undefined,
        goodsCode: undefined,
        limit: 10,
        page: 1
      },
      listLoading: false,
      list: [],
      total: 0,
      selectData: []
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
      this.listLoading = true
      this.listQuery.activityId = this.id
      activityGoodsListData(this.listQuery).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleGoodsFilter() {
      this.listQuery.page = 1
      this.dialogOpen()
    },
    selectable(row) {
      if (row.onSale) {
        return true
      } else {
        return false
      }
    },
    tableChange(selection) {
      this.selectData = []
      selection.forEach(item => {
        this.selectData.push(item.id)
      })
    },
    tableKey(row) {
      return row.id
    },
    dialogClose() {
      this.dialogVisible = false
      this.selectData = []
      this.$refs.goodsTable.clearSelection()
    },
    handleSubmit() {
      if (this.selectData.length === 0) {
        return
      }
      activityGoodsAdd({ activityId: this.id, goodsIds: this.selectData }).then(res => {
        this.$notify({
          title: '成功',
          message: '操作成功!',
          type: 'success'
        })
        this.dialogVisible = false
        this.selectData = []
        this.$refs.goodsTable.clearSelection()
        this.listQuery.page = 1
        this.$emit('submit')
      }).catch(err => {
        this.$notify.error({
          title: '失败',
          message: err.data.errmsg
        })
      })
    }
  }
}
</script>
