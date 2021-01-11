import request from '@/utils/request'

const StorageUpload = 'wx/storage/upload' // 图片上传,
export function imgUpLoad(data) {
  return request({
    url: StorageUpload,
    method: 'post',
    data
  })
}
