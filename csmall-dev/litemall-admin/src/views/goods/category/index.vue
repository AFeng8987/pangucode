<template>
  <div class="app-container">

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/category/create']" class="filter-item" type="primary" size="small" @click="handleLevelOneCreate">新增一级分类</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 查询结果 -->
      <el-table ref="table" v-loading="listLoading" :data="list" :row-key="tableKey" :expand-row-keys="expands" element-loading-text="正在查询中。。。" border fit highlight-current-row @expand-change="toggleRowExpansion">
        <el-table-column type="expand">
          <template slot-scope="scope">
            <el-table :data="scope.row.create" border fit highlight-current-row>
              <el-table-column align="center" label="分类ID" prop="id" />
              <el-table-column align="center" label="分类图标" prop="iconUrl">
                <template slot-scope="col">
                  <img v-if="col.row.iconUrl" :src="col.row.iconUrl" width="40">
                </template>
              </el-table-column>
              <el-table-column align="center" label="分类名称" prop="name" />
              <el-table-column align="center" label="分类等级" prop="level">
                <template slot-scope="col">
                  <el-tag :type="col.row.level === 'L1' ? 'primary' : 'info' ">{{ col.row.level === 'L1' ? '一级类目' : '二级类目' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column align="center" label="关键字" prop="keywords" />
              <el-table-column align="center" label="排序" prop="sortOrder" />
              <el-table-column align="center" label="操作" width="190">
                <template slot-scope="col">
                  <el-button v-if="col.row.level === 'L1'" v-permission="['POST /admin/category/create']" type="primary" size="mini" @click="handleLevelTwoCreate(col.row)">新增二级分类</el-button><br>
                  <el-button v-permission="['POST /admin/category/update']" style="margin-top: 6px;" type="primary" size="mini" @click="handleUpdate(col.row)">编辑</el-button>
                  <el-button v-permission="['POST /admin/category/delete']" type="danger" size="mini" @click="handleDelete(col.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column align="center" label="分类ID" prop="id" />
        <el-table-column align="center" label="分类图标" prop="iconUrl">
          <template slot-scope="col">
            <img v-if="col.row.iconUrl" :src="col.row.iconUrl" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" label="分类名称" prop="name" />
        <el-table-column align="center" label="分类等级" prop="level">
          <template slot-scope="col">
            <el-tag :type="col.row.level === 'L1' ? 'primary' : 'info' ">{{ col.row.level === 'L1' ? '一级类目' : '二级类目' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="排序" prop="sortOrder" />
        <el-table-column align="center" label="操作" width="190">
          <template slot-scope="col">
            <el-button v-if="col.row.level === 'L1'" v-permission="['POST /admin/category/create']" type="primary" size="mini" @click="handleLevelTwoCreate(col.row)">新增二级分类</el-button><br>
            <el-button v-permission="['POST /admin/category/update']" style="margin-top: 6px;" type="primary" size="mini" @click="handleUpdate(col.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/category/delete']" type="danger" size="mini" @click="handleDelete(col.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

    <!-- 新增/编辑 -->
    <category v-model="config.dialogVisible" :state="config.dialogStatus" :data="config.data" @submit="getList" />
  </div>
</template>

<script>
import { listCategory, deleteCategory } from '@/api/goods/category'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import Category from './components/category'

export default {
  name: 'GoodsCategory',
  components: { BackToTop, Category, Pagination },
  data() {
    return {
      listLoading: false,
      list: [],
      total: 0,
      config: {
        dialogVisible: false,
        dialogStatus: '',
        data: null
      },
      listQuery: {
        page: 1,
        limit: 10
      },
      L2List: [],
      expands: [],
      index: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化
    getList() {
      this.expands = []
      this.listLoading = true
      listCategory(this.listQuery.page, this.listQuery.limit).then(response => {
        this.list = response.list
        this.total = response.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    // 新增一级分类
    handleLevelOneCreate() {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'create'
      this.config.data = null
    },
    // 新增二级分类
    handleLevelTwoCreate(row) {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'create'
      this.config.data = row
    },
    // 编辑
    handleUpdate(row) {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'update'
      this.config.data = row
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该类目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCategory(row)
          .then(response => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '删除成功',
              duration: 1500
            })
          })
          .catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg,
              duration: 1500
            })
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    tableKey(row) {
      return row.id
    },
    toggleRowExpansion(row, index) {
      this.index = !this.index
      this.expands = []
      if (!index.length) {
        this.expands = []
      } else {
        this.expands.push(row.id)
        this.getData(row.id)
      }
    },
    getData(id) {
      const level = 'L2'
      listCategory(undefined, 100, level, id).then(res => {
        this.list.forEach(item => {
          if (id === item.id) {
            item.create = res.list
          }
        })
      })
    }
  }
}
</script>
