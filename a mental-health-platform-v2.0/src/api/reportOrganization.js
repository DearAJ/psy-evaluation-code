import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

// key-value的需要使用qs进行转换；row+json格式的不需要

// 生成个人报告，个人报告的数据
export function generatePersonalReport(data) {
  return request({
    url: getAddress() + 'task/personalReport',
    method: 'post',
    data
  })
}

// 省、市、区县、学校报告列表
export function getPersonalReportList(data) {
  return request({
    url: getAddress() + 'task/personalReportList',
    method: 'post',
    data
  })
}

// 查看团体报告列表
export function viewGroupReport(data) {
  return request({
    url: getAddress() + 'groupReport/viewGroupReport',
    method: 'post',
    data
  })
}

// 团体报告列表
export function getGroupReportList(data) {
  return request({
    url: getAddress() + 'groupReport/groupReportList',
    method: 'post',
    data
  })
}

// 添加团体报告
export function addGroupReport(data) {
  return request({
    url: getAddress() + 'groupReport/addGroupReport',
    method: 'post',
    data
  })
}