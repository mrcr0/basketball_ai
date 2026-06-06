import axios from '../utils/axios'

export const getTactics = (params) => {
  return axios.get('/tactics', { params })
}

export const getTacticById = (id) => {
  return axios.get(`/tactics/${id}`)
}

export const explainTactic = (name) => {
  return axios.get('/tactics/explain', { params: { name } })
}
