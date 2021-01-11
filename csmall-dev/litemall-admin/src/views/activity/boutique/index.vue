<template>
  <div class="app-container">
    <el-card :body-style="{ padding: '0px' }">
      <div slot="header" class="clearfix">
        <span>数据列表</span>
      </div>

      <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" label="编号" prop="id" />
        <el-table-column align="center" label="模块图片" prop="picUrl">
          <template slot-scope="scope">
            <el-image style="width: 50px; height: 50px" :src="scope.row.picUrl" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="模块名称" prop="activityName" />
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/activity/activityUpdate']" type="primary" @click="handleEdit(scope.row)">编辑活动信息</el-button><br>
            <el-button v-permission="['GET /admin/activity/activityGoods/{activityId}']" style="margin-top: 5px" type="success" @click="handleDelete(scope.row)">编辑活动商品</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
    </el-card>

    <el-dialog title="编辑活动版块" :visible.sync="dialogVisible" @close="dialogClose">
      <el-form ref="activityForms" :model="activityForm" :rules="rules" status-icon label-position="left" label-width="150px" style="margin-left:50px;">
        <el-form-item label="活动板块名称" prop="activityName">
          <el-row>
            <el-col :span="14">
              <el-input v-model.trim="activityForm.activityName" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="活动板块图标" prop="picUrl">
          <el-upload class="avatar-uploader" :show-file-list="false" :action="uploadPath" :on-success="uploadAvatar" :headers="headers" accept=".jpg,.jpeg,.png,.gif">
            <img v-if="activityForm.picUrl" :src="activityForm.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogClose">取消</el-button>
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <edit-active-goods v-model="config.dialogVisible" :data="config.data" />
  </div>
</template>

<style scoped>
.avatar-uploader /deep/ .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader /deep/ .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<script>
import { activityList, activityUpdate } from '@/api/activity/boutique'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import editActiveGoods from './components/editActiveGoods'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Boutique',
  components: { editActiveGoods, Pagination },
  data() {
    return {
      uploadPath,
      listQuery: {
        limit: 20,
        page: 1
      },
      list: [],
      listLoading: false,
      total: 0,
      dialogVisible: false,
      activityForm: {
        activityName: undefined,
        id: undefined,
        picUrl: undefined
      },
      rules: {
        activityName: [{ required: true, message: '活动板块名称不能为空', trigger: 'blur' },
          { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' }],
        picUrl: [{ required: true, message: '活动板块图标不能为空', trigger: 'blur' }]
      },
      config: {
        dialogVisible: false,
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
  },
  methods: {
    getList() {
      this.listLoading = true
      activityList(this.listQuery).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    // 编辑活动商品
    handleDelete(row) {
      this.config.dialogVisible = true
      this.config.data = {
        id: row.id,
        activityName: row.activityName
      }
    },
    // 编辑活动信息
    handleEdit(row) {
      this.dialogVisible = true
      this.activityForm = {
        activityName: row.activityName,
        id: row.id,
        picUrl: row.picUrl
      }
    },
    uploadAvatar(res) {
      this.activityForm.picUrl = res.data.url
    },
    updateData() {
      this.$refs['activityForms'].validate(valid => {
        if (valid) {
          activityUpdate(this.activityForm).then(res => {
            this.dialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
            this.getList()
          }).catch(err => {
            this.$notify.error({
              title: '失败',
              message: err.data.errmsg
            })
          })
        }
      })
    },
    dialogClose() {
      this.dialogVisible = false
      this.activityForm = {
        activityName: undefined,
        id: undefined,
        picUrl: undefined
      }
    }
  }
}
</script>
