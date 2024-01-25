import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import Blob from "@/excel/Blob.js"
import Export2Excel from "@/excel/Export2Excel.js"

import ElementUI from 'element-ui';
import './styles/element-variables.scss'

// iconfont
import './assets/iconfont/iconfont.css'

// 权限控制
import './permission' // permission control

// 加密
import JSEncrypy from 'jsencrypt'
Vue.prototype.$encryptByPublicKey = function(content) {
  let encrypt = new JSEncrypy();
  encrypt.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiqbmU6X+PC1sOVXGgIsLHLyg6K4Y/+EGY2hbNAG/5mj44ujDCM4xrpssxMqYvbM16lrIxGgE+VN3pqamkb8batCvM7kUoPEuaJ/p/cbyzjFG85LugjHlG2Tlq8uCzLqXVQk9NmfuMKVmdv1W1qPSbIkCWEJvVBWieThuuFFfXYeigZAW2X6Rj/r5AVKp5x6Pb4xHWPjs4aoZZ2DzboutlHPLCcfA39q5/IFMZrY09APaD+4Yfxoo1xCMcYDwQUeXMCuoRxrpcEpK7heD/gkIw3RG0ZNeuKI3DLzv5VT1kB3+N0OI2AKxc3wHGmExWKfjS5oRvto+B8+EabCsifGahwIDAQAB");
  
  return encrypt.encrypt(content);
}

Vue.use(ElementUI);
Vue.config.productionTip = false;

Vue.prototype.$mapMapping = require('./assets/mapMaping').mapping;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
