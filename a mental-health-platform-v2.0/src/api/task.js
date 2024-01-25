import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

// key-value的需要使用qs进行转换；row+json格式的不需要

// 学校端相关
export function getSchoolTasks(data) {
  return request({
    url: getAddress() + 'task/schoolTaskList',
    method: 'post',
    data
  })
}

export function verifyScaleData(data) {
  return request({
    url: getAddress() + 'scale/verifyScaleData',
    method: 'post',
    data: data
  })
}

export function batchAddScaleData(data) {
  return request({
    url: getAddress() + 'scale/batchAddScaleData',
    method: 'post',
    data
  })
}

export function downloadScaleData() {
  window.open(getDownloadAddress() + 'template/XX区县-XX学校-任务名-模板.xlsx');
}

export function taskExecution(data) {
  return request({
    url: getAddress() + 'task/taskExecution',
    method: 'post',
    data
  })
}

export function schoolTaskCompletion(data) {
  return request({
    url: getAddress() + 'task/schoolTaskCompletion',
    method: 'post',
    data
  })
}

export function schoolLatestTask(data) {
  return request({
    url: getAddress() + 'task/schoolLatestTask',
    method: 'post',
    data
  })
}

// 区县端

// 省、市、区、校任务进度（省市区进度和预警的表格）
export function taskProgress(data) {
  return request({
    url: getAddress() + 'task/taskProgress',
    method: 'post',
    data
  })
}

// 省市区县端
export function taskStatistics(data) {
  return request({
    url: getAddress() + 'task/taskStatistics',
    method: 'post',
    data
  })
}

// 获取最新任务
export function countyLatestTask(data) {
  return request({
    url: getAddress() + 'task/countyLatestTask',
    method: 'post',
    data
  })
}

// 市级
// 获取最新任务
export function cityLatestTask(data) {
  return request({
    url: getAddress() + 'task/cityTaskStatistics',
    method: 'post',
    data
  })
}

// 市级获取最新任务预警
export function cityLatestWarn(data) {
  return request({
    url: getAddress() + 'task/cityTaskWarningStatistics',
    method: 'post',
    data
  })
}

// 省市区获取任务列表
export function taskList(data) {
  return request({
    url: getAddress() + 'task/taskList',
    method: 'post',
    data
  })
}

// 学校提交任务
export function submitTask(data) {
  return request({
    url: getAddress() + 'task/submitTask',
    method: 'post',
    data
  })
}

// 下发任务
export function addTask(data) {
  return request({
    url: getAddress() + 'task/addTask',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 根据userId获取任务列表
export function getTaskByUserId(data) {
  return request({
    url: getAddress() + 'task/personalReportListByUserId',
    method: 'post',
    data
  })
}

// 导出自定义量表的原始数据
export function getTaskRawData(data) {
  return request({
    url: getAddress() + 'task/getTaskRawData',
    method: 'post',
    data
  })
}

// 省市区修改任务结束时间
export function updateEndTime(data) {
  return request({
    url: getAddress() + 'task/updateTaskEndDate',
    method: 'post',
    data
  })
}

// 省市区修改任务结束时间
export function resetTask(data) {
  return request({
    url: getAddress() + 'task/resetTask',
    method: 'post',
    data
  })
}

// 省市区学校最新任务
export function latestTask(data) {
  return request({
    url: getAddress() + 'task/latestTask',
    method: 'post',
    data
  })
}

// 学校最新任务的各班级统计
export function schoolTaskProgress(data) {
  return request({
    url: getAddress() + 'task/schoolTaskProgress',
    method: 'post',
    data
  })
}



// 全部原始数据
export function getAllOriginalData(data) {
  return request({
    url: getAddress() + 'task/getAllOriginalData',
    method: 'post',
    data
  })
}

// 学校最新任务的各班级统计
export function allUnitTaskProcess(data) {
  return request({
    url: getAddress() + 'task/allUnitTaskProcess',
    method: 'post',
    data
  })
}