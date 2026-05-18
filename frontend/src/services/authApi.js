import api from './api'

export async function login(email, password) {
  const response = await api.post('/auth/login', { email, password })
  return response.data
}

export async function register(name, email, password) {
  const response = await api.post('/auth/register', { name, email, password })
  return response.data
}

export async function resolveCurrentUser(email) {
  const response = await api.get('/users')
  return response.data.find((user) => user.email === email) || null
}