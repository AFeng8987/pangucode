<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>基本信息</span>
      </div>

      <el-table :data="basicInformation" border>
        <el-table-column align="center" label="主订单号" prop="payOrderSn" />
        <el-table-column align="center" label="子订单号" prop="orderSn" />
        <el-table-column align="center" label="售后编号" prop="aftersaleSn" />
        <el-table-column align="center" label="工厂名称" prop="plantName" />
        <el-table-column align="center" label="支付方式" prop="payType" />
        <el-table-column align="center" label="支付订单号" prop="payId" />
        <el-table-column align="center" label="下单时间" prop="addTime" />
        <el-table-column align="center" label="付款时间" prop="payTime" />
        <el-table-column align="center" label="订单状态">
          <template slot-scope="scope">
            <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 10px">
      <div slot="header" class="clearfix">
        <span>收货人信息</span>
      </div>

      <el-table :data="receivingInformation" border>
        <el-table-column align="center" label="收货人" prop="consignee" />
        <el-table-column align="center" label="手机号" prop="mobile" />
        <el-table-column align="center" label="收货地址" prop="address" />
      </el-table>
    </el-card>

    <el-card style="margin-top: 10px">
      <div slot="header" class="clearfix">
        <span>退货信息</span>
      </div>

      <el-table :data="refundInformation" border>
        <el-table-column align="center" label="类型">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.serviceType === 1">仅退款</el-tag>
            <el-tag v-if="scope.row.serviceType === 2">退货退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="退款状态">
          <template slot-scope="scope">
            <span>{{ scope.row.status | orderStatusText }}</span><br>
          </template>
        </el-table-column>
        <el-table-column align="center" label="退款原因" prop="reason" />
        <el-table-column align="center" label="退款说明" prop="comment" />
        <el-table-column align="center" label="退款金额" prop="amount" />
        <el-table-column align="center" label="退款凭证" prop="pictures">
          <template slot-scope="scope">
            <el-popover v-for="item in scope.row.pictures" :key="item" placement="left">
              <el-image slot="reference" style="width: 50px; height: 50px" :src="item" />
              <el-image style="width: 600px; height: 600px" :src="item" />
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="serviceType === 2" style="margin-top: 10px">
      <div slot="header" class="clearfix">
        <span>退货快递信息</span>
      </div>

      <el-table :data="courierInformation" border>
        <el-table-column align="center" label="快递公司" prop="courierCompany" />
        <el-table-column align="center" label="快递单号" prop="courierNumber" />
      </el-table>
    </el-card>

    <el-card style="margin-top: 10px">
      <div slot="header" class="clearfix">
        <span>商品信息</span>
      </div>

      <el-table :data="orderGoodsData" border>
        <el-table-column align="center" label="编号" prop="goodsId" />
        <el-table-column align="center" label="商品名称" prop="goodsName" />
        <el-table-column align="center" label="商品图片">
          <template slot-scope="scope">
            <el-image style="width: 70px; height: 70px" :src="scope.row.picUrl" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="规格">
          <template slot-scope="scope">
            <el-tag v-for="item in scope.row.specifications" :key="item">{{ item }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="数量" prop="number" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { afterSaleDetail } from '@/api/order/refund'

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
  1: '后台允许退货',
  2: '后台驳回申请',
  3: '后台确认收货通过审核(退款中)',
  4: '后台审核驳回',
  5: '退款成功',
  6: '用户取消',
  // 10: '待发货申请',
  11: '待收货申请',
  12: '已完成申请'
}

export default {
  name: 'RefundDetails',
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
      statusMap,
      statusText,
      serviceType: 1,
      basicInformation: [],
      receivingInformation: [],
      refundInformation: [],
      orderGoodsData: [],
      courierInformation: []
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      if (!this.$route.query.id) {
        this.$message.error('携带的订单id为空，即将为你返回至退货审核列表')
        this.$router.push({ path: '/order/refund' })
      }
      afterSaleDetail(this.$route.query.id).then(res => {
        const data = res.data.data
        this.serviceType = data.apply.serviceType
        this.handleData(data)
      })
    },
    handleData(row) {
      this.basicInformation = []
      this.refundInformation = []
      this.receivingInformation = []
      this.orderGoodsData = []
      this.courierInformation = []
      const basic = {
        payOrderSn: row.order.payOrderSn,
        orderSn: row.order.orderSn,
        aftersaleSn: row.apply.aftersaleSn,
        plantName: row.plant.plantName,
        payType: row.order.payType,
        payId: row.order.payId,
        addTime: row.order.addTime,
        payTime: row.order.payTime,
        orderStatus: row.order.orderStatus
      }
      this.basicInformation.push(basic)
      this.receivingInformation.push(row.order)
      const refund = {
        serviceType: row.apply.serviceType,
        status: row.apply.status,
        reason: row.apply.reason,
        amount: row.apply.amount,
        pictures: row.apply.pictures,
        comment: row.apply.comment
      }
      this.refundInformation.push(refund)
      this.orderGoodsData.push(row.orderGood)
      if (row.apply.courierCompany || row.apply.courierNumber) {
        const courier = {
          courierCompany: row.apply.courierCompany,
          courierNumber: row.apply.courierNumber
        }
        this.courierInformation.push(courier)
      }
    }
  }
}
</script>
