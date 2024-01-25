// const base_host = "http://127.0.0.1:8008";     // 本地环境
// const base_host = "http://118.31.75.253:8071"; //服务器(绝对路径)

const base_host = "";                             //服务器(相对路径)

export function getBaseHost() { //
  return base_host;
}

export function getAddress() { // API
  return base_host + "/psy/";
}

export function getDownloadAddress() { // files
  return base_host + "/files/"
}
