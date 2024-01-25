// 档案管理
import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

export function getStudentFiles(data) {
  return request({
    url: getAddress() + 'psyUserFilesTask/getAllStudentInfo',
    method: 'post',
    data
  })
}

export function getTeacherFiles(data) {
  return request({
    url: getAddress() + 'psyUserFilesTask/getAllTeacherInfo',
    method: 'post',
    data
  })
}

export function fileDetails(data) {
  return request({
    url: getAddress() + 'psyUserFilesTask/getUserDetailInfoById',
    method: 'post',
    data
  })
}

