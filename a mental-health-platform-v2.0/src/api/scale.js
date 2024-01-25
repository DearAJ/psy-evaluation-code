import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

// 生成自定义量表
export function addScale(data) {
  return request({
    url: getAddress() + 'scale/addScale',
    method: 'post',
    data
  })
}

// 获取量表list
export function getScaleList(data) {
  return request({
    url: getAddress() + 'scale/scaleList',
    method: 'post',
    data
  })
}

// 新增量表数据
export function getCustomScaleList(data) {
  return request({
    url: getAddress() + 'task/customTaskList',
    method: 'post',
    data
  })
}

// 删除自定义量表
export function deleteScale(data) {
  return request({
    url: getAddress() + 'scale/deleteScale',
    method: 'post',
    data
  })
}

// 修改自定义量表
export function updateScale(data) {
  return request({
    url: getAddress() + 'scale/updateScale',
    method: 'post',
    data
  })
}

// 学生端
// 获取学生的所有任务
export function getTasks(data) {
  return request({
    url: getAddress() + 'task/studentTaskList',
    method: 'post',
    data
  })
}

// 根据量表id获取量表
export function getScale(data) {
  return request({
    url: getAddress() + 'scale/getScale',
    method: 'post',
    data
  })
}

// 提交量表答案
export function submitAnswer(data) {
  return request({
    url: getAddress() + 'task/taskResult',
    method: 'post',
    data
  })
}