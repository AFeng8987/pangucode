<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.payOrderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入主订单号" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入子订单号" />
      <el-input v-model="listQuery.allianceName" clearable class="filter-item" style="width: 160px;" placeholder="请输入加盟商姓名" />
      <el-input v-model="listQuery.userName" clearable class="filter-item" style="width: 160px;" placeholder="请输入加盟商账号" />
      <el-date-picker v-model="listQuery.createTimeArray" type="daterange" class="filter-item" range-separator="至" start-placeholder="订单关闭开始日期" end-placeholder="订单关闭结束日期" :picker-options="pickerOptions" />
      <el-date-picker v-model="listQuery.orderTimeArray" type="daterange" class="filter-item" range-separator="至" start-placeholder="订单创建开始日期" end-placeholder="订单创建结束日期" :picker-options="pickerOptions" />

      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="8">
            <span>数据列表</span>
          </el-col>
          <el-col :span="16" style="text-align: right">
            <el-button v-permission="['POST /admin/order/export']" type="primary" size="small" @click="exportSelected">导出选中订单</el-button>
            <el-button v-if="searchHidden" v-permission="['POST /admin/order/export']" type="success" size="small" @click="exportSearch">导出搜索订单</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table ref="orderTable" v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="tableChange">
        <el-table-column align="center" type="selection" width="55" />
        <el-table-column align="center" label="加盟商姓名" prop="allianceName" />
        <el-table-column align="center" label="加盟商账号" prop="userName" />
        <el-table-column align="center" label="主订单号" prop="payOrderSn" />
        <el-table-column align="center" label="子订单号" prop="orderSn" />
        <el-table-column align="center" label="类目" prop="categoryName" />
        <el-table-column align="center" label="数量" prop="goodsNumber" />
        <el-table-column align="center" label="销售价格" prop="goodsPrice" />
        <el-table-column align="center" label="成本单价" prop="plantCostPrice" />
        <el-table-column align="center" label="总利润" prop="totalProfit" />
        <el-table-column align="center" label="优惠金额" prop="couponPrice" />
        <el-table-column align="center" label="工厂" prop="plantName" />
        <el-table-column align="center" label="订单关闭时间" prop="orderCloseTime" width="100" />
        <el-table-column align="center" label="创建时间" prop="orderAddTime" width="100" />
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 导出提示 -->
    <el-dialog :title="exportTitle" :visible.sync="dialogExportVisiable" :close-on-click-modal="false" width="30%">
      <div style="text-align: center;font-size: 16px;"><i class="el-icon-success" />{{ exportFont }}</div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogExportVisiable = false">取 消</el-button>
        <el-button type="primary" @click="downSureExc">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { orderExportList, orderExport } from '@/api/order/orderExport'
import { parseTime } from '@/utils/index'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'OrderExport',
  components: { Pagination },
  data() {
    return {
      listQuery: {
        limit: 20,
        page: 1,
        allianceName: undefined,
        orderAddEnd: undefined,
        orderAddStart: undefined,
        userName: undefined,
        orderCloseStart: undefined,
        orderCloseEnd: undefined,
        orderSn: undefined,
        payOrderSn: undefined,
        orderTimeArray: [],
        createTimeArray: []
      },
      listLoading: false,
      list: [],
      total: 0,
      groupQuery: {
        address: undefined,
        type: 1,
        limit: 20,
        page: 1
      },
      searchHidden: false,
      exportTitle: '',
      exportFont: '',
      dialogExportVisiable: false,
      downLoadExl: {
        ids: [],
        type: undefined,
        groupId: undefined,
        orderSn: undefined,
        orderAddStart: undefined,
        orderAddEnd: undefined,
        start: undefined,
        end: undefined
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近二周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 14)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近二个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 60)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    }
  },
  computed: {
    _params() {
      return {
        limit: this.listQuery.limit,
        page: this.listQuery.page,
        orderCloseStart: this.listQuery.orderCloseStart ? parseTime(this.listQuery.orderCloseStart, '{y}-{m}-{d}') : undefined,
        orderCloseEnd: this.listQuery.orderCloseEnd ? parseTime(this.listQuery.orderCloseEnd, '{y}-{m}-{d}') : undefined,
        orderSn: this.listQuery.orderSn ? this.listQuery.orderSn : undefined,
        payOrderSn: this.listQuery.payOrderSn ? this.listQuery.payOrderSn : undefined,
        allianceName: this.listQuery.allianceName ? this.listQuery.allianceName : undefined,
        userName: this.listQuery.userName ? this.listQuery.userName : undefined,
        orderAddStart: this.listQuery.orderAddStart ? parseTime(this.listQuery.orderAddStart, '{y}-{m}-{d}') : undefined,
        orderAddEnd: this.listQuery.orderAddEnd ? parseTime(this.listQuery.orderAddEnd, '{y}-{m}-{d}') : undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取订单导出list
    getList() {
      this.listLoading = true
      orderExportList(this._params).then(res => {
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
      if (this.listQuery.orderTimeArray && this.listQuery.orderTimeArray.length === 2) {
        this.listQuery.orderAddStart = this.listQuery.orderTimeArray[0]
        this.listQuery.orderAddEnd = this.listQuery.orderTimeArray[1]
      } else {
        this.listQuery.orderAddStart = undefined
        this.listQuery.orderAddEnd = undefined
      }

      if (this.listQuery.createTimeArray && this.listQuery.createTimeArray.length === 2) {
        this.listQuery.orderCloseStart = this.listQuery.createTimeArray[0]
        this.listQuery.orderCloseEnd = this.listQuery.createTimeArray[1]
      } else {
        this.listQuery.orderCloseStart = undefined
        this.listQuery.orderCloseEnd = undefined
      }
      if (this.listQuery.userName || this.listQuery.allianceName || this.listQuery.orderSn || this.listQuery.payOrderSn || this.listQuery.orderAddStart || this.listQuery.orderAddEnd || this.listQuery.orderCloseStart || this.listQuery.orderCloseEnd) {
        this.searchHidden = true
      } else {
        this.searchHidden = false
      }
      this.getList()
    },
    // 选择需要导出的订单
    tableChange(selection) {
      this.downLoadExl.ids = []
      selection.forEach(element => {
        this.downLoadExl.ids.push(element.id)
      })
    },
    // 导出选中订单
    exportSelected() {
      if (this.downLoadExl.ids.length <= 0) {
        this.$message({
          showClose: true,
          message: '请先选择你需要部分导出的数据',
          type: 'warning'
        })
      } else {
        this.exportTitle = '导出选中订单'
        this.downLoadExl.type = 'part'
        this.exportFont = '将导出您勾选的内容报表'
        this.dialogExportVisiable = true
      }
    },
    // 导出搜索订单
    exportSearch() {
      this.exportTitle = '导出搜索订单'
      this.downLoadExl.type = 'All'
      this.exportFont = '将为您导出搜索的内容报表'
      this.dialogExportVisiable = true
    },
    // 确定导出
    downSureExc() {
      this.downLoadExl.allianceName = this.listQuery.allianceName ? this.listQuery.allianceName : undefined
      this.downLoadExl.userName = this.listQuery.userName ? this.listQuery.userName : undefined
      this.downLoadExl.orderSn = this.listQuery.orderSn ? this.listQuery.orderSn : undefined
      this.downLoadExl.payOrderSn = this.listQuery.payOrderSn ? this.listQuery.payOrderSn : undefined
      this.downLoadExl.orderAddStart = this.listQuery.orderAddStart ? parseTime(this.listQuery.orderAddStart, '{y}-{m}-{d}') : undefined
      this.downLoadExl.orderAddEnd = this.listQuery.orderAddEnd ? parseTime(this.listQuery.orderAddEnd, '{y}-{m}-{d}') : undefined
      this.downLoadExl.orderCloseStart = this.listQuery.orderCloseStart ? parseTime(this.listQuery.orderCloseStart, '{y}-{m}-{d}') : undefined
      this.downLoadExl.orderCloseEnd = this.listQuery.orderCloseEnd ? parseTime(this.listQuery.orderCloseEnd, '{y}-{m}-{d}') : undefined

      orderExport(this.downLoadExl).then(res => {}).catch(err => {
        this.downloads(err.data, '订单报表' + parseTime(new Date(), '{y}{m}{d}{h}{i}{s}'))
        this.dialogExportVisiable = false
        this.$notify({
          title: '成功',
          message: '导出成功',
          type: 'success'
        })
      })
    },
    // 接口返回的文件流
    downloads(data, name) {
      if (!data) {
        return
      }
      const url = window.URL.createObjectURL(new Blob([data]))
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.setAttribute('download', `${name}.xls`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }
  }
}
</script>
