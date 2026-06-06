import axios from '../utils/axios'

export const createRecord = (data) => {
  return axios.post('/records', data)
}

export const getUserRecords = () => {
  return axios.get('/records/user')
}

export const analyzeTrainingData = () => {
  return axios.get('/records/analyze')
}
