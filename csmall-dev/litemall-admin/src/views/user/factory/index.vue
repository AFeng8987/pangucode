<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入工厂名称" />
      <el-input v-model="listQuery.phone" clearable class="filter-item" style="width: 200px;" placeholder="请输入工厂联系电话" />
      <el-input v-model="listQuery.contacts" clearable class="filter-item" style="width: 200px;" placeholder="请输入工厂联系人" />
      <el-date-picker v-model="listQuery.addTime" clearable class="filter-item" type="date" placeholder="添加时间" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/plant/add']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">添加工厂</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <!-- <el-table-column align="center" type="selection" width="55" /> -->
        <el-table-column align="center" label="编号" prop="id" />
        <el-table-column align="center" label="工厂名称" prop="plantName" />
        <el-table-column align="center" label="工厂地址" prop="plantAddress" />
        <el-table-column align="center" label="工厂联系人" prop="plantContacts" />
        <el-table-column align="center" label="工厂联系电话" prop="plantPhone" />
        <el-table-column align="center" label="发货联系人" prop="sendContacts" />
        <el-table-column align="center" label="发货地址" prop="sendAddress" />
        <el-table-column align="center" label="发货电话" prop="sendPhone" />
        <el-table-column align="center" label="收货联系人" prop="receiveContacts" />
        <el-table-column align="center" label="收货地址" prop="receiveAddress" />
        <el-table-column align="center" label="收货电话" prop="receivePhone" />
        <el-table-column align="center" label="添加日期" prop="addTime" width="100" />
        <!-- <el-table-column align="center" label="货物数量（种类）" prop="goodsNumber" />
        <el-table-column align="center" label="实际库存" prop="stock" />
        <el-table-column align="center" label="销售量" prop="salesVolume" /> -->
        <el-table-column align="center" label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/plant/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button><br>
            <el-button v-permission="['POST /admin/plant/del/{id}']" style="margin-top: 5px" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <factory-operation v-model="config.dialogVisible" :state="config.dialogStatus" :data="config.data" @submit="getList" />
  </div>
</template>

<script>
import factoryOperation from './components/factoryOperation'
import { plantList, plantDelete } from '@/api/user/factory'
import { parseTime } from '@/utils/index'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Factory',
  components: { factoryOperation, Pagination },
  data() {
    return {
      listQuery: {
        name: undefined,
        phone: undefined,
        contacts: undefined,
        addTime: undefined,
        limit: 20,
        page: 1
      },
      listLoading: false,
      list: [],
      total: 0,
      config: {
        dialogVisible: false,
        dialogStatus: '',
        data: null
      }
    }
  },
  computed: {
    _params() {
      return {
        name: this.listQuery.name ? this.listQuery.name : undefined,
        phone: this.listQuery.phone ? this.listQuery.phone : undefined,
        contacts: this.listQuery.contacts ? this.listQuery.contacts : undefined,
        addTime: this.listQuery.addTime ? parseTime(this.listQuery.addTime, '{y}-{m}-{d}') : undefined,
        limit: this.listQuery.limit,
        page: this.listQuery.page
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      plantList(this._params).then(res => {
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
    // 新增工厂
    handleCreate() {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'create'
      this.config.data = null
    },
    // 编辑工厂
    handleUpdate(row) {
      this.config.dialogVisible = true
      this.config.dialogStatus = 'update'
      this.config.data = row
    },
    // 删除工厂
    handleDelete(row) {
      this.$confirm('此操作将删除该工厂, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        plantDelete(row.id).then(res => {
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
          message: '已取消操作'
        })
      })
    }
  }
}
</script>
