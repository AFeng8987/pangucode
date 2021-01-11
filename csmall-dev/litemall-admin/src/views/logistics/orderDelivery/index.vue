<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 160px;" placeholder="请输入收货人" />
      <el-input v-model="listQuery.payOrderSn" clearable class="filter-item" style="width: 260px;" placeholder="请输入主订单号" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 260px;" placeholder="请输入子订单号" />
      <el-date-picker v-model="listQuery.timeArray" type="daterange" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-if="status === 0" v-model="listQuery.plantId" placeholder="请选择工厂" class="filter-item" clearable>
        <el-option v-for="item in plantOption" :key="item.id" :label="item.plantName" :value="item.id" />
      </el-select>

      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>

      <el-tabs v-model="activeName" type="border-card" @tab-click="handleActiveClick">
        <el-tab-pane v-for="item in deliveryStatusOptions" :key="item.value" :name="item.value">
          <span slot="label">
            <span>{{ item.label }}</span>
            <el-badge :value="badge[item.badge]" :max="99" class="item" />
          </span>
          <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
            <el-table-column align="center" label="主订单号" prop="payOrderSn" />
            <el-table-column align="center" label="子订单号" prop="orderSn" />
            <el-table-column v-if="status === 0" align="center" label="工厂名称" prop="plantName" />
            <el-table-column align="center" label="收货人" prop="consignee" />
            <el-table-column align="center" label="收货地址" prop="address" />
            <el-table-column align="center" label="物流单号" prop="reminderShipment">
              <template slot-scope="scope">
                <span v-if="judgeLogistics(scope.row) === false" style="color: rgb(255, 153, 0);font-weight: bold;">提醒发货</span>
                <span v-else>{{ judgeLogistics(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发货状态" prop="deliveryStatus">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.deliveryStatus === true">已发货</el-tag>
                <el-tag v-else>未发货</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="订单状态" prop="orderStatus">
              <template slot-scope="scope">
                <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="下单时间" prop="addTime" width="100" />
            <el-table-column align="center" label="发货时间" prop="shipTime" width="100" />
            <!-- <el-table-column align="center" label="备注" prop="message" /> -->
            <el-table-column align="center" label="操作">
              <template slot-scope="scope">
                <el-button v-permission="['GET /admin/delivery/goodsDetail']" size="mini" type="success" @click="deliveryListClick(scope.row)">发货/清单</el-button><br>
              </template>
            </el-table-column>

          </el-table>
        </el-tab-pane>
      </el-tabs>

      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 发货/清单 -->
    <detailed-list v-model="deliveryConfig.dialogVisible" :data="deliveryConfig.data" @submit="getList" />

  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import { deliveryList } from '@/api/logistics/orderDelivery'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { plantList } from '@/api/user/factory'
import detailedList from './components/detailedList'

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
  name: 'OrderDelivery',
  components: { Pagination, detailedList },
  filters: {
    orderStatusFilter(status) {
      if (status === 103) {
        return statusMap[102]
      }
      if (status === 402) {
        return statusMap[401]
      }

      return statusMap[status]
    }
  },
  data() {
    return {
      statusMap,
      listQuery: {
        consignee: undefined,
        deliveryStatus: undefined,
        orderSn: undefined,
        plantId: undefined,
        payOrderSn: undefined,
        timeArray: [],
        start: undefined,
        end: undefined,
        page: 1,
        limit: 10
      },
      activeName: '',
      listLoading: false,
      list: [],
      total: 0,
      badge: {
        countDelivery: ''
      },
      deliveryStatusOptions: [
        {
          value: '',
          label: '全部订单'
        },
        {
          value: '1',
          label: '已发货订单'
        },
        {
          value: '2',
          label: '待发货订单',
          badge: 'countDelivery'
        }
      ],
      deliveryConfig: {
        dialogVisible: false,
        data: null
      },
      status: '',
      plantOption: [],
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
        deliveryStatus: this.listQuery.deliveryStatus,
        plantId: this.listQuery.plantId ? this.listQuery.plantId : undefined
      }
    }
  },
  created() {
    this.getList()
    this.status = this.$store.getters.status
    if (Number(this.status) === 0) {
      this.getPlant()
    }
  },
  methods: {
    // 获取订单list
    getList() {
      this.listLoading = true
      deliveryList(this._params).then(res => {
        this.badge.countDelivery = res.data.data.countDelivery
        this.listLoading = false
        this.list = res.data.data.list
        this.total = res.data.data.total
      }).catch(() => {
        this.listLoading = false
        this.list = []
        this.total = 0
      })
    },
    // 获取工厂信息
    getPlant(row) {
      const data = { limit: 20, page: 1, name: row || undefined }
      plantList(data).then(res => {
        this.plantOption = res.data.data.list
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

      this.getList()
    },
    // tabs切换
    handleActiveClick(tab) {
      if (Number(tab.name) === 1) {
        this.listQuery.deliveryStatus = true
      } else if (Number(tab.name) === 2) {
        this.listQuery.deliveryStatus = false
      } else {
        this.listQuery.deliveryStatus = undefined
      }
      this.getList()
    },
    // 判断是否有物流单号
    judgeLogistics(row) {
      if (row.shipSn) {
        return row.shipSn
      } else {
        if (row.reminderShipment === false) {
          return '未收到提醒发货'
        } else {
          return false
        }
      }
    },
    // 发货清单
    deliveryListClick(row) {
      this.deliveryConfig.dialogVisible = true
      this.deliveryConfig.data = row
    }
  }
}
</script>
