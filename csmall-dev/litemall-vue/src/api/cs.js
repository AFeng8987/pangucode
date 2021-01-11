import request from '@/utils/request'

export function load() {
  return request({
    url: '/wx/aftersale/customer',
    loading: true,
    method: 'get'
  }).then(({ data }) => {
    if (data.errno === 0) {
      return data.data
    }
  })
}
