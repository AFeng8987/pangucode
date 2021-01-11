<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.afterSaleSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入售后编号" />
      <el-input v-model="listQuery.payOrderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入主订单号" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入子订单号" />
      <el-input v-model="listQuery.payId" clearable class="filter-item" style="width: 160px;" placeholder="请输入支付订单号" />
      <el-date-picker v-model="listQuery.timeArray" type="daterange" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.serviceType" placeholder="请选择退款类型" clearable class="filter-item">
        <el-option v-for="(item, index) in serviceTypeOption" :key="index" :label="item.label" :value="item.value" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="8">
            <span>数据列表</span>
          </el-col>
          <el-col :span="16" style="text-align: right">
            <el-button v-show="processedType" v-permission="['POST /admin/afterSale/batch-returnGoodAudit']" type="warning" size="mini" @click="batchRefundGoods">批量允许退货</el-button>
            <el-button v-show="processedType" v-permission="['POST /admin/afterSale/batch-returnGoodAudit']" style="margin-top:5px" type="danger" size="mini" @click="batchBeRejected">批量驳回申请</el-button>

            <el-button v-show="auditType" v-permission="['POST /admin/afterSale/batch-returnAudit']" type="success" size="mini" @click="batchAudit">批量通过审核</el-button>
            <el-button v-show="auditType" v-permission="['POST /admin/afterSale/batch-returnAudit']" style="margin-top:5px" type="danger" size="mini" @click="batchRejectedAudit">批量驳回审核</el-button>

          </el-col>
        </el-row>
      </div>

      <el-tabs v-model="activeName" type="border-card" @tab-click="handleActiveClick">
        <el-tab-pane v-for="item in refundStatusOption" :key="item.value" :name="item.value">
          <span slot="label">
            <span>{{ item.label }}</span>
            <el-badge :value="badge[item.badge]" :max="99" class="item" />
          </span>
          <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="tableChange">
            <el-table-column align="center" :selectable="selectable" type="selection" width="55" />
            <el-table-column align="center" label="用户" prop="userName" />
            <el-table-column align="center" label="售后编号" prop="afterSaleSn" />
            <el-table-column align="center" label="主订单号" prop="payOrderSn" />
            <el-table-column align="center" label="子订单号" prop="orderSn" />
            <el-table-column align="center" label="工厂名称" prop="plantName" />
            <el-table-column align="center" label="商品名称" prop="goodsName" />
            <el-table-column align="center" label="订单状态" prop="orderStatus">
              <template slot-scope="scope">
                <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="支付类型" prop="payType" />
            <el-table-column align="center" label="支付订单号" prop="payId" />
            <el-table-column align="center" label="退款金额" prop="amount" />
            <el-table-column align="center" label="类型" prop="serviceType">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.serviceType === 1">仅退款</el-tag>
                <el-tag v-if="scope.row.serviceType === 2">退货退款</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="退款状态" prop="status">
              <template slot-scope="scope">
                <span>{{ scope.row.status | orderStatusText }}</span><br>
                <span v-if="scope.row.status === 2 || scope.row.status === 4" style="color:red">原因：{{ scope.row.rejectReason }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="是否确认收货" prop="confirmReturn">
              <template slot-scope="scope">
                <span v-if="scope.row.confirmReturn">是</span>
                <span v-else>否</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="时间" prop="addTime" width="100" />
            <el-table-column align="center" label="详情">
              <template slot-scope="scope">
                <el-button type="success" size="mini" @click="handleDetails(scope.row)">详情</el-button><br>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="130">
              <template slot-scope="scope">
                <span v-if="getJudgeData(scope.row) === 1" v-permission="['POST /admin/afterSale/returnGoodAudit']">
                  <el-button type="warning" size="mini" @click="handleRefundGoods(scope.row)">允许退货</el-button><br>
                  <el-button style="margin-top:5px" type="danger" size="mini" @click="handleBeRejected(scope.row)">驳回申请</el-button>
                </span>
                <span v-if="getJudgeData(scope.row) === 2" v-permission="['POST /admin/afterSale/returnAudit']">
                  <el-button type="success" size="mini" @click="handleAudit(scope.row)">通过审核</el-button><br>
                  <el-button style="margin-top:5px" type="danger" size="mini" @click="handleRejectedAudit(scope.row)">驳回审核</el-button>
                </span>
                <span v-if="getJudgeData(scope.row) === 5">
                  <el-button v-permission="['POST /admin/afterSale/confirm']" type="primary" size="mini" @click="handleConfirm(scope.row)">确认收到退货</el-button><br>
                  <el-button v-permission="['POST /admin/afterSale/returnAudit']" style="margin-top:5px" type="danger" size="mini" @click="handleRejectedAudit(scope.row)">驳回审核</el-button>
                </span>
                <span v-if="getJudgeData(scope.row) === 3">
                  <el-button type="info" size="mini" disabled>已操作</el-button>
                </span>
                <el-button v-if="getJudgeData(scope.row) === 4" v-permission="['POST /admin/afterSale/updateStatus']" size="mini" type="primary" @click="manualClick(scope.row)">手动退款</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>
  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { afterSaleList, returnAudit, batchReturnAudit, returnGoodAudit, batchReturnGoodAudit, afterSaleUpdateStatus, afterSaleConfirm } from '@/api/order/refund'

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

const statusText = {
  0: '未申请',
  1: '退货审核通过',
  2: '退货审核驳回',
  3: '退款中',
  4: '退款驳回',
  5: '退款成功',
  6: '退款失败',
  7: '用户取消',
  11: '待收货申请',
  12: '已完成申请'
}

export default {
  name: 'Refund',
  components: { Pagination },
  filters: {
    orderStatusFilter(status) {
      if (status === 103) {
        return statusMap[102]
      }
      if (status === 402 || status === 403) {
        return statusMap[401]
      }

      return statusMap[status]
    },
    orderStatusText(status) {
      return statusText[status]
    }
  },
  data() {
    return {
      statusText,
      statusMap,
      listQuery: {
        afterSaleSn: undefined,
        orderSn: undefined,
        payOrderSn: undefined,
        payId: undefined,
        refundStatus: undefined,
        serviceType: undefined,
        start: undefined,
        end: undefined,
        timeArray: [],
        limit: 20,
        page: 1
      },
      listLoading: false,
      list: [],
      total: 0,
      activeName: '-1',
      badge: {
        applyTotal: '',
        allowReturnTotal: '',
        exportTotal: ''
      },
      processedType: false,
      auditType: false,
      selectData: [],
      refundStatusOption: [
        {
          value: '-1',
          label: '全部审核'
        },
        {
          value: '0',
          label: '退货待审核',
          badge: 'applyTotal'
        },
        {
          value: '1',
          label: '退款待审核',
          badge: 'allowReturnTotal'
        },
        {
          value: '2',
          label: '审核驳回'
        },
        {
          value: '3',
          label: '审核通过',
          badge: 'exportTotal'
        },
        {
          value: '4',
          label: '退款成功'
        },
        {
          value: '5',
          label: '退款失败'
        }
      ],
      serviceTypeOption: [
        {
          value: 1,
          label: '仅退款'
        },
        {
          value: 2,
          label: '退货退款'
        }
      ],
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
        afterSaleSn: this.listQuery.afterSaleSn ? this.listQuery.afterSaleSn : undefined,
        orderSn: this.listQuery.orderSn ? this.listQuery.orderSn : undefined,
        payOrderSn: this.listQuery.payOrderSn ? this.listQuery.payOrderSn : undefined,
        payId: this.listQuery.payId ? this.listQuery.payId : undefined,
        refundStatus: this.listQuery.refundStatus,
        serviceType: this.listQuery.serviceType ? this.listQuery.serviceType : undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取数据
    getList() {
      this.listLoading = true
      afterSaleList(this._params).then(res => {
        this.listLoading = false
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.badge.applyTotal = res.data.data.applyTotal
        this.badge.allowReturnTotal = res.data.data.allowReturnTotal
        this.badge.exportTotal = res.data.data.exportTotal
      }).catch(() => {
        this.listLoading = false
        this.list = []
        this.total = 0
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
    tableKey(row) {
      return row.id
    },
    // tabs切换
    handleActiveClick(tab) {
      if (Number(tab.name) === -1) {
        this.listQuery.refundStatus = undefined
        this.processedType = false
        this.auditType = false
      } else {
        this.listQuery.refundStatus = Number(tab.name)
        switch (Number(tab.name)) {
          case 0:
            this.processedType = true
            this.auditType = false
            break
          case 1:
            this.auditType = true
            this.processedType = false
            break
          case 3:
            this.processedType = false
            this.auditType = false
            break
          default:
            this.processedType = false
            this.auditType = false
        }
      }

      this.getList()
    },
    getJudgeData(row) {
      if (row.status === 11 || row.status === 12) {
        return 1
      } else if (row.status === 1) {
        if (row.serviceType === 2) {
          if (row.confirmReturn) {
            return 2
          } else {
            return 5
          }
        }
        return 2
      } else if (row.status === 6) {
        return 4
      } else {
        return 3
      }
    },
    // 详情
    handleDetails(row) {
      this.$router.push({ path: '/order/refundDetails', query: { id: row.id }})
    },
    // 选中的数据
    tableChange(row) {
      this.selectData = []
      row.forEach(element => {
        this.selectData.push(element.id)
      })
    },
    selectable(row) {
      if (this.listQuery.refundStatus === 1) {
        if (row.serviceType === 2) {
          if (row.confirmReturn) {
            return true
          } else {
            return false
          }
        }
      }
      return true
    },
    // 允许退货
    handleRefundGoods(row) {
      this.$confirm('此操作将允许退货, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true

            const data = {
              id: row.id,
              status: 1
            }
            returnGoodAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
              this.getList()
              done()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    // 驳回申请
    handleBeRejected(row) {
      this.$prompt('请输入驳回原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '不能为空',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              id: row.id,
              status: 2,
              rejectReason: instance.inputValue
            }

            returnGoodAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '驳回成功'
              })
              done()
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消'
        })
      })
    },
    // 批量允许退货
    batchRefundGoods() {
      if (this.selectData.length === 0) {
        this.$message({
          message: '您还未选择订单',
          type: 'warning'
        })
        return
      }
      this.$confirm('此操作将允许退货, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true

            const data = {
              ids: this.selectData,
              status: 1
            }
            batchReturnGoodAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '更新成功',
                duration: 1500
              })
              this.getList()
              done()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    // 批量驳回申请
    batchBeRejected() {
      if (this.selectData.length === 0) {
        this.$message({
          message: '您还未选择订单',
          type: 'warning'
        })
        return
      }
      this.$prompt('请输入驳回原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '不能为空',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              ids: this.selectData,
              status: 2,
              rejectReason: instance.inputValue
            }

            batchReturnGoodAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '驳回成功',
                duration: 1500
              })
              done()
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消'
        })
      })
    },
    // 通过审核
    handleAudit(row) {
      this.$confirm('此操作将通过审核, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              id: row.id,
              status: 3
            }
            returnAudit(data).then(res => {
              instance.confirmButtonLoading = false
              done()
              this.$notify.success({
                title: '成功',
                message: '更新成功',
                duration: 1500
              })
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              done()
              if (err.data.errno !== 502) {
                this.$notify.error({
                  title: '失败',
                  message: err.data.errmsg,
                  duration: 3000
                })
              }
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    // 驳回审核
    handleRejectedAudit(row) {
      this.$prompt('请输入驳回原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '不能为空',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              id: row.id,
              status: 4,
              rejectReason: instance.inputValue
            }

            returnAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '驳回成功',
                duration: 1500
              })
              done()
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消'
        })
      })
    },
    // 批量通过审核
    batchAudit() {
      if (this.selectData.length === 0) {
        this.$message({
          message: '您还未选择订单',
          type: 'warning'
        })
        return
      }
      this.$confirm('此操作将通过审核, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              ids: this.selectData,
              status: 3
            }
            batchReturnAudit(data).then(res => {
              instance.confirmButtonLoading = false
              done()
              this.$notify.success({
                title: '成功',
                message: '更新成功',
                duration: 1500
              })
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    // 批量驳回审核
    batchRejectedAudit() {
      if (this.selectData.length === 0) {
        this.$message({
          message: '您还未选择订单',
          type: 'warning'
        })
        return
      }
      this.$prompt('请输入驳回原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '不能为空',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            const data = {
              ids: this.selectData,
              status: 4,
              rejectReason: instance.inputValue
            }

            batchReturnAudit(data).then(res => {
              instance.confirmButtonLoading = false
              this.$notify.success({
                title: '成功',
                message: '驳回成功',
                duration: 1500
              })
              done()
              this.getList()
            }).catch(err => {
              instance.confirmButtonLoading = false
              this.$notify.error({
                title: '失败',
                message: err.data.errmsg,
                duration: 1500
              })
            })
          } else {
            done()
          }
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消'
        })
      })
    },
    // 手动退款
    manualClick(obj) {
      this.$confirm('此操作确定手动退款, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        afterSaleUpdateStatus(obj.afterSaleSn).then(res => {
          this.$notify.success({
            title: '成功',
            message: '操作成功',
            duration: 1500
          })
          this.getList()
        }).catch(err => {
          this.$notify.error({
            title: '失败',
            message: err.data.errmsg,
            duration: 1500
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    // 确认收到退货
    handleConfirm(row) {
      this.$confirm('此操作将确认收到退货，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        afterSaleConfirm({ afterSaleSn: row.afterSaleSn }).then(res => {
          this.$notify.success({
            title: '成功',
            message: '操作成功',
            duration: 1500
          })
          this.getList()
        }).catch(err => {
          this.$notify.error({
            title: '失败',
            message: err.data.errmsg,
            duration: 1500
          })
        })
      })
    }
  }
}
</script>
