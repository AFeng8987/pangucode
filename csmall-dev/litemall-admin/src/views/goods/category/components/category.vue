<template>
  <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogVisible" top="5vh" :close-on-click-modal="false" @open="dialogOpen" @close="diaClose">
    <el-form ref="categoryFrom" :model="categoryFrom" :rules="rules" status-icon label-position="left" label-width="120px" style="margin-left:50px;">
      <div v-if="levelStatus === false">
        <el-form-item label="一级类目名称" prop="name">
          <el-row>
            <el-col :span="16">
              <el-input v-model="categoryFrom.name" clearable placeholder="请输入一级类目名称" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="一级类目图标" prop="iconUrl">
          <el-upload
            class="avatar-uploader"
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="handleAvatarSuccessPositive"
            :before-upload="beforeAvatarUpload"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <img v-if="categoryFrom.iconUrl" :src="categoryFrom.iconUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
      </div>
      <div v-else>
        <el-form-item label="一级类目名称" prop="levelName">
          <el-row>
            <el-col :span="16">
              <el-input v-model="categoryFrom.levelName" disabled="" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="二级类目名称" prop="name">
          <el-row>
            <el-col :span="16">
              <el-input v-model="categoryFrom.name" clearable placeholder="请输入二级类目名称" />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="二级类目图标" prop="iconUrl">
          <el-upload
            class="avatar-uploader"
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="handleAvatarSuccessPositive"
            :before-upload="beforeAvatarUpload"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <img v-if="categoryFrom.iconUrl" :src="categoryFrom.iconUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="关键字" required>
          <el-tag v-for="(item, index) in keywordsData" :key="index" class="keywords_tag" closable @close="handleClose(item)">{{ item }}</el-tag>
          <el-input v-if="inputVisible" ref="saveTagInput" v-model="inputValue" class="input-new-tag" size="mini" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm" />
          <el-button v-else type="primary" size="mini" @click="showInput">新增</el-button>
        </el-form-item>
      </div>
      <el-form-item label="排序" prop="sortOrder">
        <el-row>
          <el-col :span="16">
            <el-input v-model.number="categoryFrom.sortOrder" placeholder="请输入排序(1~100正整数)" />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="diaClose">取消</el-button>
      <el-button v-if="judge() === '3'" type="primary" @click="createData">确定</el-button>
      <el-button v-if="judge() === '4'" type="primary" @click="updateData">确定</el-button>
      <el-button v-if="judge() === '1'" type="primary" @click="createLevelData">确定</el-button>
      <el-button v-if="judge() === '2'" type="primary" @click="updateLevelData">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import lrz from 'lrz'
import { listCatL1, createCategory, updateCategory } from '@/api/goods/category'

export default {
  name: 'Catrgory',
  props: {
    value: Boolean,
    state: { type: String, default: '' },
    data: { type: Object, default: null }
  },
  data() {
    const validateChinese = (rule, value, callback) => {
      const reg = /^[\u4e00-\u9fa5]+$/
      if (value.match(reg)) {
        callback()
      } else {
        callback(new Error('请输入中文'))
      }
    }
    const validateNumber = (rule, value, callback) => {
      const reg = /^([1-9][0-9]{0,1}|100)$/
      if (!reg.test(value)) {
        callback(new Error('请输入1-100，并且为正整数'))
      } else {
        callback()
      }
    }
    return {
      uploadPath,
      dialogVisible: false,
      dialogStatus: '',
      levelStatus: false,
      textMap: {
        update: '编辑',
        create: '创建'
      },
      categoryFrom: {
        name: undefined,
        iconUrl: undefined,
        level: undefined,
        pid: undefined,
        keywords: undefined,
        levelName: undefined,
        id: undefined,
        sortOrder: undefined
      },
      keywordsData: [],
      inputValue: '',
      inputVisible: false,
      rules: {
        name: [{ required: true, message: '类目名称不能为空', trigger: 'blur' }, { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }, { validator: validateChinese, trigger: 'blur' }],
        iconUrl: [{ required: true, message: '类目图标不能为空', trigger: 'blur' }],
        levelName: [{ required: true, message: '类目图标不能为空', trigger: 'blur' }],
        sortOrder: [{ required: true, message: '排序不能为空', trigger: 'blur' }, { validator: validateNumber, trigger: 'blur' }]
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
  watch: {
    value(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    // 打开dialog前的操作
    dialogOpen() {
      this.dialogStatus = this.state
      this.restFrom()
      if (this.data === null && this.dialogStatus === 'create') {
        this.levelStatus = false
        this.categoryFrom.level = 'L1'
      } else if (this.data.level === 'L1' && this.state === 'update') {
        this.levelStatus = false
        this.categoryFrom.name = this.data.name
        this.categoryFrom.level = this.data.level
        this.categoryFrom.id = this.data.id
        this.categoryFrom.iconUrl = this.data.iconUrl
        this.categoryFrom.sortOrder = this.data.sortOrder
      } else {
        this.levelStatus = true
        this.categoryFrom.levelName = this.data.name
        this.categoryFrom.pid = this.data.id
        if (this.data.level === 'L2' && this.state === 'update') {
          this.getLevelName(this.data.pid)
          this.handleData()
        }
      }
    },
    // 根据pid查询一级分类名称
    getLevelName(row) {
      listCatL1().then(res => {
        const list = res.data.data.list
        for (let i = list.length - 1; i >= 0; i--) {
          if (list[i].value === row) {
            this.categoryFrom.levelName = list[i].label
          }
        }
      })
    },
    // 二级类目修改时操作数据
    handleData() {
      this.categoryFrom.iconUrl = this.data.iconUrl
      this.categoryFrom.id = this.data.id
      this.categoryFrom.level = this.data.level
      this.categoryFrom.name = this.data.name
      this.categoryFrom.pid = this.data.pid
      this.categoryFrom.sortOrder = this.data.sortOrder
      this.keywordsData = this.data.keywords.split(';')
    },
    // 上传图片超过2M需要压缩
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
    // 上传图片成功之后的返回
    handleAvatarSuccessPositive(res) {
      if (res.errno === 0) {
        this.categoryFrom.iconUrl = res.data.url
      } else {
        this.$message.error(res.errmsg)
      }
    },
    // 添加关键字
    showInput() {
      if (this.keywordsData.length >= 8) {
        this.$message({
          message: '关键字不能超过8个',
          type: 'warning'
        })
        return
      }
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      if (this.inputValue) {
        if (this.keywordsData.indexOf(this.inputValue) === -1) {
          this.keywordsData.push(this.inputValue)
        } else {
          this.$message({
            message: '关键字不能有相同的，请重新输入',
            type: 'warning'
          })
        }
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    handleClose(tag) {
      this.keywordsData.splice(this.keywordsData.indexOf(tag), 1)
    },
    // 关闭dialog时的操作
    diaClose() {
      this.dialogVisible = false
      this.$refs.categoryFrom.resetFields()
      this.keywordsData = []
    },
    // 添加一级类目
    createData() {
      this.$refs.categoryFrom.validate(valie => {
        if (valie) {
          createCategory(this.categoryFrom).then(res => {
            this.$emit('submit')
            this.diaClose()
            this.$notify.success({
              title: '成功',
              message: '添加成功',
              duration: 1500
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg,
              duration: 1500
            })
          })
        }
      })
    },
    // 编辑一级类目
    updateData() {
      this.$refs.categoryFrom.validate(valie => {
        if (valie) {
          updateCategory(this.categoryFrom).then(res => {
            this.$emit('submit')
            this.diaClose()
            this.$notify.success({
              title: '成功',
              message: '修改成功',
              duration: 1500
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg,
              duration: 1500
            })
          })
        }
      })
    },
    // 添加二级类目
    createLevelData() {
      this.$refs.categoryFrom.validate(valie => {
        if (valie) {
          if (this.keywordsData.length <= 0) {
            this.$message({
              message: '关键字必须需要填写一个',
              type: 'warning'
            })
            return
          }
          createCategory(this.handleLevelData()).then(res => {
            this.$emit('submit')
            this.diaClose()
            this.$notify.success({
              title: '成功',
              message: '添加成功',
              duration: 1500
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg,
              duration: 1500
            })
          })
        }
      })
    },
    // 编辑二级类目
    updateLevelData() {
      this.$refs.categoryFrom.validate(valie => {
        if (valie) {
          if (this.keywordsData.length <= 0) {
            this.$message({
              message: '关键字必须需要填写一个',
              type: 'warning'
            })
            return
          }
          updateCategory(this.handleLevelData()).then(res => {
            this.$emit('submit')
            this.diaClose()
            this.$notify.success({
              title: '成功',
              message: '添加成功',
              duration: 1500
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg,
              duration: 1500
            })
          })
        }
      })
    },
    // 整理二级类目的数据
    handleLevelData() {
      this.keywordsData.forEach(item => {
        this.categoryFrom.keywords = (this.categoryFrom.keywords ? this.categoryFrom.keywords + ';' : '') + item
      })
      return {
        pid: this.categoryFrom.pid,
        name: this.categoryFrom.name,
        iconUrl: this.categoryFrom.iconUrl,
        level: this.categoryFrom.level || 'L2',
        id: this.categoryFrom.id || undefined,
        sortOrder: this.categoryFrom.sortOrder,
        keywords: this.categoryFrom.keywords
      }
    },
    judge() {
      if (this.data === null && this.dialogStatus === 'create') {
        return '3'
      } else {
        if (this.dialogStatus === 'create' && this.data.level === 'L1') {
          return '1'
        } else if (this.dialogStatus === 'update' && this.data.level === 'L2') {
          return '2'
        } else if (this.dialogStatus === 'update') {
          return '4'
        }
      }
    },
    // 初始化数据
    restFrom() {
      this.categoryFrom = {
        name: undefined,
        iconUrl: undefined,
        level: undefined,
        pid: undefined,
        keywords: undefined,
        levelName: undefined,
        id: undefined,
        sortOrder: undefined
      }
      this.keywordsData = []
    }
  }
}
</script>

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
  .input-new-tag{
    width: 100px;
  }
  .keywords_tag{
    margin-right: 10px;
  }
</style>
