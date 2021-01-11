<template>
  <div class="app-container">

    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入banner名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/ad/create']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">添加banner</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" label="编号" prop="id" />
        <el-table-column align="center" label="Banner名称" prop="name" />
        <el-table-column align="center" label="Banner图片" prop="url">
          <template slot-scope="scope">
            <el-image style="width: 80px" :src="scope.row.url" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="关联商品" prop="goodsName" />
        <el-table-column align="center" label="关联图片" prop="goodsPic">
          <template slot-scope="scope">
            <span v-if="scope.row.goodsPic === '-'">{{ scope.row.goodsPic }}</span>
            <el-image v-else style="width: 80px" :src="scope.row.goodsPic" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="关联链接" prop="link" />
        <el-table-column align="center" label="排序" prop="sortOrder" />
        <el-table-column align="center" label="是否启用" prop="enabled">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.enabled === true" type="success">启用</el-tag>
            <el-tag v-else type="danger">不启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/ad/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button><br>
            <el-button v-permission="['POST /admin/ad/delete']" style="margin-top: 5px" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 添加/编辑 -->
    <handle-banner v-model="config.dialogVisible" :status="config.dialogStatus" :data="config.data" @submit="getList" />
  </div>
</template>

<script>
import { adList, adDelete } from '@/api/goods/banner'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import handleBanner from './components/handleBanner'

export default {
  name: 'Banner',
  components: { Pagination, handleBanner },
  data() {
    return {
      listQuery: {
        content: undefined,
        name: undefined,
        order: 'desc',
        sort: 'add_time',
        limit: 20,
        page: 1
      },
      listLoading: true,
      list: [],
      total: 0,
      config: {
        dialogVisible: false,
        dialogStatus: '',
        data: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取banner列表
    getList() {
      adList(this.listQuery).then(res => {
        this.listLoading = false
        this.list = res.data.data.list
        this.total = res.data.data.total
      }).catch(() => {
        this.listLoading = false
        this.list = []
        this.total = 0
      })
    },
    // 搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    // banner添加
    handleCreate() {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'create'
      this.config.data = null
    },
    // 编辑
    handleUpdate(row) {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'update'
      this.config.data = row
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该Banner, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        adDelete(row).then(res => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success'
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
    }
  }
}
</script>
