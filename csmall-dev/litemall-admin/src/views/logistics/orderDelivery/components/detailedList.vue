<template>
  <el-dialog title="发货清单" top="3vh" :visible.sync="dialogListVisible" @open="dialogOpen" @close="dialogClose">
    <section style="margin-top:-35px;margin-left: 10px;">
      <el-form ref="prints" :model="list" label-position="left">
        <el-row>
          <el-col :span="12">
            <el-form-item label="主订单号">
              <span>{{ list.order.payOrderSn }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="子订单号">
              <span>{{ list.order.orderSn }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单用户">
              <span>{{ list.user }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="this.$store.getters.status === 0" label="工厂名称">
              <span>{{ plantName }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="用户留言">
          <span>{{ list.order.message }}</span>
        </el-form-item>

        <el-form-item label="收货信息">
          <span>收货人：{{ list.order.consignee }}</span>&nbsp;&nbsp;
          <span>手机号：{{ list.order.mobile }}</span><br>
          <span style="margin-left:68px">地址：{{ list.order.address }}</span>
        </el-form-item>
        <el-form-item label="商品信息">
          <el-table :data="list.orderGoods" border fit highlight-current-row>
            <el-table-column align="center" label="商品名称" prop="goodsName">
              <template slot-scope="scope">
                <span v-if="scope.row.is_refund === false">{{ scope.row.goodsName }}</span>
                <s v-else style="color:#909399">{{ scope.row.goodsName }}</s>
              </template>
            </el-table-column>
            <el-table-column align="center" label="规格" prop="specifications">
              <template slot-scope="scopes">
                <span v-if="scopes.row.is_refund === false">{{ scopes.row.specifications }}</span>
                <s v-else style="color:#909399">{{ scopes.row.specifications }}</s>
                <!-- <span>{{ scopes.row.specifications }}</span> -->
              </template>
            </el-table-column>
            <el-table-column align="center" label="数量" prop="number">
              <template slot-scope="scope">
                <span v-if="scope.row.is_refund === false">{{ scope.row.number }}</span>
                <s v-else style="color:#909399">{{ scope.row.number }}</s>
              </template>
            </el-table-column>
            <el-table-column align="center" label="编码名称" prop="goodCodeName">
              <template slot-scope="scope">
                <span v-if="scope.row.is_refund === false">{{ scope.row.goodCodeName }}</span>
                <s v-else style="color:#909399">{{ scope.row.goodCodeName }}</s>
              </template>
            </el-table-column>
            <el-table-column align="center" label="商品编码" prop="goodsCode">
              <template slot-scope="scope">
                <span v-if="scope.row.is_refund === false">{{ scope.row.goodsCode }}</span>
                <s v-else style="color:#909399">{{ scope.row.goodsCode }}</s>
              </template>
            </el-table-column>
            <el-table-column align="center" label="规格编码" prop="specsCode">
              <template slot-scope="scope">
                <span v-if="scope.row.is_refund === false">{{ scope.row.specsCode }}</span>
                <s v-else style="color:#909399">{{ scope.row.specsCode }}</s>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item v-if="!list.order.deliveryStatus" label="发货信息" style="margin-top:20px">
          <el-row :gutter="20">
            <el-col :span="5">
              <el-select v-model="courierType" size="mini" placeholder="请选择快递类型" @change="typeChange">
                <el-option label="顺丰" value="SF" />
                <el-option label="其他" value="QT" />
              </el-select>
            </el-col>
            <el-col v-if="typeStatus" :span="5">
              <el-input v-model.trim="courierNumber" size="mini" clearable placeholder="请输入快递单号" @change="identifyClick" />
            </el-col>
            <el-col v-if="typeStatus && deliveryStatus" :span="5">
              <el-select v-model="courierCompany" size="mini" placeholder="请选择快递公司">
                <el-option v-for="vv in courierList" :key="vv.ShipperCode" :label="vv.ShipperName" :value="vv.ShipperName" />
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item v-else label="快递信息" style="margin-top:20px">
          <span>（快递公司）{{ list.order.shipChannel }}</span>
          <span>（快递单号）{{ list.order.shipSn }}</span>
          <span>（发货时间）{{ list.order.shipTime }}</span>
        </el-form-item>
      </el-form>
    </section>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取 消</el-button>
      <el-button type="success" @click="printOrder">打 印</el-button>
      <el-button v-if="!list.order.deliveryStatus" type="primary" @click="handleConfirmClick">发货</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { goodsDetail, deliveryShip, courierDelivery } from '@/api/logistics/orderDelivery'

import courier from './courier.json'

export default {
  name: 'DetailedList',
  props: {
    value: Boolean,
    data: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      dialogListVisible: false,
      list: {
        order: {},
        orderGoods: []
      },
      plantName: '',
      courier,
      courierType: '',
      courierNumber: '',
      courierCompany: '',
      typeStatus: false,
      deliveryStatus: false,
      courierList: []
    }
  },
  computed: {
    // 获取快递公司名称
    getShipChannel() {
      let shipChannel = ''
      this.courier.forEach(element => {
        if (element.code === this.list.order.shipChannel) {
          shipChannel = element.name
        }
      })
      return shipChannel
    }

  },
  watch: {
    value(val) {
      this.dialogListVisible = val
    },
    dialogListVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    // 打开dialog时的操作
    dialogOpen() {
      this.getgoodsDetail()
      this.plantName = this.data.plantName
    },
    getgoodsDetail() {
      goodsDetail({ id: Number(this.data.orderId) }).then(res => {
        this.list = res.data.data
      })
    },
    // 关闭dialog时的操作
    dialogClose() {
      this.dialogListVisible = false
      this.list = {
        order: {},
        orderGoods: []
      }
      this.plantName = ''
      this.shipChannel = ''
      this.shipSn = ''
      this.courierType = ''
      this.courierNumber = ''
      this.courierCompany = ''
      this.typeStatus = false
      this.deliveryStatus = false
      this.courierList = []
      this.$emit('submit')
    },
    // 打印
    printOrder() {
      this.$print(this.$refs.prints)
      this.dialogListVisible = false
    },
    // 发货
    handleConfirmClick() {
      const data = this.validationData()
      if (data) {
        this.$confirm('发货, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {
          deliveryShip(data).then(res => {
            this.$notify.success({
              title: '成功',
              message: '发货成功'
            })
            this.getgoodsDetail()
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
    },
    validationData() {
      if (!this.typeStatus) {
        if (this.courierType) {
          return { orderId: this.list.order.id, shipChannel: '顺丰快递' }
        } else {
          this.$message({
            message: '请选择发货快递',
            type: 'warning'
          })
          return false
        }
      } else {
        if (this.courierNumber && this.courierCompany) {
          return { orderId: this.list.order.id, shipChannel: this.courierCompany, shipSn: this.courierNumber }
        } else {
          this.$message({
            message: '请输入快递单号或者未选择快递公司',
            type: 'warning'
          })
          return false
        }
      }
    },
    typeChange(res) {
      if (res === 'SF') {
        this.typeStatus = false
      } else {
        this.typeStatus = true
      }
    },
    identifyClick() {
      if (this.courierNumber) {
        courierDelivery(this.courierNumber).then(res => {
          this.courierList = []
          this.courierCompany = ''
          const shipData = res.data.data.Shippers
          if (shipData.length === 1) {
            this.courierList = shipData
            this.courierCompany = shipData[0].ShipperName
          } else {
            this.courierList = shipData
          }
          this.deliveryStatus = true
        })
      } else {
        this.$message({
          message: '请输入快递单号',
          type: 'warning'
        })
      }
    }
  }
}
</script>

<style scoped>
.el-form-item{
  margin: 0;
}
</style>
