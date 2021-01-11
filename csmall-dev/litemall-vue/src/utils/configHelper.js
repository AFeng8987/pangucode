const baseUrl = process.env.VUE_APP_EXTERNAL_SERVER

/**
 * 是否是外部链接，这里依赖于external url
 * @param {string} url
 */
export function isInternal(url) {
  // console.log('base url: ', baseUrl, url)
  return url.indexOf(baseUrl) !== -1
}
