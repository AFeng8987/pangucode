<template>
  <el-dialog :title="'活动板块商品 (' + titleText + ')'" :visible.sync="dialogVisible" top="5vh" width="75%" :close-on-click-modal="false" @open="dialogOpen">
    <div class="filter-container">
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称" />
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编码" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>
    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleAdd">添加商品</el-button>
          </el-col>
        </el-row>
      </div>
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column label="商品ID" align="center" prop="id" />
        <el-table-column label="商品编码" align="center" prop="goodsCode" />
        <el-table-column label="商品信息" align="center" prop="name" width="170">
          <template slot-scope="scope">
            <div class="goods_pic"><el-image style="width: 40px; height: 40px;" :src="scope.row.picUrl" /></div>
            <div class="goods_name">{{ scope.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column label="价格" align="center" prop="retailPrice" />
        <el-table-column label="运费" align="center" prop="freight" />
        <el-table-column label="库存" align="center" prop="inventory" />
        <el-table-column label="销量" align="center" prop="sales" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>
    <add-goods :id="config.activityId" v-model="config.dialogVisible" @submit="getList" />
  </el-dialog>
</template>

<script>
import { activityGoodsList, activityGoodsDelete } from '@/api/activity/boutique'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import addGoods from './addGoods'

export default {
  name: 'EditActiveGoods',
  components: { Pagination, addGoods },
  props: {
    value: Boolean,
    data: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      dialogVisible: false,
      listLoading: false,
      list: [],
      listQuery: {
        activityId: undefined,
        goodsCode: undefined,
        goodsName: undefined,
        limit: 10,
        page: 1
      },
      total: 0,
      config: {
        dialogVisible: false,
        activityId: null
      },
      titleText: ''
    }
  },
  computed: {
    _pasear() {
      return {
        activityId: this.listQuery.activityId ? Number(this.listQuery.activityId) : undefined,
        goodsCode: this.listQuery.goodsCode ? this.listQuery.goodsCode : undefined,
        goodsName: this.listQuery.goodsName ? this.listQuery.goodsName : undefined,
        limit: this.listQuery.limit,
        page: this.listQuery.page
      }
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
      this.titleText = this.data.activityName
      this.listQuery.activityId = this.data.id
      this.listQuery.page = 1
      this.getList()
    },
    getList() {
      this.listLoading = true
      activityGoodsList(this._pasear).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleAdd() {
      this.config.dialogVisible = true
      this.config.activityId = this.listQuery.activityId
    },
    handleDelete(row) {
      this.$confirm('此操作将去掉该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        activityGoodsDelete(this.listQuery.activityId, row.id).then(res => {
          this.$notify({
            title: '成功',
            message: '操作成功!',
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

<style scoped>
.goods_pic{
  display: inline-block;
  width: 40px;
  vertical-align: middle;
}
.goods_name{
  display: inline-block;
  width: 100px;
  vertical-align: middle;
}
</style>
