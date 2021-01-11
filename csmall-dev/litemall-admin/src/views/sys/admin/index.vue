<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入管理员账号" @keyup.enter.native="handleFilter" />
      <el-button v-permission="['GET /admin/admin/list']" class="filter-item" type="primary" icon="el-icon-search" size="small" @click.native.prevent="handleFilter">查找</el-button>
    </div>

    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>数据列表</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-permission="['POST /admin/admin/create']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">添加</el-button>
          </el-col>
        </el-row>
      </div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column :index="indexMethod" align="center" label="管理员ID" type="index" width="100" />

        <el-table-column align="center" label="管理员账号" prop="username" />

        <el-table-column align="center" label="管理员头像" prop="avatar">
          <template slot-scope="scope">
            <img v-if="scope.row.avatar" :src="scope.row.avatar" width="40">
          </template>
        </el-table-column>

        <el-table-column align="center" label="管理员角色" prop="roleIds">
          <template slot-scope="scope">
            <el-tag v-for="roleId in scope.row.roleIds" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="操作" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/admin/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button v-permission="['POST /admin/admin/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
            <el-button v-permission="['POST /admin/admin/updatepswd']" type="primary" size="mini" @click="handleUpdatePsw(scope.row)">修改密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <!-- 修改密码 -->
    <update-pswd v-model="config.dialogPswVisible" :data="config.data" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="管理员账号" prop="username">
          <el-input v-model.trim="dataForm.username" />
        </el-form-item>
        <el-form-item v-if="dialogStatus=='create'" label="管理员密码" prop="password">
          <el-input v-model.trim="dataForm.password" show-password type="password" auto-complete="off" />
        </el-form-item>
        <el-form-item label="管理员头像" prop="avatar">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadAvatar"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <img v-if="dataForm.avatar" :src="dataForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="管理员角色" prop="roleIds">
          <el-select v-model="dataForm.roleIds" placeholder="请选择">
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员状态" prop="status">
          <el-select v-model="dataForm.status" placeholder="请选择" @change="statusChange">
            <el-option v-for="(item, index) in statusOption" :key="index" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-show="adminType === true" label="所属工厂" prop="plantId">
          <el-select v-model="dataForm.plantId" clearable style="width：70%" :filterable="true" placeholder="请选择" :filter-method="filterMethod">
            <el-option v-for="item in plantOption" :key="item.id" :label="item.plantName" :value="item.id" />
          </el-select>
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

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { listAdmin, createAdmin, updateAdmin, deleteAdmin } from '@/api/admin'
import { roleOptions } from '@/api/role'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { plantList } from '@/api/user/factory'

import updatePswd from './components/updatepswd'

export default {
  name: 'Admin',
  components: { Pagination, updatePswd },
  data() {
    return {
      uploadPath,
      list: null,
      total: 0,
      roleOptions: null,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      dataForm: {
        id: undefined,
        username: undefined,
        password: undefined,
        avatar: undefined,
        roleIds: '',
        status: '',
        plantId: ''
      },
      adminType: false,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      statusOption: [
        {
          value: 0,
          label: '系统管理员'
        },
        {
          value: 1,
          label: '工厂管理员'
        }
      ],
      plantOption: [],
      rules: {
        username: [
          { required: true, message: '管理员账号不能为空', trigger: 'blur' }
        ],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }, { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }],
        roleIds: [{ required: true, message: '请选择管理员角色', trigger: 'change' }],
        status: [{ required: true, message: '请选择管理员状态', trigger: 'change' }]
      },
      config: {
        dialogPswVisible: false,
        data: null
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    this.getList()

    roleOptions()
      .then(response => {
        this.roleOptions = response.data.data.list
      })

    this.getPlant()
  },
  methods: {
    // 编号自动叠加
    indexMethod(index) {
      return (this.listQuery.page - 1) * this.listQuery.limit + index + 1
    },
    formatRole(roleId) {
      for (let i = 0; i < this.roleOptions.length; i++) {
        if (roleId === this.roleOptions[i].value) {
          return this.roleOptions[i].label
        }
      }
      return ''
    },
    getList() {
      this.listLoading = true
      listAdmin(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    // 获取工厂信息
    getPlant(row) {
      const data = { limit: 20, page: 1, name: row || undefined }
      plantList(data).then(res => {
        this.plantOption = res.data.data.list
      })
    },
    filterMethod(row) {
      this.getPlant(row)
    },
    statusChange(row) {
      if (Number(row) === 1) {
        this.adminType = true
      } else {
        this.adminType = false
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        username: undefined,
        password: undefined,
        avatar: undefined,
        roleIds: '',
        status: '',
        plantId: ''
      }
    },
    uploadAvatar: function(response) {
      this.dataForm.avatar = response.data.url
    },
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
          const data = {
            avatar: this.dataForm.avatar,
            id: this.dataForm.id,
            password: this.$md5(this.dataForm.password),
            roleIds: [],
            username: this.dataForm.username,
            status: this.dataForm.status,
            plantId: this.dataForm.plantId
          }
          data.roleIds.push(this.dataForm.roleIds)
          createAdmin(data)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '添加管理员成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dataForm.roleIds = row.roleIds[0]
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
      if (Number(row.status) === 1) {
        this.adminType = true
      } else {
        this.adminType = false
      }
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          const data = {
            avatar: this.dataForm.avatar,
            id: this.dataForm.id,
            roleIds: [],
            username: this.dataForm.username,
            status: this.dataForm.status,
            plantId: this.dataForm.plantId
          }
          data.roleIds.push(this.dataForm.roleIds)
          updateAdmin(data)
            .then(() => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新管理员成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('此操作将删除该账号, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteAdmin(row)
          .then(response => {
            this.$notify.success({
              title: '成功',
              message: '删除管理员成功'
            })
            this.getList()
          })
          .catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleUpdatePsw(row) {
      this.config.dialogPswVisible = true
      this.config.data = row
    }
  }
}
</script>
