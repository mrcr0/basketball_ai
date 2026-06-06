import axios from '../utils/axios'

export const register = (data) => {
  return axios.post('/users/register', data)
}

export const login = (data) => {
  return axios.post('/users/login', data)
}

export const getProfile = () => {
  return axios.get('/users/profile')
}

export const updateProfile = (data) => {
  return axios.put('/users/profile', data)
}

export const forgotPassword = (data) => {
  return axios.post('/users/forgot-password', data)
}

export const validateResetToken = (token) => {
  return axios.get('/users/reset-token/validate', { params: { token } })
}

export const resetPassword = (data) => {
  return axios.post('/users/reset-password', data)
}
