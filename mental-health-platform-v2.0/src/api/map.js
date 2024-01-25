import request from "@/utils/request"
import getBaseHost from "./config";

// data格式为：'zhejiang'、'hangzhou'……
export function getMap(data) {
  let url = getBaseHost() + "/files/json/" + data + '.json';
  return request({
    url: url,
    method: 'get'
  })
}