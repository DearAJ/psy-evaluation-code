import { getAddress, getDownloadAddress } from "./config";
import request from "@/utils/request"
import Qs from 'qs'

// key-value的需要使用qs进行转换；row+json格式的不需要

// 获取三端的学校，分页
export function getSchoolData(data) {
  return request({
    url: getAddress() + 'manage/schoolList',
    method: 'post',
    data
  })
}

// 获取省市区所有的学校，不分页
export function getAllSchool(data) {
  return request({
    url: getAddress() + 'manage/schoolListAll',
    method: 'post',
    data
  })
}

// 获取有学校的区县列表
export function getAllCounty(data) {
  return request({
    url: getAddress() + 'manage/getAllCounty',
    method: 'post',
    data
  })
}

// 获取有学校的市列表
export function getAllCity(data) {
  return request({
    url: getAddress() + 'manage/getAllCity',
    method: 'post',
    data
  })
}

export function addSchool(data) {
  return request({
    url: getAddress() + 'manage/addSchool',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function deleteSchool(data) {
  return request({
    url: getAddress() + 'manage/deleteSchool',
    method: 'post',
    data
  })
}

export function updateSchool(data) {
  return request({
    url: getAddress() + 'manage/updateSchool',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function verifySchoolExcel(data) {
  return request({
    url: getAddress() + 'manage/verifySchoolExcel',
    method: 'post',
    data: data
  })
}

export function addSchools(data) {
  return request({
    url: getAddress() + 'manage/addSchools',
    method: 'post',
    data
  })
}

export function getStudentData(data) {
  return request({
    url: getAddress() + 'manage/studentList',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function addStudent(data) {
  return request({
    url: getAddress() + 'manage/addStudent',
    method: 'post',
    data: Qs.stringify(data),
  })
}

// 批量上传学生
export function addStudents(data) {
  return request({
    url: getAddress() + 'manage/addStudents',
    method: 'post',
    data
  })
}

export function  updateStudent(data) {
  return request({
    url: getAddress() + 'manage/updateStudent',
    method: 'post',
    data: Qs.stringify(data)
  })
}

export function  deleteStudent(data) {
  return request({
    url: getAddress() + 'manage/deleteStudent',
    method: 'post',
    data
  })
}

// 上传学生表格
export function verifyStudentExcel(data) {
  return request({
    url: getAddress() + 'manage/verifyStudentExcel',
    method: 'post',
    data: data
  })
}

export function downloadStudentTemplate() {
  window.open(getDownloadAddress() + 'template/学校学生组织管理录入模板.xlsx');
}

export function downloadSchoolTemplate() {
  window.open(getDownloadAddress() + 'template/区县组织管理录入模板.xlsx');
}

export function getClasses(data) {
  return request({
    url: getAddress() + 'manage/getClasses',
    method: 'post',
    data
  })
}

// 获取某个学校所有的班级
export function getAllClasses(data) {
  return request({
    url: getAddress() + 'manage/getAllClasses',
    method: 'post',
    data
  })
}

// 市端
export function countyList(data) {
  return request({
    url: getAddress() + 'manage/countyList',
    method: 'post',
    data
  })
}

export function schoolNameListByCounty(data) {
  return request({
    url: getAddress() + 'manage/schoolNameList',
    method: 'post',
    data
  })
}

// 学校端-教师管理
// 获取教师数据
export function getTeacherData(data) {
  return request({
    url: getAddress() + 'manage/teacherList',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 下载教师管理模板
export function downloadTeacherTemplate() {
  window.open(getDownloadAddress() + 'template/学校教师组织管理录入模板.xlsx');
}

// 验证上传表格信息
export function verifyTeacherExcel(data) {
  return request({
    url: getAddress() + 'manage/verifyTeacherExcel',
    method: 'post',
    data: data
  })
}

// 批量上传教师
export function addTeachers(data) {
  return request({
    url: getAddress() + 'manage/addTeachers',
    method: 'post',
    data
  })
}

// 添加老师
export function addTeacher(data) {
  return request({
    url: getAddress() + 'manage/addTeacher',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 编辑教师
export function updateTeacher(data) {
  return request({
    url: getAddress() + 'manage/updateTeacher',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 删除教师
export function deleteTeacher(data) {
  return request({
    url: getAddress() + 'manage/deleteTeacher',
    method: 'post',
    data
  })
}

// 学校端-角色管理
export function getSchoolRoleData(data) {
  return request({
    url: getAddress() + 'manage/schoolRoleList',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 添加角色
export function addSchoolRole(data) {
  return request({
    url: getAddress() + 'manage/addSchoolRole',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 编辑角色
export function updateSchoolRole(data) {
  return request({
    url: getAddress() + 'manage/updateSchoolRole',
    method: 'post',
    data: Qs.stringify(data)
  })
}

// 删除教师
export function deleteSchoolRole(data) {
  return request({
    url: getAddress() + 'manage/deleteSchoolRole',
    method: 'post',
    data
  })
}

// 首页数据面板
export function getDataStatistics(data) {
  return request({
    url: getAddress() + 'manage/getDataStatistics',
    method: 'post',
    data
  })
}