import api from './api'

function getTokenFromSession() {
  const rawSession = localStorage.getItem('campuslost_user')
  if (!rawSession) return null

  try {
    const session = JSON.parse(rawSession)
    return session?.token || null
  } catch {
    return null
  }
}

function decodeJwtPayload(token) {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length !== 3) return null

  try {
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const decoded = atob(base64)
    return JSON.parse(decoded)
  } catch {
    return null
  }
}

export async function login(email, password) {
  const response = await api.post('/auth/login', { email, password })
  return response.data
}

export async function register(name, email, password) {
  const response = await api.post('/auth/register', { name, email, password })
  return response.data
}

export async function resolveCurrentUser(email) {
  const token = getTokenFromSession()
  if (!token) return null

  const payload = decodeJwtPayload(token)
  const resolvedEmail = email || payload?.sub || payload?.email
  if (!resolvedEmail) return null

  const isAdmin = resolvedEmail === 'admin@test.com'
  const fallbackId = isAdmin ? 1 : 2
  return {
    token,
    email: resolvedEmail,
    id: fallbackId,
    role: isAdmin ? 'ADMIN' : 'USER',
    isAdmin,
  }
}
