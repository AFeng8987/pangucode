<template>
  <div class="app-container">

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" label="ID" width="100" type="index" />

        <el-table-column align="center" label="优惠券名称" prop="name" />

        <el-table-column align="center" label="介绍" prop="desc" />

        <el-table-column align="center" label="标签" prop="tag" />

        <el-table-column align="center" label="最低消费" prop="min">
          <template slot-scope="scope">满{{ scope.row.min }}元可用</template>
        </el-table-column>

        <el-table-column align="center" label="满减金额" prop="discount">
          <template slot-scope="scope">减免{{ scope.row.discount }}元</template>
        </el-table-column>

        <el-table-column align="center" label="商品使用范围" prop="goodsType">
          <template slot-scope="scope">{{ scope.row.goodsType | formatGoodsType }}</template>
        </el-table-column>

        <el-table-column align="center" label="优惠券类型" prop="type">
          <template slot-scope="scope">{{ scope.row.type | formatType }}</template>
        </el-table-column>

        <el-table-column align="center" label="状态" prop="status">
          <template slot-scope="scope">{{ scope.row.status | formatStatus }}</template>
        </el-table-column>

        <el-table-column align="center" label="操作" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/coupon/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getInit" />
    </el-card>

    <update-coupon v-model="config.visible" :data="config.data" @submit="getInit" />
  </div>
</template>

<script>
import { couponList } from '@/api/goods/coupon'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

import updateCoupon from './components/updateCoupon'

const defaultTypeOptions = [
  {
    label: '通用领券',
    value: 0
  },
  {
    label: '注册赠券',
    value: 1
  },
  {
    label: '兑换码',
    value: 2
  }
]

export default {
  name: 'Coupon',
  components: { Pagination, updateCoupon },
  filters: {
    formatType(type) {
      for (let i = 0; i < defaultTypeOptions.length; i++) {
        if (type === defaultTypeOptions[i].value) {
          return defaultTypeOptions[i].label
        }
      }
      return ''
    },
    formatGoodsType(goodsType) {
      if (goodsType === 0) {
        return '全场通用'
      } else if (goodsType === 1) {
        return '指定分类'
      } else {
        return '指定商品'
      }
    },
    formatStatus(status) {
      if (status === 0) {
        return '正常'
      } else if (status === 1) {
        return '已过期'
      } else {
        return '已下架'
      }
    }
  },
  data() {
    return {
      listLoading: false,
      list: [],
      listQuery: {
        page: 1,
        limit: 20
      },
      total: 0,
      config: {
        visible: false,
        data: null
      }
    }
  },
  created() {
    this.getInit()
  },
  methods: {
    getInit() {
      couponList(this.listQuery).then(res => {
        this.list = res.list
        this.total = res.total
      })
    },
    // 修改减免金额
    handleUpdate(row) {
      this.config.visible = true
      this.config.data = row
    }
  }
}
</script>
