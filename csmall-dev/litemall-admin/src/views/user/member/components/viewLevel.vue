<template>
  <el-dialog width="60%" :title=" titleName + username +'的团队'" :visible.sync="dialogVisible" top="2vh" @open="dialogOpen" @close="dialogClose">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="(item, index) in tabsAction" :key="index" :label="item.labels" :name="item.name" />
    </el-tabs>
    <el-table v-loading="teamLoading" :data="tableData" border>
      <el-table-column label="昵称" prop="nickName" />
      <el-table-column label="账号" prop="userName" />
      <el-table-column label="注册时间" prop="addTime" />
      <el-table-column label="推荐人昵称" prop="parentNickName" />
      <el-table-column label="推荐人账号" prop="parentUserName" />
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="teamQuery.page" :limit.sync="teamQuery.limit" @pagination="getTeamData" />
  </el-dialog>
</template>

<script>
import { subordinate } from '@/api/user/member'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'ViewLevel',
  components: { Pagination },
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
      activeName: '1',
      tabsAction: [
        {
          labels: '直属',
          name: '1'
        },
        {
          labels: '非直属',
          name: '2'
        }
      ],
      tableData: [],
      username: '',
      teamQuery: {
        userId: '',
        type: undefined,
        page: 1,
        limit: 10
      },
      total: 0,
      teamLoading: false,
      titleName: ''
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
      if (this.data.userLevel === 0) {
        this.titleName = '普通会员'
      } else {
        this.titleName = '加盟商'
      }
      this.username = this.data.username
      this.teamQuery.userId = this.data.id
      this.teamQuery.type = undefined
      this.getTeamData()
    },
    getTeamData() {
      this.teamLoading = true
      subordinate(this.teamQuery).then(res => {
        this.tableData = res.data.data.list
        this.total = res.data.data.total
        this.teamLoading = false
      }).catch(() => {
        this.tableData = []
        this.total = 0
        this.teamLoading = false
      })
    },
    dialogClose() {
      this.tableData = []
      this.activeName = '1'
    },
    handleClick(tab, event) {
      this.teamQuery.type = Number(this.activeName)
      this.teamQuery.page = 1
      this.getTeamData()
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
