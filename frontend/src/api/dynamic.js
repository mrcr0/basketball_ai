import axios from '../utils/axios'

export const createDynamic = (data) => {
  return axios.post('/dynamics', data)
}

export const getDynamics = () => {
  return axios.get('/dynamics')
}

export const getDynamicDetail = (id) => {
  return axios.get(`/dynamics/${id}`)
}

export const likeDynamic = (id) => {
  return axios.post(`/dynamics/${id}/like`)
}

export const addComment = (id, content, parentId) => {
  return axios.post(`/dynamics/${id}/comments`, { content, parentId })
}

export const shareDynamic = (id) => {
  return axios.post(`/dynamics/${id}/share`)
}

export const updateComment = (commentId, content) => {
  return axios.post(`/dynamics/comments/${commentId}/update`, { content })
}

export const deleteComment = (commentId) => {
  return axios.post(`/dynamics/comments/${commentId}/delete`)
}
