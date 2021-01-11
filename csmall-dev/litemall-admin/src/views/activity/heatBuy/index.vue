<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 160px;" placeholder="请输入商品编码" />
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 160px;" placeholder="请输入商品名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>热卖商品列表</span>
      </div>

      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" label="商品ID" prop="id" />

        <el-table-column align="center" min-width="100" label="名称" prop="name" />

        <el-table-column align="center" min-width="100" label="商品编码" prop="goodsCode" />

        <el-table-column align="center" property="iconUrl" label="图片">
          <template slot-scope="scope">
            <img :src="scope.row.picUrl" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="100" label="商品售价" prop="retailPrice" />

        <el-table-column align="center" label="是否在售" prop="isOnSale">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '在售' : '未售' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="是否热品" prop="isHot">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.isHot" @change="isHotChange(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    </el-card>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>
  </div>
</template>

<script>
import { updateGoods, hotGoods } from '@/api/activity/heatBuy'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'HeatBuy',
  components: { Pagination, BackToTop },
  data() {
    return {
      listQuery: {
        goodsCode: undefined,
        name: undefined,
        page: 1,
        limit: 20,
        sort: 'add_time',
        order: 'desc'
      },
      listLoading: false,
      list: [],
      total: 0
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化
    getList() {
      this.listLoading = true
      hotGoods(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
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
    // 是否热卖开关
    isHotChange(row) {
      if (row.isOnSale === true) {
        if (row.isHot === false) {
          this.$confirm('此操作将该商品移除热卖模块, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.updateIsHot({ id: row.id, isHot: row.isHot })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '取消操作'
            })
            this.getList()
          })
        } else {
          this.updateIsHot({ id: row.id, isHot: row.isHot })
        }
      } else {
        this.$message({
          type: 'info',
          message: '你所操作的商品还未上架，请上架之后操作!!!'
        })
        this.getList()
      }
    },
    // 调用热卖开关接口
    updateIsHot(row) {
      updateGoods(row).then(res => {
        this.$notify.success({
          title: '成功',
          message: '操作成功'
        })
        this.getList()
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
