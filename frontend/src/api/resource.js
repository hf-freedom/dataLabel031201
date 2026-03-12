import request from '../utils/request'

export function getResourceList() {
  return request.get('/resource/list')
}

export function getResourcePage(params) {
  return request.get('/resource/page', { params })
}

export function getResourceById(id) {
  return request.get(`/resource/${id}`)
}

export function createResource(data) {
  return request.post('/resource', data)
}

export function updateResource(data) {
  return request.put('/resource', data)
}

export function deleteResource(id) {
  return request.delete(`/resource/${id}`)
}

export function enableResource(id) {
  return request.put(`/resource/${id}/enable`)
}

export function disableResource(id) {
  return request.put(`/resource/${id}/disable`)
}

export function getResourceByType(type) {
  return request.get(`/resource/type/${type}`)
}

export function getResourceByParentId(parentId) {
  return request.get(`/resource/parent/${parentId}`)
}

export function getUserMenus(userId) {
  return request.get(`/resource/user/${userId}/menus`)
}

export function getUserResources(userId) {
  return request.get(`/resource/user/${userId}/resources`)
}
