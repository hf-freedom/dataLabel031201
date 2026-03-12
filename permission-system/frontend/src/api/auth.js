import request from './request'

export const login = (data) => {
  return request.post('/auth/login', data)
}

export const getUserMenus = () => {
  return request.get('/resource/user/menus')
}

export const getUserPermissions = () => {
  return request.get('/resource/user/permissions')
}
