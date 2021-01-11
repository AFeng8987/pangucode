<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.codeName" clearable class="filter-item" style="width: 200px;" placeholder="请输入编码名称" />
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入编码" />
      <el-input v-model="listQuery.specCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入规格编码" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/stock/addFactory']" class="filter-item" type="primary" size="small" @click="batchHandleAddFactory">批量新增工厂与库存</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="tableChange">
        <el-table-column align="center" type="selection" width="55" />
        <el-table-column align="center" label="编码" prop="goodsCode" />
        <el-table-column align="center" label="编码名称" prop="codeName" />
        <el-table-column align="center" label="商品规格" prop="specsDesc">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.specsDesc" :key="tag">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="规格编码" prop="specsCode" />
        <el-table-column align="center" label="规格图片" prop="url">
          <template slot-scope="scope">
            <el-image style="width: 70px; height: 70px" fit="contain" :src="scope.row.url" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="价格" prop="shopPrice" />
        <el-table-column align="center" label="查看详情" width="180">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/stock/listFactory']" type="success" size="small" @click="handleViewFactory(scope.row)">查看已有工厂与库存</el-button>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="160">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/stock/addFactory']" type="warning" size="small" @click="handleAddFactory(scope.row)">新增工厂与库存</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 添加工厂 -->
    <create-factory v-model="addConfig.dialogAddVisible" :data="addConfig.data" />

    <!-- 查看工厂 -->
    <view-factory v-model="viewConfig.dialogSeeVisible" :data="viewConfig.data" />
  </div>
</template>

<script>
import { stockList } from '@/api/stock/adjust'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import createFactory from './components/createFactory'
import viewFactory from './components/viewFactory'

export default {
  name: 'Adjust',
  components: { Pagination, createFactory, viewFactory },
  data() {
    return {
      listQuery: {
        codeName: undefined,
        goodsCode: undefined,
        specCode: undefined,
        limit: 20,
        page: 1
      },
      listLoading: false,
      list: [],
      total: 0,
      addConfig: {
        dialogAddVisible: false,
        data: []
      },
      viewConfig: {
        dialogSeeVisible: false,
        data: null
      }
    }
  },
  computed: {
    _params() {
      return {
        codeName: this.listQuery.codeName ? this.listQuery.codeName : undefined,
        goodsCode: this.listQuery.goodsCode ? this.listQuery.goodsCode : undefined,
        specCode: this.listQuery.specCode ? this.listQuery.specCode : undefined,
        page: this.listQuery.page,
        limit: this.listQuery.limit
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      stockList(this._params).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    // 搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    // 添加工厂
    handleAddFactory(row) {
      this.addConfig.dialogAddVisible = true
      this.addConfig.data = []
      this.addConfig.data.push(row)
    },
    // 批量添加工厂
    batchHandleAddFactory() {
      if (this.addConfig.data.length > 0) {
        let result = true
        this.addConfig.data.reduce(function(prev, cur) {
          if (prev && prev.goodsCode !== cur.goodsCode) {
            result = false
          }
          return cur
        }, null)

        if (result) {
          this.addConfig.dialogAddVisible = true
        } else {
          this.$message({
            message: '您选中的编码有不同的，请重新选中',
            type: 'warning',
            duration: 1000
          })
        }
      } else {
        this.$message({
          message: '您还未选中',
          type: 'warning',
          duration: 1000
        })
      }
    },
    // 查看工厂
    handleViewFactory(row) {
      this.viewConfig.dialogSeeVisible = true
      this.viewConfig.data = row
    },
    // 选择需要批量的规格
    tableChange(res) {
      this.addConfig.data = []
      this.addConfig.data = res
    }
  }
}
</script>
