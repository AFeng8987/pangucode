<template>
  <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogVisible" :close-on-click-modal="false" top="1vh" @open="dialogOpen" @close="dialogClose">
    <el-form ref="bannerFrom" :model="bannerFrom" :rules="rules" status-icon label-position="left" label-width="130px" style="margin-left:35px;">
      <el-form-item label="Banner名称" prop="name">
        <el-row>
          <el-col :span="16">
            <el-input v-model.trim="bannerFrom.name" placeholder="请输入Banner名称" />
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="Banner图片" prop="url">
        <el-upload
          :headers="headers"
          :action="uploadPath"
          :show-file-list="false"
          :on-success="handleAvatarSuccessUrl"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
          accept=".jpg,.jpeg,.png,.gif"
        >
          <img v-if="bannerFrom.url" :src="bannerFrom.url" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon" />
        </el-upload>
        <span style="color:red;">(图片分辨率：宽度750px*高度400px)</span>
      </el-form-item>
      <el-form-item label="关联选择" prop="status">
        <el-row>
          <el-col :span="16">
            <el-select v-model="bannerFrom.status" @change="bannerChange">
              <el-option v-for="item in relevance" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item v-show="isState" label="关联商品" prop="link">
        <div class="eimg">
          <img v-if="goodsPicUrl" :src="goodsPicUrl" class="eimgs">
          <i v-else />
        </div>
        <el-button class="ebutton" type="primary" @click="dialogPicture">添加图片跳转</el-button>
      </el-form-item>
      <el-form-item v-show="!isState" label="关联链接" prop="link">
        <el-col :span="16">
          <el-input v-model.trim="bannerFrom.link" placeholder="请输入外部链接" />
        </el-col>
      </el-form-item>
      <el-form-item label="是否开启" prop="enabled">
        <el-switch v-model="bannerFrom.enabled" />
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-col :span="16">
          <el-input v-model.trim="bannerFrom.sortOrder" clearable placeholder="请输入数字" />
        </el-col>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">取消</el-button>
      <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
      <el-button v-else type="primary" @click="updateData">确定</el-button>
    </div>

    <el-dialog title="选择关联商品" :visible.sync="dialogGoodsVisible" width="60%" append-to-body lock-scroll :close-on-click-modal="false" top="1vh">
      <div class="filter-container">
        <el-input v-model="listQuery.goodsId" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品编码" />
        <el-input v-model="listQuery.goodsSn" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品编号" />
        <el-input v-model="listQuery.name" clearable class="filter-item" size="small" style="width: 200px;" placeholder="请输入商品名称" />
        <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFilter">查找</el-button>
      </div>

      <el-card :body-style="{ padding: '0px' }">
        <el-table v-loading="listLoading" :data="list" height="400" element-loading-text="正在查询中。。。" border fit highlight-current-row>
          <el-table-column align="center" label="商品编码" prop="id" />
          <el-table-column align="center" min-width="100" label="名称" prop="name" />
          <el-table-column align="center" property="iconUrl" label="图片">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="40">
            </template>
          </el-table-column>
          <el-table-column align="center" label="是否在售" prop="isOnSale">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isOnSale ? 'success' : 'info' ">{{ scope.row.isOnSale ? '在售' : '未售' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template slot-scope="scope">
              <el-button :type="scope.row.isOnSale ? 'primary' : 'info' " :disabled="scope.row.isOnSale !== true" size="mini" @click="handleChoose(scope.row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getGoods" />
      </el-card>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import lrz from 'lrz'
import { adCreate, adRead, adUpdate } from '@/api/goods/banner'
import { listGoods } from '@/api/goods/goods'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'HandleBanner',
  components: { Pagination },
  props: {
    data: {
      type: Object,
      default: null
    },
    value: Boolean,
    status: {
      type: String,
      default: null
    }
  },
  data() {
    const validNumber = (rule, value, callback) => {
      const reg = /^[1-9]\d{0,2}$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('只能输入正整数并且限制1000以内'))
      }
    }
    return {
      uploadPath,
      dialogVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '新增'
      },
      bannerFrom: {
        url: undefined,
        name: undefined,
        status: 0,
        enabled: true,
        link: undefined,
        id: undefined,
        sortOrder: undefined
      },
      goodsPicUrl: '',
      rules: {
        name: [{ required: true, message: 'Banner名称不能为空', trigger: 'blur' },
          { max: 40, message: '40 个字符以内', trigger: 'blur' }
        ],
        url: [{ required: true, message: 'Banner图片不能为空', trigger: 'blur' }],
        status: [{ required: true, message: '请选择关联选择', trigger: 'change' }],
        link: [{ required: true, message: '不能为空', trigger: 'blur' }],
        enabled: [{ required: true, message: '不能为空', trigger: 'blur' }],
        sortOrder: [{ required: true, message: '不能为空', trigger: 'blur' }, { validator: validNumber, trigger: 'blur' }]
      },
      isState: true,
      relevance: [
        {
          value: 0,
          label: '关联商品'
        },
        {
          value: 1,
          label: '关联链接'
        }
      ],
      dialogGoodsVisible: false,
      listQuery: {
        page: 1,
        limit: 10,
        goodsId: undefined,
        goodsSn: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      listLoading: false,
      list: [],
      total: 0
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
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
      this.dialogStatus = this.status
      if (this.data !== null) {
        adRead({ id: this.data.id }).then(res => {
          const goodsData = res.data.data
          this.bannerFrom.id = goodsData.id
          this.bannerFrom.url = goodsData.url
          this.bannerFrom.name = goodsData.name
          this.bannerFrom.enabled = goodsData.enabled
          this.bannerFrom.link = goodsData.link
          this.bannerFrom.sortOrder = goodsData.sortOrder
          if (goodsData.status === false) {
            this.bannerFrom.status = 0
            this.goodsPicUrl = this.data.goodsPic
            this.isState = true
          } else {
            this.bannerFrom.status = 1
            this.isState = false
          }
        })
      } else {
        this.clearData()
      }
    },
    // 关闭dialog操作
    dialogClose() {
      this.dialogVisible = false
      this.clearData()
    },
    // 上传图片前超过2M压缩
    beforeAvatarUpload(file) {
      return new Promise((resolve, reject) => {
        const isLt2M = file.size / 1024 / 1024 < 2 // 判定图片大小是否小于2MB
        if (!isLt2M) {
          lrz(file, { quality: 0.8 }).then(res => {
            resolve(res.file)
          })
        } else {
          resolve(file)
        }
      })
    },
    // 上传图片成功
    handleAvatarSuccessUrl(res) {
      if (res.errno === 0) {
        this.bannerFrom.url = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
    // 根据用户选择的展示关联的商品或者链接
    bannerChange(row) {
      if (Number(row) === 0) {
        this.isState = true
      } else {
        this.isState = false
      }
      this.bannerFrom.link = undefined
      this.goodsPicUrl = undefined
    },
    // 添加确定按钮操作
    createData() {
      this.$refs.bannerFrom.validate(valie => {
        if (valie) {
          if (Number(this.bannerFrom.status) === 1) {
            const reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/
            if (!reg.test(this.bannerFrom.link)) {
              this.$message({
                message: '您输入的网站有误！！！',
                type: 'warning'
              })
              return
            }
          }
          adCreate(this.bannerFrom).then(res => {
            this.$notify.success({
              title: '成功',
              message: '创建成功'
            })
            this.dialogVisible = false
            this.$emit('submit')
            this.clearData()
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.data.errmsg
            })
          })
        }
      })
    },
    // 编辑确定按钮操作
    updateData() {
      this.$refs.bannerFrom.validate(valie => {
        if (valie) {
          if (Number(this.bannerFrom.status) === 1) {
            const reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/
            if (!reg.test(this.bannerFrom.link)) {
              this.$message({
                message: '您输入的网站有误！！！',
                type: 'warning'
              })
              return
            }
          }
          adUpdate(this.bannerFrom).then(res => {
            this.$notify.success({
              title: '成功',
              message: '创建成功'
            })
            this.dialogVisible = false
            this.$emit('submit')
            this.clearData()
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.data.errmsg
            })
          })
        }
      })
    },
    // 添加图片关联
    dialogPicture() {
      this.dialogGoodsVisible = true
      this.getGoods()
    },
    // 商品搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getGoods()
    },
    // 获取全部商品list
    getGoods() {
      this.listLoading = true
      listGoods(this.listQuery).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    // 选择关联的商品
    handleChoose(row) {
      this.goodsPicUrl = row.picUrl
      this.bannerFrom.link = row.id
      this.dialogGoodsVisible = false
      this.list = []
      this.total = 0
      this.listQuery = {
        page: 1,
        limit: 10,
        goodsId: undefined,
        goodsSn: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      }
    },
    // 清楚数据
    clearData() {
      this.bannerFrom = {
        url: undefined,
        name: undefined,
        status: 0,
        enabled: true,
        link: undefined,
        id: undefined,
        sortOrder: undefined
      }
      this.goodsPicUrl = ''
    }
  }
}
</script>

<style scoped>
.eimgs {
    width: 120px;
    height: 120px;
    display: block;
}

.eimg {
    width: 120px;
    height: 120px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    display: inline-block;
    vertical-align: bottom;
}

.eimg:hover {
    border-color: #20a0ff;
}

.ebutton {
    display: inline-block;
    vertical-align: bottom;
}
/* 上传图片css */
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
.avatar-uploader /deep/ .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>
