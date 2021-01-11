<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.nickName" clearable class="filter-item" style="width: 200px;" placeholder="请输入昵称" />
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入账号" />
      <!-- <el-select v-model="listQuery.userLevel" clearable class="filter-item" style="width: 200px;" placeholder="请选择账号属性">
        <el-option v-for="item in activeData" :key="item.value" :label="item.label" :value="item.value" />
      </el-select> -->
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>

      <el-tabs v-model="activeName" type="border-card" @tab-click="handleActiveClick">
        <el-tab-pane v-for="item in activeData" :key="item.name" :label="item.label" :name="item.name">
          <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
            <el-table-column :index="indexMethod" align="center" label="编号" width="100" type="index" />
            <el-table-column align="center" label="昵称" prop="nickname" />
            <el-table-column align="center" label="账号" prop="username" />
            <el-table-column align="center" label="账号属性" prop="userLevel">
              <template slot-scope="scope">
                <span v-if="scope.row.userLevel === 0 ">普通会员</span>
                <span v-else>加盟商</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="注册日期" prop="addTime" width="160" />
            <el-table-column align="center" label="操作变更时间" prop="updateTime" width="160" />
            <el-table-column align="center" label="操作">
              <template slot-scope="scope">
                <el-button style="margin-bottom: 10px;" type="success" size="mini" @click="handleLevel(scope.row)">团队查看</el-button><br>
                <el-button v-if="scope.row.userLevel === 0" v-permission="['POST /admin/user/allianceAdd']" type="primary" size="mini" @click="handleChengaMerchants(scope.row)">变更为加盟商</el-button>
                <el-button v-else v-permission="['POST /admin/user/allianceDel/{id}']" type="danger" size="mini" @click="handleChengaMember(scope.row)">变更为普通会员</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 变更为加盟商 -->
    <chenga-merchants v-model="config.dialogVisible" :data="config.data" @submit="getList" />

    <!-- 等级 -->
    <view-level v-model="level.dialogVisible" :data="level.data" />
  </div>
</template>

<script>
import chengaMerchants from './components/chengaMerchants'
import viewLevel from './components/viewLevel'
import { userList, allianceDel } from '@/api/user/member'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Memder',
  components: { chengaMerchants, Pagination, viewLevel },
  data() {
    return {
      listQuery: {
        nickName: undefined,
        order: 'desc',
        userLevel: undefined,
        username: undefined,
        sort: 'add_time',
        limit: 20,
        page: 1
      },
      activeName: '3',
      activeData: [
        { label: '全部会员', name: '3', value: '' },
        { label: '加盟商', name: '1', value: 1 },
        { label: '普通会员', name: '0', value: 0 }
      ],
      listLoading: false,
      list: [],
      total: 0,
      config: {
        dialogVisible: false,
        data: null
      },
      level: {
        dialogVisible: false,
        data: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化
    getList() {
      this.listLoading = true
      userList(this.listQuery).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    // 编号自动叠加
    indexMethod(index) {
      return (this.listQuery.page - 1) * this.listQuery.limit + index + 1
    },
    // 搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    // tabs切换
    handleActiveClick(tab) {
      if (Number(tab.name) === 3) {
        this.listQuery.userLevel = ''
      } else {
        this.listQuery.userLevel = Number(tab.name)
      }
      this.getList()
    },
    // 变更加盟商
    handleChengaMerchants(row) {
      this.config.dialogVisible = true
      this.config.data = row
    },
    // 等级
    handleLevel(row) {
      this.level.dialogVisible = true
      this.level.data = row
    },
    // 变更普通会员
    handleChengaMember(row) {
      this.$confirm('此操作将变更为普通会员, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        allianceDel(row.id).then(res => {
          this.$notify.success({
            title: '成功',
            message: '变更成功'
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

