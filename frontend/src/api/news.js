import axios from '../utils/axios'

export const getNews = (params) => {
  return axios.get('/news', { params })
}

export const getLatestNews = (limit = 10) => {
  return axios.get('/news/latest', { params: { limit } })
}

export const getNewsById = (id) => {
  return axios.get(`/news/${id}`)
}

export const triggerNewsRefresh = () => {
  return axios.post('/news/refresh')
}

export const getTencentNews = () => {
  return axios.get('/news/tencent')
}
