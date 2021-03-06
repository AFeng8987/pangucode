import dayjs from 'dayjs'
import { isNumber } from 'lodash'
export const dateFormat = (value, format = 'YYYY-MM-DD') =>
  value ? dayjs(value * 1000).format(format) : ''

export const yuan = value =>
  isNumber(value) ? `¥${value.toFixed(2)}` : value

export const minus = value => `-${value}`

export const price = value =>
  isNumber(value) ? parseFloat(value).toFixed(2) : value

export default {
  install(Vue) {
    Vue.filter('yuan', yuan)
    Vue.filter('dateFormat', dateFormat)
    Vue.filter('price', price)
    Vue.filter('minus', minus)
  }
}
