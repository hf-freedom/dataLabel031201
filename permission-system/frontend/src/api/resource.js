import request from './request'

export const getResourceList = (params) => {
  return request.get('/resource/list', { params })
}

export const getResourceTree = () => {
  return request.get('/resource/tree')
}

export const getResourceAll = () => {
  return request.get('/resource/all')
}

export const getResourceById = (id) => {
  return request.get(`/resource/${id}`)
}

export const createResource = (data) => {
  return request.post('/resource', data)
}

export const updateResource = (data) => {
  return request.put('/resource', data)
}

export const deleteResource = (id) => {
  return request.delete(`/resource/${id}`)
}

export const enableResource = (id) => {
  return request.put(`/resource/${id}/enable`)
}

export const disableResource = (id) => {
  return request.put(`/resource/${id}/disable`)
}
