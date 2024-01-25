import { getAddress, getBaseHost } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

export function login(data) {
  return request({
    url: getAddress() + 'auth/login',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function getInfo() {
  return request({
    url: getAddress() + 'user/getUserInfo',
    method: 'get'
  })
}

export function updateInfo(data) {
  return request({
    url: getAddress() + 'user/updateManager',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function uploadAvatar(data) {
  return request({
    url: getBaseHost() + '/user/avatar.upload',
    method: 'post',
    data: data
  })
}

export function modifyPassword(data) {
  return request({
    url: getAddress() + 'user/modifyPass',
    method: 'post',
    data
  })
}