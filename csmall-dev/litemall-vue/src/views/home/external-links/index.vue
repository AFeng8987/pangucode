<template>
  <div>1111</div>
</template>

<script>
export default {
  name: 'External',
  created() {
    console.log(this.$route.query.link)
    this.openLink()
  },
  methods: {
    openLink() {
      const that = this
      const ATTR = 'location=no,toolbar=yes,toolbarposition=top,hidenavigationbuttons=yes,hideurlbar=yes,closebuttoncaption=X,lefttoright=yes'
      // const ATTR = 'location=no,toolbar=yes,toolbarposition=top,hidenavigationbuttons=yes,hideurlbar=yes,closebuttoncaption=X,lefttoright=yes'
      // // const devTypePlatform = device.platform
      // // if (devTypePlatform === 'Android') {
      // //   ATTR = 'location=yes, useWideViewPort=no,hideurlbar=yes,hidenavigationbuttons=yes,closebuttoncaption=X'
      // // } else if (devTypePlatform === 'iOS') {
      // //   ATTR = 'location=no,toolbar=yes,toolbarposition=top,hidenavigationbuttons=yes,hideurlbar=yes,closebuttoncaption=X,lefttoright=yes'
      // // }
      // const REF_VARIBLE = cordova.InAppBrowser.open(this.$route.query.link, '_blank', ATTR)
      // REF_VARIBLE.addEventListener('exit', function (SomeParams) {

      // }
      const REF_VARIBLE = cordova.InAppBrowser.open(this.$route.query.link, '_blank', ATTR)
      REF_VARIBLE.addEventListener('exit', function(SomeParams) {
        if (SomeParams.type === 'exit') {
          that.$router.back()
        }
      })
    }
  }
}
</script>
