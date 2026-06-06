import axios from '../utils/axios'

export const getActions = (params) => {
  return axios.get('/actions', { params })
}

export const getActionById = (id) => {
  return axios.get(`/actions/${id}`)
}
