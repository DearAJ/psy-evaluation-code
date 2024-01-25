// 配置alias
const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

module.exports = {
  lintOnSave: true,
  chainWebpack: (config)=>{
      config.resolve.alias
          .set('@', resolve('src'))
  },
  devServer : {
    hot: true,//自动保存
    open: true,//自动启动
    port: 5000,//默认端口号
    host: "0.0.0.0"
  }
}