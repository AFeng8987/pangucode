const process = require('child_process')
const config = require('./package.json')

buildAPK()
compressAPK()

function buildAPK() {
  const s = require('./android.sign.config')
  const command = `cordova build android --release -- --keystore=${s.keystore} --alias=${s.alias} --storePassword=${s.storePassword} --password=${s.password}`
  process.execSync(command, { stdio: [0, 1, 2] })
}
function compressAPK() {
  const file = './plugins/cordova-plugin-apkupdater/src/nodejs/create-manifest.js'
  const apkFile = './platforms/android/app/build/outputs/apk/release/app-release.apk'
  const target = './update'
  const version = config.version
  const chunkSize = '1M'
  var command = `node ${file} ${version} ${chunkSize} ${apkFile} ${target}`

  console.log('result command: ', command)

  process.execSync(command, { stdio: [0, 1, 2] })
}
