<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.codeName" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编码名称" />
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编码" />
      <el-input v-model="listQuery.specsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品规格编码" />
      <el-date-picker v-model="listQuery.beginDate" clearable class="filter-item" type="date" placeholder="起始时间" />
      <el-date-picker v-model="listQuery.endDate" clearable class="filter-item" type="date" placeholder="结束时间" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" label="编号" type="index" width="80" />
        <el-table-column align="center" label="工厂名称" prop="plantName" />
        <el-table-column align="center" label="商品编码名称" prop="codeName" />
        <el-table-column align="center" label="商品编码" prop="goodsCode" />
        <el-table-column align="center" label="商品规格" prop="specsDesc">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.specsDesc" :key="tag">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="商品规格编码" prop="specsCode" />
        <el-table-column align="center" label="商品规格图片" prop="url">
          <template slot-scope="scope">
            <el-image style="width: 70px; height: 70px" fit="contain" :src="scope.row.url" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="工厂库存" prop="goodsStock" />
        <el-table-column align="center" label="操作时间" prop="dateTime" />
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>
  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { warnList } from '@/api/stock/warming'

export default {
  name: 'Warning',
  components: { Pagination },
  data() {
    return {
      listQuery: {
        codeName: '',
        beginDate: '',
        endDate: '',
        goodsCode: '',
        specsCode: '',
        limit: 20,
        page: 1
      },
      listLoading: false,
      list: [],
      total: 0
    }
  },
  computed: {
    _params() {
      return {
        beginDate: this.listQuery.beginDate ? parseTime(this.listQuery.beginDate, '{y}-{m}-{d}') : undefined,
        endDate: this.listQuery.endDate ? parseTime(this.listQuery.endDate, '{y}-{m}-{d}') : undefined,
        codeName: this.listQuery.codeName ? this.listQuery.codeName : undefined,
        goodsCode: this.listQuery.goodsCode ? this.listQuery.goodsCode : undefined,
        specsCode: this.listQuery.specsCode ? this.listQuery.specsCode : undefined,
        page: this.listQuery.page,
        limit: this.listQuery.limit
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化
    getList() {
      warnList(this._params).then(res => {
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
    }
  }
}
</script>
