import axios from '../utils/axios'

export const getKnowledge = (params) => {
  return axios.get('/knowledge', { params })
}

export const getKnowledgeById = (id) => {
  return axios.get(`/knowledge/${id}`)
}

export const getHotKnowledge = (limit = 10) => {
  return axios.get('/knowledge/hot', { params: { limit } })
}

export const getSubCategories = (category) => {
  return axios.get('/knowledge/subcategories', { params: { category } })
}
