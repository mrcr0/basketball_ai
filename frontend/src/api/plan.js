import axios from '../utils/axios'

export const generateAIPlan = (data) => {
  return axios.post('/plans/ai-generate', data, { timeout: 120000 })
}

export const getUserPlans = () => {
  return axios.get('/plans/user')
}

export const getPlanById = (id) => {
  return axios.get(`/plans/${id}`)
}

export const updatePlan = (id, data) => {
  return axios.put(`/plans/${id}`, data)
}

export const deletePlan = (id) => {
  return axios.delete(`/plans/${id}`)
}
