<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 160px;" placeholder="请输入收货人" />
      <el-input v-model="listQuery.userName" clearable class="filter-item" style="width: 160px;" placeholder="请输入用户账号" />
      <el-input v-model="listQuery.payOrderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入主订单号" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入子订单号" />
      <el-date-picker v-model="listQuery.timeArray" clearable type="daterange" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.orderStatusArray" clearable multiple style="width: 160px" class="filter-item" placeholder="请选择订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
        </el-row>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row class="aa">
        <el-table-column align="center" label="主订单号" prop="payOrderSn" />
        <el-table-column align="center" label="子订单号" prop="orderSn" />
        <el-table-column align="center" label="用户账号" prop="userName" />
        <el-table-column align="center" label="收货人" prop="consignee" />
        <el-table-column align="center" label="收货地址" prop="address" />
        <el-table-column align="center" label="订单金额" prop="orderPrice" />
        <el-table-column align="center" label="商品金额" prop="goodsPrice" />
        <el-table-column align="center" label="优惠金额" prop="couponPrice" />
        <el-table-column align="center" label="订单状态" prop="orderStatus">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="加盟商名称" prop="allianceName" />
        <el-table-column align="center" label="时间" prop="addTime" width="100" />
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini" @click="detailsClick(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 查看详情 -->
    <details-order v-model="config.dialogVisible" :data="config.data" />

  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import { orderList } from '@/api/order/order'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import detailsOrder from './components/detailsOrder'

const statusMap = {
  101: '待付款',
  102: '已取消',
  // 103: '已取消',
  201: '待发货',
  301: '已发货',
  401: '已收货',
  // 402: '已收货',
  501: '订单关闭'
}
export default {
  name: 'Order',
  components: { Pagination, detailsOrder },
  filters: {
    orderStatusFilter(status) {
      if (status === 103) {
        return statusMap[102]
      }
      if (status === 402 || status === 403) {
        return statusMap[401]
      }

      return statusMap[status]
    }
  },
  data() {
    return {
      listQuery: {
        page: 1,
        limit: 20,
        start: undefined,
        end: undefined,
        consignee: undefined,
        orderSn: undefined,
        payOrderSn: undefined,
        orderStatusArray: [],
        timeArray: [],
        userName: undefined
      },
      copy: [],
      statusMap,
      // groupMap: [],
      listLoading: false,
      list: [],
      total: 0,
      config: {
        dialogVisible: false,
        data: null
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
        start: this.listQuery.start ? parseTime(this.listQuery.start, '{y}-{m}-{d}') : undefined,
        end: this.listQuery.end ? parseTime(this.listQuery.end, '{y}-{m}-{d}') : undefined,
        consignee: this.listQuery.consignee ? this.listQuery.consignee : undefined,
        orderSn: this.listQuery.orderSn ? this.listQuery.orderSn : undefined,
        payOrderSn: this.listQuery.payOrderSn ? this.listQuery.payOrderSn : undefined,
        userName: this.listQuery.userName ? this.listQuery.userName : undefined,
        orderStatusArray: this.copy
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取订单列表
    getList() {
      this.listLoading = true
      orderList(this._params).then(res => {
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
      if (this.listQuery.timeArray && this.listQuery.timeArray.length === 2) {
        this.listQuery.start = this.listQuery.timeArray[0]
        this.listQuery.end = this.listQuery.timeArray[1]
      } else {
        this.listQuery.start = undefined
        this.listQuery.end = undefined
      }

      this.copy = []
      this.listQuery.orderStatusArray.forEach(element => {
        if (Number(element) === 102) {
          this.copy.push('103')
        }
        if (Number(element) === 401) {
          this.copy.push('402')
        }
      })

      this.copy = this.copy.concat(this.listQuery.orderStatusArray)

      this.getList()
    },
    // 详情
    detailsClick(row) {
      this.config.dialogVisible = true
      this.config.data = row
    }
  }
}
</script>
