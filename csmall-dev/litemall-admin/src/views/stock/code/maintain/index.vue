<template>
  <div class="app-container">

    <el-card class="card-show">
      <span>编码：{{ goodsCode.goodsCode }}</span>
      <span style="margin-left: 20px;">编码名称：{{ goodsCode.codeName }}</span>
    </el-card>

    <!-- 规格维护 -->
    <el-card class="card-show">
      <div slot="header" class="clearfix">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="10">
            <span>规格维护</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-button v-if="specs.length > 0" v-permission="['POST /admin/code/delSpecs']" class="filter-item" type="danger" icon="el-icon-edit" size="small" @click="handleTotalDelete">删除</el-button>
            <el-button v-else v-permission="['POST /admin/code/createSpecs']" class="filter-item" type="primary" icon="el-icon-edit" size="small" @click="handleCreate">新增</el-button>
          </el-col>
        </el-row>
      </div>
      <el-table :data="specs">
        <el-table-column property="spec" label="规格名" />
        <el-table-column property="value" label="规格值">
          <template slot-scope="scope">
            <el-tag type="primary">
              {{ scope.row.value }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增规格 -->
    <add-spec :id="goodsCode.id" v-model="dialogVisible" @submit="getcreateSubmit" />

    <!-- 规格库存 -->
    <el-card>
      <div slot="header" class="clearfix">
        <div>规格库存</div>
      </div>
      <el-card class="spec_upload" shadow="never">
        <div slot="header" class="clearfix">
          <h4 class="upload_header">上传规格属性图片(宽高比1:1最佳)：</h4>
          <el-button type="primary" size="small" @click="batchAddClick">批量操作</el-button>
        </div>
        <div class="upload_content">
          <el-upload
            class="uploader"
            :limit="10"
            :file-list="imgs"
            :on-success="updateImgs"
            :on-remove="removeImg"
            list-type="picture-card"
            :action="uploadPath"
            :headers="headers"
            :multiple="true"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <i slot="default" class="el-icon-plus avatar-uploader-icon" />
            <div slot="file" slot-scope="{file}">
              <img
                class="el-upload-list__item-thumbnail"
                :src="file.url"
                alt=""
              >
              <span class="el-upload-list__item-actions">
                <span
                  class="el-upload-list__item-delete"
                  @click="removeImg(file)"
                >
                  <i class="el-icon-delete" />
                </span>
              </span>
            </div>
          </el-upload>
        </div>
      </el-card>
      <el-table ref="products" :data="products" @selection-change="tableChange">
        <el-table-column align="center" type="selection" width="55" />
        <el-table-column property="value" label="货品规格" width="200">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.specsDesc" :key="tag">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" property="value" label="规格编码" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.specCode" style="width: 120px;" size="mini" placeholder="请输入编码规格" />
          </template>
        </el-table-column>
        <el-table-column align="center" property="value" label="规格图片">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              title="图片列表"
              width="400"
              trigger="click"
            >
              <el-row v-if="imgs.length" class="imgList" :gutter="10">
                <el-col v-for="img in imgs" :key="img.url" :span="6">
                  <img :src="img.url" @click="selectImg(img, scope.row)">
                </el-col>
              </el-row>
              <div v-else>请先上传图片</div>
              <el-button slot="reference">
                <img v-if="scope.row.url" class="spec_img" :src="scope.row.url" title="点击可更换图片">
                <span v-else>选择规格图片</span>
              </el-button>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column align="center" label="销售价格" property="price" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.shopPrice" style="width: 130px;" size="mini" placeholder="请输入价格">
              <template slot="append">元</template>
            </el-input>
          </template>
        </el-table-column>
        <el-table-column align="center" label="总库存" property="totalStock" />
      </el-table>
      <div class="op-container">
        <el-button v-permission="['POST /admin/code/updatePro']" type="primary" @click="handlePublish">提交</el-button>
      </div>

      <el-dialog title="批量添加图片价格" :visible.sync="batchVisible">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>编码规格</span>
          </div>
          <el-tag v-for="item in chooseData" :key="item.specsCodeId" size="medium" style="margin-right: 10px;margin-bottom:10px">{{ item.specsDesc +'/'+ item.specCode }}</el-tag>
        </el-card>
        <el-form ref="specificationsForm" :model="specificationsForm" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:50px;margin-top:30px">
          <el-form-item label="销售价格" prop="price">
            <el-row>
              <el-col :span="16">
                <el-input v-model="specificationsForm.price" clearable placeholder="请输入销售价格">
                  <template slot="append">元</template>
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="图片" prop="img">
            <el-upload
              :headers="headers"
              :action="uploadPath"
              :show-file-list="false"
              :on-success="handleAvatarSuccessPicUrl"
              :before-upload="beforeAvatarUpload"
              class="avatar-uploaders"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <img v-if="specificationsForm.img" :src="specificationsForm.img" class="avatars">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-form>

        <div class="op-container">
          <el-button @click="batchVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddClick">添加</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<style scoped>
.card-show{
  margin-bottom: 20px;
}
.op-container {
  margin-top: 20px;
  text-align: center;
}
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
  width: 60px;
  height: 60px;
  line-height: 60px;
  text-align: center;
}
.avatar {
  width: 60px;
  height: 60px;
  display: block;
}

.avatar-uploaders /deep/ .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploaders /deep/ .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploaders .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatars {
  width: 120px;
  height: 120px;
  display: block;
}
</style>

<script>
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import { codeRead, codeCreatePro, deleteSpecs } from '@/api/stock/code'
import addSpec from './components/addSpec'
import lrz from 'lrz'

export default {
  name: 'Maintain',
  components: { addSpec },
  data() {
    const validatePrice = (rule, value, callback) => {
      const reg = /^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('您输入的金额有误'))
      }
    }

    return {
      uploadPath,
      specs: [], // 规格数据
      products: [], // 拼装的规格
      goodsCode: {}, // 编码数据
      imgs: [],
      dialogVisible: false,
      chooseData: [],
      batchVisible: false,
      specificationsForm: {
        price: '',
        img: ''
      },
      rules: {
        price: [{ required: true, message: '不能为空', trigger: 'blur' }, { validator: validatePrice, trigger: 'blur' }],
        img: [{ required: true, message: '不能为空', trigger: 'blur' }]
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
    this.getData()
  },
  methods: {
    getData() {
      codeRead({ id: this.$route.query.id }).then(res => {
        const resData = res.data.data
        this.goodsCode = resData.goodsCode
        this.specs = resData.specs
        this.products = resData.products
        resData.products.forEach(({ id, url }) => {
          if (this.imgs.every(img => img.url !== url)) {
            if (url !== '') {
              this.imgs.push({ id, url })
            }
          }
        })
      }).catch(() => {
        this.goodsCode = {}
        this.specs = []
        this.products = []
      })
    },
    // 提交规格信息
    handlePublish() {
      if (this.validationSpecData() === true) {
        codeCreatePro(this.products).then(res => {
          this.$message({
            type: 'success',
            message: '编辑成功!'
          })
          setTimeout(() => {
            this.$router.push({ path: '/stock/code' })
          }, 2000)
        }).catch(err => {
          this.$notify.error({
            title: '失败',
            message: err.data.errmsg
          })
        })
      }
    },
    validationSpecData() {
      let flag = true
      for (const i in this.products) {
        if (Number(this.products[i].shopPrice) < -1) {
          this.$message({
            message: '您销售价格未输入完整',
            type: 'warning'
          })
          flag = false
          break
        }
        if (!this.products[i].url) {
          this.$message({
            message: '您规格图片未上传',
            type: 'warning'
          })
          flag = false
          break
        }
        if (!this.products[i].specCode) {
          this.$message({
            message: '您规格编码未输入完整',
            type: 'warning'
          })
          flag = false
          break
        }
      }

      return flag
    },
    // 新增
    handleCreate() {
      this.dialogVisible = true
    },
    // 删除
    handleTotalDelete() {
      this.$confirm('此操作将删除该编码下的全部规格信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const data = { id: this.goodsCode.id }
        deleteSpecs(data).then(res => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getData()
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
    getcreateSubmit(val) {
      this.specs = val.spacValue
      this.products = val.products
    },
    updateImgs(response, file, fileList) {
      // console.log('files:', file, fileList)
      this.imgs.push(Object.assign({}, response.data, { uid: file.uid }))
    },
    removeImg(file, fileList) {
      this.imgs.splice(this.imgs.findIndex(img => file.url === img.url), 1)
    },
    selectImg(img, product) {
      product.url = img.url
    },
    // 货品规格上次图片
    uploadAvatar(response, file, fileList, row) {
      this.products.forEach(item => {
        if (row.specCode === item.specCode) {
          item.url = response.data.url
        }
      })
    },
    tableChange(selected) {
      this.chooseData = []
      selected.forEach(item => {
        this.chooseData.push({ id: item.id, specCode: item.specCode, specsDesc: item.specsDesc })
      })
    },
    batchAddClick() {
      if (this.chooseData.length <= 0) {
        this.$message({
          message: '您未选择',
          type: 'warning'
        })
        return
      }
      this.batchVisible = true
    },
    handleAvatarSuccessPicUrl(res) {
      if (res.errno === 0) {
        this.specificationsForm.img = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
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
    handleAddClick() {
      this.$refs.specificationsForm.validate(valie => {
        if (valie) {
          this.products.forEach(prod => {
            this.chooseData.forEach(choose => {
              if (prod.id === choose.id) {
                prod.shopPrice = this.specificationsForm.price
                prod.url = this.specificationsForm.img
              }
            })
          })
          this.imgs = []
          this.products.forEach(({ id, url }) => {
            if (this.imgs.every(img => img.url !== url)) {
              if (url !== '') {
                this.imgs.push({ id, url })
              }
            }
          })

          this.batchVisible = false
          this.specificationsForm = {
            price: '',
            img: ''
          }
          this.$refs.products.clearSelection()
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.uploader ::v-deep {
  .el-upload {
    width: 68px;
    height: 68px;
    line-height: 74px;
  }
  .el-upload-list__item {
    width: 68px;
    height: 68px;
  }
}
.imgList{
  img {
    width: 100%;
    height: 85px;
    margin: 10px 0;
    border: 1px solid #ccc;
    &:hover {
      border: 1px solid rgb(13, 218, 30);
    }
  }
}
.spec_img {
  width: 48px;
  height: 48px;
  padding: 2px;
}
.upload_header{
  display: inline-block;
}
</style>
