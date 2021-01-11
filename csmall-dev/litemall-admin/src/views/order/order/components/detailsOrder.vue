<template>
  <el-dialog title="查看" width="60%" top="5vh" :visible.sync="dialogVisible" @open="dialogOpen" @close="dialogClose">
    <section ref="print">
      <el-form :model="orderDetail" label-position="left" style="margin-left:20px;">
        <el-form-item label="主订单号">
          <span>{{ orderDetail.order.payOrderSn }}</span>
        </el-form-item>
        <el-form-item label="子订单号">
          <span>{{ orderDetail.order.orderSn }}</span>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-tag>{{ orderDetail.order.orderStatus | orderStatusFilter }}</el-tag>
        </el-form-item>
        <el-form-item label="订单用户">
          <span>{{ orderDetail.user }}</span>
        </el-form-item>
        <el-form-item label="用户留言">
          <span>{{ orderDetail.order.message }}</span>
        </el-form-item>
        <el-form-item label="收货信息">
          <span>（收货人）{{ orderDetail.order.consignee }}</span>
          <span>（手机号）{{ orderDetail.order.mobile }}</span>
          <span>（地址）{{ orderDetail.order.address }}</span>
        </el-form-item>
        <el-form-item label="所属工厂">
          <span>{{ orderDetail.plant }}</span>
        </el-form-item>
        <el-form-item label="商品信息">
          <el-table :data="orderDetail.orderGoods" border fit highlight-current-row>
            <el-table-column align="center" label="商品名称" prop="goodsName" />
            <!-- <el-table-column align="center" label="商品编号" prop="goodsSn" /> -->
            <el-table-column align="center" label="货品规格" prop="specifications" width="150">
              <template slot-scope="scope">
                <el-tag v-for="item in scope.row.specifications" :key="item" type="success">{{ item }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" label="货品价格" prop="price" />
            <el-table-column align="center" label="货品数量" prop="number" />
            <el-table-column align="center" label="货品总价">
              <template slot-scope="scope">
                <span>{{ scope.row.price * scope.row.number }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="优惠金额" prop="couponPrice" />
            <el-table-column align="center" label="货品图片" prop="picUrl">
              <template slot-scope="scope">
                <img :src="scope.row.picUrl" width="40">
              </template>
            </el-table-column>
            <el-table-column align="center" label="是否退款" prop="isRefund">
              <template slot-scope="scope">
                <span v-if="scope.row.isRefund === false">否</span>
                <span v-else>是</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="快递信息">
          <span>（快递公司）{{ orderDetail.order.shipChannel }}</span>
          <span>（快递单号）{{ orderDetail.order.shipSn }}</span>
          <span>（发货时间）{{ orderDetail.order.shipTime }}</span>
        </el-form-item>
        <el-form-item label="收货信息">
          <span>（确认收货时间）{{ orderDetail.order.confirm_time }}</span>
        </el-form-item>
        <el-form-item label="费用信息">
          <span>
            (实际费用){{ orderDetail.order.actualPrice }}元 =
            (商品总价){{ orderDetail.order.goodsPrice }}元 -
            (优惠金额){{ orderDetail.order.couponPrice }}元
          </span>
        </el-form-item>
        <el-form-item label="支付信息">
          <span>（支付渠道）{{ orderDetail.order.payType }}</span>
          <span>（支付时间）{{ orderDetail.order.payTime }}</span>
        </el-form-item>
      </el-form>
    </section>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="printOrder">打 印</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { orderDetail } from '@/api/order/order'

const statusMap = {
  101: '待付款',
  102: '已取消',
  103: '已取消',
  201: '待发货',
  301: '已发货',
  401: '已收货',
  402: '已收货',
  403: '已收货',
  501: '订单关闭'
}

export default {
  name: 'DetailsOrder',
  filters: {
    orderStatusFilter(status) {
      return statusMap[status]
    }
  },
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
      orderDetail: {
        order: {},
        orderGoods: []
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
    // 打开dialog之前的操作
    dialogOpen() {
      orderDetail({ id: this.data.id }).then(res => {
        this.orderDetail = res.data.data
      })
    },
    // 关闭dialog之前的操作
    dialogClose() {},
    // 打印
    printOrder() {
      this.$print(this.$refs.print)
      this.dialogVisible = false
    }
  }
}
</script>
