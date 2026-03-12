import request from './request'

export const getRoleList = (params) => {
  return request.get('/role/list', { params })
}

export const getRoleAll = () => {
  return request.get('/role/all')
}

export const getRoleById = (id) => {
  return request.get(`/role/${id}`)
}

export const createRole = (data) => {
  return request.post('/role', data)
}

export const updateRole = (data) => {
  return request.put('/role', data)
}

export const deleteRole = (id) => {
  return request.delete(`/role/${id}`)
}

export const bindRoleResources = (id, resourceIds) => {
  return request.post(`/role/${id}/resources`, resourceIds)
}

export const getRoleResources = (id) => {
  return request.get(`/role/${id}/resources`)
}
