import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
})

api.interceptors.request.use((config) => {
  const rawSession = localStorage.getItem('campuslost_user')
  if (rawSession) {
    try {
      const session = JSON.parse(rawSession)
      if (session?.token) {
        config.headers.Authorization = `Bearer ${session.token}`
      }
    } catch {
      localStorage.removeItem('campuslost_user')
    }
  }

  return config
})

export default api