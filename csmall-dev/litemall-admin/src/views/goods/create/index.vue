<template>
  <div class="app-container">

    <el-card v-if="firstStepInHere" class="box-card">
      <h3>选择分类类型</h3>

      <el-row>
        <el-col :span="6" :offset="2">
          <h4>一级分类</h4>
          <div class="typeBox">
            <ul v-for="item in categoryOneList" :key="item.value" class="typeUl">
              <li class="typeLi" :class="{ chooseStyle: isActiveAll(item.value) }" @click="oneClick(item)">{{ item.label }}</li>
            </ul>
          </div>
        </el-col>
        <el-col :span="6" :offset="2">
          <h4>二级分类</h4>
          <div class="typeBox">
            <ul v-for="item in categoryTwoList" :key="item.value" class="typeUl">
              <li class="typeLi" :class="{ chooseStyle: isActiveTwo(item.value) }" @click="twoClick(item)">{{ item.label }}</li>
            </ul>
          </div>
        </el-col>
      </el-row>

      <el-row class="typeButton">
        <el-col :span="12" :offset="8">
          <el-button type="primary" @click="nextClick">下一步</el-button>
        </el-col>
      </el-row>
    </el-card>

    <div v-else>
      <el-card class="box-card">
        <h3>商品添加</h3>

        <el-form ref="goods" :model="goods" :rules="rules" status-icon label-position="top" label-width="140px" style="margin-left:40px;margin-top:40px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="商品编码(可搜索：请输入编码名称)" prop="goodsCodeId">
                <el-row>
                  <el-col :span="18">
                    <el-select v-model="goods.goodsCodeId" clearable filterable :filter-method="searchCode" style="width:100%" placeholder="请输入商品编码名称" @change="codeClick">
                      <el-option v-for="item in goodsCoding" :key="item.id" :label="'编码/编码名称：' + item.goodsCode + ' / ' +item.codeName" :value="item.id" />
                    </el-select>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="简单描述" prop="brief">
                <el-input v-model="goods.brief" clearable placeholder="请输入描述" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="商品名称" prop="name">
                <el-row>
                  <el-col :span="18">
                    <el-input v-model="goods.name" clearable placeholder="请输入商品名称" />
                  </el-col>
                </el-row>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="原价" prop="counterPrice">
                <el-input v-model="goods.counterPrice" placeholder="请输入原件">
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
                <el-select v-model="goods.keywords" style="width:100%" placeholder="请选择关键字">
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
                  accept=".jpg,.jpeg,.png,.gif"
                >
                  <i class="el-icon-plus" />
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="商品详情介绍(图片宽度750px)" prop="detail">
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
        <el-button type="primary" @click="handlePublish('save')">保存</el-button>
        <el-button type="primary" @click="handlePublish('shelves')">上架</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { listCatL1, listCatL2 } from '@/api/goods/category'
import { uploadPath, createStorage, getQiNiuToken } from '@/api/storage'
import { getToken } from '@/utils/auth'
import lrz from 'lrz'
import { codeList, codeRead } from '@/api/stock/code'
import { publishGoods } from '@/api/goods/goods'

import { validatePrice } from '@/validates/price'

import * as qiniu from 'qiniu-js'

export default {
  name: 'GoodsCreate',
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
      categoryOneList: [],
      categoryTwoList: [],
      activeOneIndex: -1,
      activeTwoIndex: -1,
      goods: {
        goodsCodeId: undefined,
        name: undefined,
        brief: undefined,
        counterPrice: undefined,
        retailPrice: undefined,
        keywords: undefined,
        categoryId: undefined,
        picUrl: undefined,
        gallery: [],
        detail: undefined,
        shareUrl: '',
        isHome: false,
        homeSortOrder: 0,
        sales: 0
      },
      attributes: [],
      attributeForm: {
        attribute: undefined,
        value: undefined
      },
      attributeVisiable: false,
      firstStepInHere: true,
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
        gallery: [{ required: true, message: '商品展示图不能为空', trigger: 'blur' }],
        detail: [{ required: true, message: '商品详情介绍不能为空', trigger: 'blur' }],
        homeSortOrder: [{ validator: validNumber, trigger: 'blur' }]
      },
      goodsCoding: [],
      keywordsDate: [],
      productsData: [],
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
    this.emptyData()
    this.getCategory()
  },
  methods: {
    // 查询一级分类数据List
    getCategory() {
      listCatL1().then(res => {
        this.categoryOneList = res.data.data.list
      })
    },
    // 点击一级分类
    oneClick(row) {
      if (this.activeOneIndex === row.value) {
        this.activeOneIndex !== -1
      } else {
        this.activeOneIndex = row.value
      }
      listCatL2(row.value).then(res => {
        this.categoryTwoList = res.data.data.list
      })
    },
    // 点击二级类目
    twoClick(row) {
      this.goods.categoryId = row.value
      if (this.activeTwoIndex === row.value) {
        this.activeTwoIndex !== -1
      } else {
        this.activeTwoIndex = row.value
      }
      if (row.keywords) {
        this.keywordsDate = row.keywords.split(';')
      }
    },
    // 选择好类目 下一步
    nextClick() {
      if (this.activeTwoIndex !== -1 && this.goods.categoryId) {
        this.firstStepInHere = false
        this.getGoodsCoding()
      } else {
        this.$message({
          message: '您还未选择类目！！！',
          type: 'warning'
        })
      }
    },
    // 获取商品编码
    getGoodsCoding() {
      codeList().then(res => {
        this.goodsCoding = res.data.data.list
      })
    },
    // 根据用户输入的信息查询商品编码
    searchCode(row) {
      codeList({ codeName: row || undefined }).then(res => {
        this.goodsCoding = res.data.data.list
      })
    },
    isActiveAll(index) {
      return index === this.activeOneIndex
    },
    isActiveTwo(index) {
      return index === this.activeTwoIndex
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
    // 选择商品编码
    codeClick(row) {
      if (row) {
        codeRead({ id: row }).then(res => {
          this.productsData = res.data.data.products
        })
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
    // 上架/保存按钮
    handlePublish(type) {
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

          if (this.productsData.length <= 0) {
            this.$message({
              message: '您所选择的编码没有规格信息',
              type: 'warning'
            })
            return false
          }
          if (type === 'save') {
            this.goods.isOnSale = false
          } else {
            this.goods.isOnSale = true
          }
          const finalGoods = {
            goods: this.getGoodsData(this.goods),
            attributes: this.attributes
          }
          publishGoods(finalGoods).then(res => {
            this.$notify.success({
              title: '成功',
              message: '创建成功'
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
    getGoodsData(obj) {
      const goodsData = {}
      goodsData.brief = obj.brief
      goodsData.categoryId = obj.categoryId
      goodsData.counterPrice = obj.counterPrice
      goodsData.detail = obj.detail
      goodsData.gallery = JSON.stringify(obj.gallery)
      goodsData.goodsCodeId = obj.goodsCodeId
      goodsData.homeSortOrder = Number(obj.homeSortOrder)
      goodsData.isHome = obj.isHome
      goodsData.isOnSale = obj.isOnSale
      goodsData.keywords = obj.keywords
      goodsData.name = obj.name
      goodsData.picUrl = obj.picUrl
      goodsData.retailPrice = obj.retailPrice
      goodsData.shareUrl = obj.shareUrl
      goodsData.sales = obj.sales ? obj.sales : 0

      return goodsData
    },
    // 取消/完成上架
    emptyData() {
      this.goods = {
        goodsCodeId: undefined,
        name: undefined,
        brief: undefined,
        counterPrice: undefined,
        retailPrice: undefined,
        keywords: undefined,
        categoryId: undefined,
        picUrl: undefined,
        gallery: [],
        detail: undefined,
        shareUrl: undefined,
        isHome: false,
        homeSortOrder: 0,
        sales: 0
      }
      this.attributeForm = {
        attribute: undefined,
        value: undefined
      }
      this.categoryOneList = []
      this.categoryTwoList = []
      this.activeOneIndex = -1
      this.activeTwoIndex = -1
      this.attributes = []
      this.goodsCoding = []
      this.keywordsDate = []
      this.productsData = []
    }
  }
}
</script>

<style scoped>
.typeBox{
  border:1px solid rgb(219, 216, 216);
  border-radius: 10px;
  height: 200px;
  text-align: center;
  overflow-y: scroll;
}
.typeUl{
  font-size:14px;
  padding:0;
  margin:0;
}
.typeLi{
  padding:15px;
  cursor: pointer;
  list-style-type: none;
}
.typeButton{
  margin-top: 50px;
}
.chooseStyle {
  background: #289ffd;
  color: white;
}
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
