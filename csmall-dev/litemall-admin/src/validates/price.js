import { validPrice } from '../utils/validate'

export function validatePrice(rule, value, callback) {
  if (validPrice(value)) {
    callback()
  } else {
    callback(new Error('您输入的金额有误！'))
  }
}
