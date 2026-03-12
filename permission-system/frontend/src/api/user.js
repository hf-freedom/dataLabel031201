import request from './request'

export const getUserList = (params) => {
  return request.get('/user/list', { params })
}

export const getUserAll = () => {
  return request.get('/user/all')
}

export const getUserById = (id) => {
  return request.get(`/user/${id}`)
}

export const createUser = (data) => {
  return request.post('/user', data)
}

export const updateUser = (data) => {
  return request.put('/user', data)
}

export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}

export const bindUserRoles = (id, roleIds) => {
  return request.post(`/user/${id}/roles`, roleIds)
}

export const getUserRoles = (id) => {
  return request.get(`/user/${id}/roles`)
}
