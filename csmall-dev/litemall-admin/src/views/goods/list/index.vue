<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.goodsId" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品id" />
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品编码" />
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品名称" />
      <el-input v-model="listQuery.categoryName" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入类目名称" />
      <el-select v-model="listQuery.isOnSale" clearable class="filter-item" size="small" style="width: 160px;" placeholder="请选择上下架状态">
        <el-option v-for="(item, index) in options" :key="index" :label="item.label" :value="item.value" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['GET /admin/ad/list']" class="filter-item" type="primary" size="small" @click="handleBanner">配置Banner</el-button>
            <el-button v-permission="['POST /admin/goods/create']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">添加</el-button>
            <el-button v-permission="['POST /admin/goods/homeGoods']" class="filter-item" type="primary" size="small" @click="handleIsHome">首页展示确认</el-button>
          </el-col>
        </el-row>
      </div>

      <el-tabs v-model="activeName" type="border-card" @tab-click="handleActiveClick">
        <el-tab-pane v-for="item in goodsStatusOption" :key="item.value" :name="item.value" :label="item.label">
          <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="tableChange">
            <el-table-column align="center" type="selection" width="55" />
            <el-table-column align="center" label="商品id" prop="id" />
            <el-table-column align="center" label="商品编码" prop="goodsCode" />
            <el-table-column align="center" min-width="100" label="名称" prop="name" />
            <el-table-column align="center" property="iconUrl" label="图片">
              <template slot-scope="scope">
                <el-popover placement="right">
                  <img slot="reference" :src="scope.row.picUrl" width="40">
                  <img :src="scope.row.picUrl" width="400">
                </el-popover>
              </template>
            </el-table-column>

            <el-table-column align="center" label="当前价格" prop="retailPrice" />

            <el-table-column align="center" label="所在类目" prop="categoryName" />

            <el-table-column align="center" label="是否首页展示" prop="isHome">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.isHome" type="success">是</el-tag>
                <el-tag v-else type="success">否</el-tag>
              </template>
            </el-table-column>

            <el-table-column align="center" label="首页展示排序" prop="homeSortOrder" />

            <el-table-column align="center" label="是否热品" prop="isHot">
              <template slot-scope="scope">
                <div v-permission="['POST /admin/goods/hotSale']"><el-switch v-model="scope.row.isHot" @change="isHotChange(scope.row)" /></div>
                <span v-show="scope.row.isHot === true">热品</span>
                <span v-show="scope.row.isHot === false">非热品</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="商品预览">
              <template slot-scope="scope">
                <el-link type="primary" :underline="false" @click="popoverShow(scope.row)">点击查看</el-link>
              </template>
            </el-table-column>

            <el-table-column align="center" label="是否在售" prop="isOnSale">
              <template slot-scope="scope">
                <div v-permission="['POST /admin/goods/onSale']">
                  <el-switch v-model="scope.row.isOnSale" @change="isOnSaleChange(scope.row)" />
                </div>
                <span v-show="scope.row.isOnSale === true">上架</span>
                <span v-show="scope.row.isOnSale === false">下架</span>
              </template>
            </el-table-column>

            <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button v-permission="['POST /admin/goods/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
                <el-button v-permission="['POST /admin/goods/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

    <el-dialog title="预览商品扫一扫" :visible.sync="dialogVisible" width="25%" @opened="openedQRCode" @close="closeQRCode">
      <div class="qrCodeUrl">
        <div ref="qrCodeUrl" />
      </div>
      <div class="goodsName">{{ goodsName }}</div>
    </el-dialog>

    <el-dialog title="是否首页展示" :visible.sync="isHomeDialogVisible" center width="20%">
      <div style="text-align: center;">
        <el-switch
          v-model="isHomeData.isHome"
          active-text="是"
          inactive-text="否"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isHomeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="isHomeClick">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<style scoped>
.qrCodeUrl{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.goodsName{
  text-align: center;
  font-size: 20px;
  margin-top: 20px;
}
</style>

<script>
import { listGoods, deleteGoods, goodsOnSale, homeGoods } from '@/api/goods/goods'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

import QRCode from 'qrcodejs2'

import { updateGoods } from '@/api/activity/heatBuy'

export default {
  name: 'GoodsList',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      link: process.env.VUE_APP_EXTERNAL_SERVER,
      dialogVisible: false,
      goodsId: null,
      goodsName: '',
      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsCode: undefined,
        goodsName: undefined,
        categoryName: undefined,
        isHome: undefined,
        isOnSale: undefined
      },
      goodsStatusOption: [
        {
          value: '1',
          label: '全部商品'
        },
        {
          value: '2',
          label: '首页展示商品'
        }
      ],
      activeName: '1',
      isHomeData: {
        goodsIds: [],
        isHome: true
      },
      isHomeDialogVisible: false,
      options: [
        {
          value: true,
          label: '上架状态'
        },
        {
          value: false,
          label: '下架状态'
        }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化
    getList() {
      this.listLoading = true
      listGoods(this.listQuery).then(response => {
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
    // 添加
    handleCreate() {
      this.$router.push({ path: '/goods/create' })
    },
    // 编辑
    handleUpdate(row) {
      if (!row.isOnSale) {
        this.$router.push({ path: '/goods/edit', query: { id: row.id }})
      } else {
        this.$message({
          message: '请先将商品下架',
          type: 'warning'
        })
      }
    },
    // 配置banner
    handleBanner() {
      this.$router.push({ path: '/goods/banner' })
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGoods(row).then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        }).catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 上下架
    isOnSaleChange(row) {
      if (row.isOnSale === false) {
        this.$confirm('此操作将该商品下架, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.updateIsOnSale({ id: row.id, isOnSale: row.isOnSale })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消操作'
          })
          this.getList()
        })
      } else {
        this.updateIsOnSale({ id: row.id, isOnSale: row.isOnSale })
      }
    },
    // 是否热卖
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
    // 调用热卖商品
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
    },
    // 掉用上下架接口
    updateIsOnSale(msg) {
      goodsOnSale(msg).then(res => {
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
    },
    popoverShow(row) {
      this.dialogVisible = true
      this.goodsId = row.id
      this.goodsName = row.name
    },
    openedQRCode() {
      new QRCode(this.$refs.qrCodeUrl, {
        text: this.link + 'preview?d=' + this.goodsId,
        width: 300,
        height: 300,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H
      })
    },
    closeQRCode() {
      this.$refs.qrCodeUrl.innerHTML = ''
    },
    handleActiveClick(tab) {
      if (tab.name === '1') {
        this.listQuery.isHome = undefined
      } else {
        this.listQuery.isHome = true
      }
      this.getList()
    },
    tableChange(selection) {
      this.isHomeData.goodsIds = []
      selection.forEach(element => {
        this.isHomeData.goodsIds.push(element.id)
      })
    },
    handleIsHome() {
      if (this.isHomeData.goodsIds.length <= 0) {
        this.$message({
          message: '您未选择',
          type: 'warning'
        })
        return
      }

      this.isHomeDialogVisible = true
    },
    isHomeClick() {
      homeGoods(this.isHomeData).then(res => {
        this.$notify.success({
          title: '成功',
          message: '操作成功'
        })
        this.isHomeDialogVisible = false
        this.getList()
      }).catch(err => {
        this.isHomeDialogVisible = false
        this.$notify.error({
          title: '失败',
          message: err.data.errmsg
        })
      })
    }
  }
}
</script>
