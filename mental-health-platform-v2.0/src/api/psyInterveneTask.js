// 危机干预模块
import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

// key-value的需要使用qs进行转换；row+json格式的不需要

// 获取预警库信息
export function getWarningData(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getSchemePageDataByCondition',
    method: 'post',
    data
  })
}

export function getWarningStatistics(data) { 
  return request({
    url: getAddress() + 'psyIntervationTask/getIntervationStaRes',
    method: 'post',
    data
  })
}

export function getSaveRecordData(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/selectUnSubmitRecordOrCrisis',
    method: 'post',
    data
  })
}

// 添加干预记录或危机等级确认
export function addRecordData(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/addIntervationRecordOrCrisis',
    method: 'post',
    data
  })
}

// 获取某个人的所有干预过程
export function getAllInterveneDataById(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getIntervationContentByFileId',
    method: 'post',
    data
  })
}

// 添加干预方案
export function addPlanData(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/addIntervationPlan',
    method: 'post',
    data
  })
}

// 查找未提交的干预方案（其实就是获取未提交的干预方案的内容）
export function getPlanDetailById(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getPlanDetailById',
    method: 'post',
    data
  })
}

// 根据id提交保存的干预方案
export function updateIntervationPlanById(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/updateIntervationPlanById',
    method: 'post',
    data
  })
}

// 根据id获取干预记录
export function getRcordDetailById(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getRcordDetailById',
    method: 'post',
    data
  })
}

// 根据id提交保存的干预记录
export function updateRecordOrCrisisById(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/updateRecordOrCrisisById',
    method: 'post',
    data
  })
}

// 根据学生获取干预记录
export function getIntervationContentByStuId(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getIntervationContentByStuId',
    method: 'post',
    data
  })
}

// 获取预警库统计信息
export function getIntervationStudentInfo(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getIntervationStudentInfo',
    method: 'post',
    data
  })
}

// 获取预警库超时信息
export function getExpiringStudent(data) {
  return request({
    url: getAddress() + 'psyIntervationTask/getExpiringStudent',
    method: 'post',
    data
  })
}

// 获取词云统计
export function getHotWordList() {
  return request({
    url: getAddress() + 'psyIntervationTask/getHotWordList',
    method: 'get',
   })
}