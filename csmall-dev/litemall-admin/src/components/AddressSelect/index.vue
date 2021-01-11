<template>
  <el-form ref="value" :inline="true" :model="value" :rules="rules">
    <el-row :style="!state ? 'margin-bottom: 25px': ''">
      <el-col :span="6">
        <el-form-item label="" prop="startProvice">
          <el-select v-model="value.startProvice" placeholder="请选择省" @change="updateCityStart()">
            <el-option v-for="item in cityDataStart" :key="item.code" :label="item.name" :value="item.name" @click.native="updateCityStart()" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="" prop="startCity">
          <el-select v-model="value.startCity" placeholder="请选择市" @change="updateDistrictStart()">
            <el-option v-for="item in cityArrStart" :key="item.code" :label="item.name" :value="item.name" @click.native="updateDistrictStart()" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="" prop="startDistrict">
          <el-select v-model="value.startDistrict" placeholder="请选择区/县" @change="updateAreaStart()">
            <el-option v-for="item in regionArrStart" :key="item.code" :label="item.name" :value="item.name" @click.native="updateAreaStart()" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="" prop="startTown">
          <el-select v-model="value.startTown" placeholder="请选择街道/镇">
            <el-option v-for="item in townArrStart" :key="item.code" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="!state">
      <el-col :span="16">
        <el-form-item prop="startAddress">
          <el-input v-model.trim="value.startAddress" placeholder="请输入具体地址" style="width: 300px" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import ares from './pcas-code.json'

export default {
  name: 'AddressSelect',
  props: {
    value: {
      type: Object,
      default: null
    },
    state: Boolean
  },
  data() {
    return {
      ares, // 省市区的json数据
      cityDataStart: {}, // 存放省
      cityArrStart: [], // 存放市
      regionArrStart: [], // 存放区/县
      townArrStart: [], // 存放街道/镇
      rules: {
        startProvice: [{ required: true, message: '省不能为空', trigger: 'change' }],
        startCity: [{ required: true, message: '市不能为空', trigger: 'change' }],
        startDistrict: [{ required: true, message: '区/县不能为空', trigger: 'change' }],
        startTown: [{ required: true, message: '街道/镇不能为空', trigger: 'change' }],
        startAddress: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    value(val) {
      this.updateData()
    }
  },
  created() {
    this.cityDataStart = ares
    this.updateData()
  },
  methods: {
    // 如果有值传过来  需要将省市区街道的数据筛选出来
    updateData() {
      if (this.value.startProvice) {
        // 将市的数据筛选出来
        for (let i = this.cityDataStart.length - 1; i >= 0; i--) {
          if (this.value.startProvice === this.cityDataStart[i].name) {
            this.cityArrStart = this.cityDataStart[i].children
            // 将区的数据筛选出来
            for (let j = this.cityArrStart.length - 1; j >= 0; j--) {
              if (this.value.startCity === this.cityArrStart[j].name) {
                this.regionArrStart = this.cityArrStart[j].children
                // 将街道的数据筛选出来
                for (const item in this.regionArrStart) {
                  if (this.value.startDistrict === this.regionArrStart[item].name) {
                    this.townArrStart = this.regionArrStart[item].children
                  }
                }
              }
            }
          }
        }
      }
    },
    // 根据选中的省找到省下面的市
    updateCityStart() {
      this.value.startCity = ''
      this.value.startDistrict = ''
      this.value.startTown = ''
      for (const item in this.cityDataStart) {
        if (this.value.startProvice === this.cityDataStart[item].name) {
          this.cityArrStart = this.cityDataStart[item].children
        }
      }
    },
    // 根据选中的市找到市下面的区/县
    updateDistrictStart() {
      for (const item in this.cityArrStart) {
        if (this.value.startCity === this.cityArrStart[item].name) {
          this.regionArrStart = this.cityArrStart[item].children
        }
      }
    },
    // 根据选中的区/县找到区/县下面的街道/镇
    updateAreaStart() {
      for (const item in this.regionArrStart) {
        if (this.value.startDistrict === this.regionArrStart[item].name) {
          this.townArrStart = this.regionArrStart[item].children
        }
      }
    },
    validateForm() {
      let flag = null
      this.$refs['value'].validate(valid => {
        if (valid) {
          flag = true
        } else {
          flag = false
        }
      })
      return flag
    },
    resetForm() {
      this.$refs['value'].resetFields()
    }
  }
}
</script>
