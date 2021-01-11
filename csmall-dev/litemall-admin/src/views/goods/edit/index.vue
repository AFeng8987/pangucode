<template>
  <div class="app-container">

    <el-card>
      <h3>商品编辑</h3>

      <el-form ref="goods" :model="goods" :rules="rules" status-icon label-position="top" label-width="140px" style="margin-left:40px;margin-top:40px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品编码 (可搜索：请输入编码名称)" prop="goodsCodeId">
              <el-row>
                <el-col :span="18">
                  <el-select v-model="goods.goodsCodeId" filterable clearable :filter-method="searchCode" style="width:100%" placeholder="请输入商品编码名称(编码+编码名称)" @change="codeClick">
                    <el-option v-for="item in goodsCoding" :key="item.id" :label="'编码/编码名称：' + item.goodsCode + ' / ' +item.codeName" :value="item.id" />
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="简单描述" prop="brief">
              <el-input v-model="goods.brief" placeholder="请输入描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-row>
                <el-col :span="18">
                  <el-input v-model="goods.name" placeholder="请输入商品名称" />
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="原价" prop="counterPrice">
              <el-input v-model="goods.counterPrice" type="number" placeholder="请输入原价">
                <template slot="append">元</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="本店售价" prop="retailPrice">
              <el-row>
                <el-col :span="18">
                  <el-input v-model="goods.retailPrice" placeholder="请输入本店售价">
                    <template slot="append">元</template>
                  </el-input>
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="关键词" prop="keywords">
              <el-select v-model="goods.keywords" clearable style="width:100%" placeholder="请选择关键字">
                <el-option v-for="item in keywordsDate" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-row>
              <el-col :span="10">
                <el-form-item label="销量" prop="sales">
                  <el-input v-model="goods.sales" clearable placeholder="请输入首页展示排序" />
                </el-form-item>
              </el-col>
              <el-col :span="8" style="text-align: center;">
                <el-form-item label="首页展示" prop="isHome">
                  <el-switch
                    v-model="goods.isHome"
                    active-text="是"
                    inactive-text="否"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="10">
            <el-form-item label="首页展示排序" prop="homeSortOrder">
              <el-input v-model="goods.homeSortOrder" clearable placeholder="请输入首页展示排序" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="5">
            <el-form-item label="一级分类" :rules="[{ required: true, message: '请选择一级分类', trigger: 'change' }]">
              <el-select v-model="categoryIds.L1" style="width: 100%" clearable placeholder="请选择" @change="L1CategoryChange">
                <el-option v-for="item in categoryL1List" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="二级分类" :rules="[{ required: true, message: '请选择一级分类', trigger: 'change' }]">
              <el-select v-model="categoryIds.L2" style="width: 100%" clearable placeholder="请选择" @change="L2CategoryChange">
                <el-option v-for="item in categoryL2List" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品主图（800x 800）px" prop="picUrl">
              <el-upload
                :headers="headers"
                :action="uploadPath"
                :show-file-list="false"
                :on-success="handleAvatarSuccessPicUrl"
                :before-upload="beforeAvatarUpload"
                class="avatar-uploader"
                accept=".jpg,.jpeg,.png,.gif"
              >
                <img v-if="goods.picUrl" :src="goods.picUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="上传视频（可以单独上传，也可以使用地址链接）">
              <el-row :gutter="20" type="flex" justify="center" align="middle">
                <el-col :span="12">
                  <el-input v-model="goods.shareUrl" clearable placeholder="视频地址链接" />
                </el-col>
                <el-col :span="12">
                  <el-upload
                    :action="uploadPath"
                    :show-file-list="false"
                    class="avatar-uploader"
                    accept=".mp4"
                    :http-request="upqiniu"
                    :before-upload="beforeVideoUpload"
                  >
                    <video v-if="goods.shareUrl" :src="goods.shareUrl" class="goodsVideo" controls="controls">您的浏览器不支持视频播放</video>
                    <i v-else class="el-icon-plus avatar-uploader-icon" />
                  </el-upload>
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="商品展示图（800x 800）px" prop="gallery">
              <el-upload
                :headers="headers"
                :action="uploadPath"
                list-type="picture-card"
                :on-remove="handleRemove"
                :limit="5"
                :before-upload="beforeAvatarUpload"
                :on-exceed="handleExceed"
                :on-success="handleAvatarSuccessGallery"
                :file-list="galleryFileList"
              >
                <i class="el-icon-plus" />
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品详情介绍" prop="detail">
          <vue-tinymce v-model="goods.detail" :setting="editorInit" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;" class="box-card">
      <h3>规格编码展示</h3>
      <el-form label-position="left" label-width="100px" style="margin-left:30px;margin-top:30px">
        <el-form-item label="规格编码：">
          <el-row>
            <el-col v-for="item in productsData" :key="item.id" style="text-decoration:underline" :span="4">{{ item.specCode+ ',' +item.specsDesc.toString() }}</el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <el-row>
        <el-col :span="20" :offset="2">
          <el-table :data="productsData">
            <el-table-column property="value" label="货品规格">
              <template slot-scope="scope">
                <el-tag v-for="tag in scope.row.specsDesc" :key="tag">
                  {{ tag }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column align="center" property="specCode" label="规格编码" />
            <el-table-column align="center" property="value" label="规格图片">
              <template slot-scope="scope">
                <img v-if="scope.row.url" :src="scope.row.url" width="50" height="50">
              </template>
            </el-table-column>
            <el-table-column align="center" label="销售价格" property="shopPrice" />
            <el-table-column align="center" label="总库存" property="totalStock" />
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px;">
      <h3>商品参数</h3>
      <el-button type="primary" @click="handleAttributeShow">添加</el-button>
      <el-table :data="attributes">
        <el-table-column property="attribute" label="商品参数名称" />
        <el-table-column property="value" label="商品参数值" />
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="attributeVisiable" title="设置商品参数">
        <el-form ref="attributeForm" :model="attributeForm" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:50px;">
          <el-form-item label="商品参数名称" prop="attribute">
            <el-row>
              <el-col :span="14">
                <el-input v-model="attributeForm.attribute" />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="商品参数值" prop="value">
            <el-row>
              <el-col :span="14">
                <el-input v-model="attributeForm.value" />
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="attributeVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleAttributeAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleUpdate('save')">保存</el-button>
      <el-button type="primary" @click="handleUpdate('shelves')">更新并上架</el-button>
    </div>
  </div>
</template>

<script>
import { detailGoods, editGoods } from '@/api/goods/goods'
import { uploadPath, createStorage, getQiNiuToken } from '@/api/storage'
import { getToken } from '@/utils/auth'
import lrz from 'lrz'
import { listCatL1, listCatL2 } from '@/api/goods/category'
import { codeList, codeRead } from '@/api/stock/code'

import { validatePrice } from '@/validates/price'
// import router from '@/router'

import * as qiniu from 'qiniu-js'

export default {
  name: 'GoodsEdit',
  data() {
    const comparePrice = (rule, value, callback) => {
      const max = this.goods.counterPrice
      const min = this.goods.retailPrice
      if (Number(min) <= Number(max)) {
        callback()
      } else {
        callback(new Error('商品价格应小于原价'))
      }
    }

    const validNumber = (rule, value, callback) => {
      const reg = /^[0-9]\d{0,2}$/
      if (reg.test(value)) {
        callback()
      } else {
        callback(new Error('只能输入正整数并且限制1000以内'))
      }
    }
    return {
      uploadPath,
      goods: {
        shareUrl: ''
      },
      attributes: [],
      categoryIds: [],
      categoryL1List: [],
      categoryL2List: [],
      goodsCoding: [],
      keywordsDate: [],
      galleryFileList: [],
      productsData: [],
      attributeForm: {
        attribute: undefined,
        value: undefined
      },
      goodsCode: {},
      attributeVisiable: false,
      rules: {
        attribute: [{ required: true, message: '商品参数名称不能为空', trigger: 'blur' }],
        value: [{ required: true, message: '商品参数值不能为空', trigger: 'blur' }],
        goodsCodeId: [{ required: true, message: '请选择商品编码', trigger: 'change' }],
        name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' },
          { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }],
        brief: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }
        ],
        retailPrice: [
          { required: true, message: '本店售价不能为空', trigger: 'blur' },
          { validator: validatePrice, trigger: 'blur' },
          { validator: comparePrice, trigger: 'blur' }
        ],
        counterPrice: [
          { validator: validatePrice, trigger: 'blur' },
          { validator: comparePrice, trigger: 'blur' }
        ],
        keywords: [{ required: true, message: '请选择关键字', trigger: 'change' }],
        picUrl: [{ required: true, message: '商品主图不能为空', trigger: 'blur' }],
        detail: [{ required: true, message: '商品详情介绍不能为空', trigger: 'blur' }],
        homeSortOrder: [{ validator: validNumber, trigger: 'blur' }]
      },
      editorInit: {
        menubar: false,
        toolbar: 'alignleft aligncenter alignright alignjustify | numlist bullist | media axupimgs | fontselect fontsizeselect forecolor backcolor | bold italic underline |',
        toolbar_drawer: 'sliding',
        plugins: 'image media axupimgs',
        language: 'zh_CN', // 本地化设置
        height: 500,
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData)
            .then(res => {
              success(res.data.data.url)
            })
            .catch(() => {
              failure('上传失败，请重新上传')
            })
        }
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
    // 根据id查询商品详情
    getList() {
      this.emptyData()
      detailGoods(this.$route.query.id).then(res => {
        res.data.data.goods.shareUrl = res.data.data.goods.shareUrl || ''
        this.goods = res.data.data.goods
        this.attributes = res.data.data.attributes
        this.categoryIds = res.data.data.categoryIds
        this.goodsCode = res.data.data.goodsCode
        if (this.goodsCode) {
          this.codeClick(res.data.data.goods.goodsCodeId)
        } else {
          this.goods.goodsCodeId = null
        }

        this.galleryFileList = []
        const gallert = JSON.parse(this.goods.gallery)
        this.goods.gallery = gallert
        for (var i = 0; i < gallert.length; i++) {
          this.galleryFileList.push({
            url: gallert[i]
          })
        }
        this.getCategoryL1()
        this.getGoodsCode()
      })
    },
    // 获取一级类目数据
    getCategoryL1() {
      listCatL1().then(res => {
        this.categoryL1List = res.data.data.list
        if (JSON.stringify(this.categoryIds) !== '{}') {
          this.getCategoryL2()
        }
      })
    },
    // 获取二级类目数据
    getCategoryL2() {
      listCatL2(Number(this.categoryIds.L1)).then(res => {
        this.categoryL2List = res.data.data.list
        this.getkeywords()
      })
    },
    // 获取商品编码
    getGoodsCode() {
      let vv = false
      codeList().then(res => {
        this.goodsCoding = res.data.data.list
        if (this.goodsCode) {
          this.goodsCoding.forEach(item => {
            if (item.id === this.goodsCode.id) {
              vv = true
            }
          })
          if (!vv) {
            this.goodsCoding.push(this.goodsCode)
          }
        }
      })
    },

    L2CategoryChange(obj) {
      this.goods.categoryId = obj
      this.getkeywords()
    },

    L1CategoryChange(obj) {
      listCatL2(Number(obj)).then(res => {
        this.categoryL2List = res.data.data.list
        this.categoryIds.L2 = ''
        this.goods.keywords = ''
        this.keywordsDate = []
      })
    },

    // 获取二级类目的关键字
    getkeywords() {
      this.categoryL2List.forEach(item => {
        if (Number(item.value) === Number(this.categoryIds.L2)) {
          this.keywordsDate = item.keywords.split(';')
        }
      })
    },
    // 根据用户输入的信息查询商品编码
    searchCode(row) {
      codeList({ codeName: row || undefined }).then(res => {
        this.goodsCoding = res.data.data.list
      })
    },
    // 选择商品编码查询出规格信息
    codeClick(row) {
      if (row) {
        codeRead({ id: row }).then(res => {
          this.productsData = res.data.data.products
        })
      }
    },
    // 视频上传前的操作
    beforeVideoUpload(file) {
      return new Promise((resolve, reject) => {
        const isLt50M = file.size / 1024 / 1024 < 50
        if (isLt50M) {
          resolve(file)
        } else {
          this.$message({
            message: '您上传的视频超过了50M',
            type: 'warning'
          })
          reject()
        }
      })
    },
    upqiniu(resq) {
      const file = resq.file
      const key = this.$md5(resq.file.name) + '.mp4'
      const config = {
        useCdnDomain: true,
        region: null
      }

      getQiNiuToken().then(resa => {
        const observable = qiniu.upload(file, key, resa.data.data, {}, config)
        observable.subscribe({
          next: (res) => {
            // 主要用来展示进度
            // console.log(res, '进度')
          },
          error: (err) => {
            // 失败报错信息
            console.log(err, '失败')
          },
          complete: (res) => {
            // 接收成功后返回的信息
            this.goods.shareUrl = 'https://img.pgwlxx.com/' + res.key
          }
        })
      })
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
    // 上传图片成功返回
    handleAvatarSuccessPicUrl(res) {
      if (res.errno === 0) {
        this.goods.picUrl = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
    // 商品展示图超过5张
    handleExceed(files, fileList) {
      this.$message({
        message: '商品展示图不能超过5张',
        type: 'warning'
      })
    },
    // 商品展示图上传成功
    handleAvatarSuccessGallery(response) {
      if (response.errno === 0) {
        this.goods.gallery.push(response.data.url)
      } else {
        this.$message.error(response.errmsg)
      }
    },
    // 商品展示图删除
    handleRemove(file, fileList) {
      for (let i = 0; i < this.goods.gallery.length; i++) {
        var url
        if (file.response === undefined) {
          url = file.url
        } else {
          url = file.response.data.url
        }

        if (this.goods.gallery[i] === url) {
          this.goods.gallery.splice(i, 1)
        }
      }
    },
    // 添加商品参数
    handleAttributeShow() {
      this.attributeForm = {
        attribute: undefined,
        value: undefined
      }
      this.attributeVisiable = true
    },
    // 删除商品参数
    handleAttributeDelete(row) {
      const index = this.attributes.indexOf(row)
      this.attributes.splice(index, 1)
    },
    // 提交商品参数
    handleAttributeAdd() {
      this.$refs.attributeForm.validate(valie => {
        if (valie) {
          this.attributes.unshift(this.attributeForm)
          this.attributeVisiable = false
        }
      })
    },
    // 取消按钮
    handleCancel() {
      this.emptyData()
      this.$router.push({ path: '/goods/goods' })
    },
    // 更新商品
    handleUpdate(type) {
      this.$refs.goods.validate(valie => {
        if (valie) {
          const reg = /(http|https):\/\/([\w.]+\/?)\S*/
          if (this.goods.shareUrl) {
            if (!reg.test(this.goods.shareUrl) && this.goods.shareUrl.lastIndexOf('.mp4') === -1) {
              this.$message({
                message: '视频链接不符合，请重新上传或者输入',
                type: 'warning'
              })
              return false
            }
          }
          if (type === 'save') {
            this.goods.isOnSale = false
          } else {
            this.goods.isOnSale = true
          }
          const data = this.operationData(this.goods, this.attributes)

          editGoods(data).then(res => {
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
            this.emptyData()
            this.$router.push({ path: '/goods/list' })
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.data.errmsg
            })
          })
        }
      })
    },

    // 操作数据
    operationData(goods, att) {
      for (let i = 0; i < att.length; i++) {
        delete this.attributes[i].addTime
        delete this.attributes[i].updateTime
      }

      const goodsData = {}
      goodsData.brandId = Number(goods.brandId)
      goodsData.brief = goods.brief
      goodsData.categoryId = Number(goods.categoryId)
      goodsData.counterPrice = Number(goods.counterPrice)
      goodsData.deleted = goods.deleted
      goodsData.detail = goods.detail
      goodsData.gallery = JSON.stringify(goods.gallery)
      goodsData.goodsCodeId = Number(goods.goodsCodeId)
      goodsData.goodsSn = goods.goodsSn
      goodsData.homeSortOrder = Number(goods.homeSortOrder)
      goodsData.id = goods.id
      goodsData.isHome = goods.isHome
      goodsData.isHot = goods.isHot
      goodsData.isNew = goods.isNew
      goodsData.isOnSale = goods.isOnSale
      goodsData.keywords = goods.keywords
      goodsData.name = goods.name
      goodsData.picUrl = goods.picUrl
      goodsData.retailPrice = Number(goods.retailPrice)
      goodsData.sales = goods.sales
      goodsData.shareUrl = goods.shareUrl
      goodsData.sortOrder = Number(goods.sortOrder)
      goodsData.unit = goods.unit
      goodsData.sales = goods.sales ? Number(goods.sales) : 0

      return { goods: goodsData, attributes: att }
    },

    // 清除数据
    emptyData() {
      this.goods = {}
      this.attributes = []
      this.categoryIds = []
      this.categoryL1List = []
      this.goodsCoding = []
      this.keywordsDate = []
      this.galleryFileList = []
      this.productsData = []
      this.attributeForm = {
        attribute: undefined,
        value: undefined
      }
    }
  }
}
</script>

<style scoped>
.op-container{
  text-align: center;
  margin-top: 40px;
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
  width: 150px;
  height: 150px;
  line-height: 150px;
  text-align: center;
}
.avatar {
  width: 150px;
  height: 150px;
  display: block;
}
.goodsVideo{
  width: 260px;
  height: 150px;
}
</style>
