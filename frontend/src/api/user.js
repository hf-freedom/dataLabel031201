import request from '../utils/request'

export function getUserList() {
  return request.get('/user/list')
}

export function getUserPage(params) {
  return request.get('/user/page', { params })
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function createUser(data) {
  return request.post('/user', data)
}

export function updateUser(data) {
  return request.put('/user', data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function bindRoles(userId, roleIds) {
  return request.post(`/user/${userId}/roles`, roleIds)
}
