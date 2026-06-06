import axios from '../utils/axios'

export const askQuestion = (question) => {
  return axios.post('/ai/ask', question)
}

export const analyzeAction = (actionType, description) => {
  return axios.post('/ai/analyze-action', description, {
    params: { actionType }
  })
}

export const chat = (question, sessionId) => {
  return axios.post('/chat/send', {
    question,
    sessionId
  })
}

export const getConversations = () => {
  return axios.get('/chat/conversations')
}

export const getHistory = (sessionId) => {
  return axios.get(`/chat/history/${sessionId}`)
}

export const deleteConversation = (sessionId) => {
  return axios.delete(`/chat/conversation/${sessionId}`)
}

export const clearHistory = (sessionId) => {
  return axios.delete(`/chat/history/${sessionId}`)
}

export const askAgent = (question) => {
  return axios.post('/api/agent/ask', question)
}
