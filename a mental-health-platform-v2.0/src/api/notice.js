import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

export function addNotice(data) {
  return request({
    url: getAddress() + 'notice/addNotice',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function noticeList(data) {
  return request({
    url: getAddress() + 'notice/noticeList',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function getNoticeById(data) {
  return request({
    url: getAddress() + 'notice/getNotice',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 在upload中的action参数处改为这个url即可
// export function uploadFile(data) {
//   return request({
//     url: getAddress() + 'notice/noticeAppendix.upload',
//     method: 'post',
//     data
//   })
// }

export function deleteFile(data) {
  return request({
    url: getAddress() + 'notice/noticeAppendixDelete',
    method: 'post',
    data: Qs.stringify(data)
  })
}