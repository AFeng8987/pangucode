<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.goodsCode" clearable class="filter-item" style="width: 200px;" placeholder="请输入编码" />
      <el-input v-model="listQuery.codeName" clearable class="filter-item" style="width: 200px;" placeholder="请输入编码名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/code/create']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">新增编码</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column :index="indexMethod" align="center" label="编号" width="100" type="index" />
        <el-table-column align="center" label="编码" prop="goodsCode" />
        <el-table-column align="center" label="编码名称" prop="codeName" />
        <el-table-column align="center" label="规格编码维护">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/code/read']" class="filter-item" type="primary" size="mini" @click="handleMaintain(scope.row)">编辑与查看</el-button>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/code/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/code/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 新增和编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="150px" style="width: 400px; margin-left:50px;">
        <el-form-item label="商品编码" prop="goodsCode">
          <el-input v-model="dataForm.goodsCode" />
        </el-form-item>
        <el-form-item label="商品编码名称" prop="codeName">
          <el-input v-model="dataForm.codeName" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { codeList, codeCreate, codeUpdate, codeDelete } from '@/api/stock/code'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Code',
  components: { Pagination },
  data() {
    return {
      listQuery: {
        goodsCode: undefined,
        codeName: undefined,
        limit: 20,
        page: 1
      },
      total: 0,
      listLoading: false,
      list: [],
      dataForm: {
        id: undefined,
        goodsCode: undefined,
        codeName: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑商品编码',
        create: '新增商品编码'
      },
      rules: {
        goodsCode: [{ required: true, message: '商品编码不能为空', trigger: 'blur' }],
        codeName: [{ required: true, message: '商品编码名称不能为空', trigger: 'blur' }, { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      codeList(this.listQuery).then(res => {
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
      this.getList()
    },
    // 编号自动叠加
    indexMethod(index) {
      return (this.listQuery.page - 1) * this.listQuery.limit + index + 1
    },
    // 新增编码
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          codeCreate(this.dataForm).then(res => {
            this.list.unshift(res.data.data)
            this.dialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '添加成功'
            })
          }).catch(err => {
            this.$notify.error({
              title: '失败',
              message: err.data.errmsg
            })
          })
        }
      })
    },
    // 编辑
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          codeUpdate(this.dataForm).then(res => {
            for (const v of this.list) {
              if (v.id === res.data.data.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, res.data.data)
                break
              }
            }
            this.dialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
          }).catch(err => {
            this.$notify.error({
              title: '失败',
              message: err.data.errmsg
            })
          })
        }
      })
    },
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将删除该编码, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        codeDelete(row).then(res => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        }).catch(err => {
          this.$notify.error({
            title: '失败',
            message: err.data.errmsg
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 规格编码维护
    handleMaintain(row) {
      this.$router.push({ path: '/stock/maintain', query: { id: row.id }})
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        goodsCode: undefined,
        codeName: undefined
      }
    }
  }
}
</script>
