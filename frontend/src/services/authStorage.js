const SESSION_KEY = 'campuslost_user'

function decodeJwtPayload(token) {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length !== 3) return null

  try {
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(json)
  } catch {
    return null
  }
}

export function getSessionUser() {
  const rawSession = localStorage.getItem(SESSION_KEY)

  if (!rawSession) {
    return null
  }

  try {
    const session = JSON.parse(rawSession)
    if (!session?.token) return null

    const payload = decodeJwtPayload(session.token) || {}
    const rawId = session.id ?? payload.id
    const id = typeof rawId === 'number'
      ? rawId
      : typeof rawId === 'string' && /^\d+$/.test(rawId)
        ? Number(rawId)
        : undefined
    const role = session.role || payload.role || (session.isAdmin ? 'ADMIN' : 'USER')
    const isAdmin = role === 'ADMIN' || session.isAdmin === true

    return {
      token: session.token,
      ...payload,
      role,
      isAdmin,
      email: payload.sub || payload.email,
      id,
    }
  } catch {
    localStorage.removeItem(SESSION_KEY)
    return null
  }
}

export function setSessionUser(sessionUser) {
  const safeSession = { ...sessionUser }
  if (safeSession.id != null) {
    const numericId = typeof safeSession.id === 'number'
      ? safeSession.id
      : typeof safeSession.id === 'string' && /^\d+$/.test(safeSession.id)
        ? Number(safeSession.id)
        : undefined

    if (numericId != null) {
      safeSession.id = numericId
    } else {
      delete safeSession.id
    }
  }

  if (safeSession.isAdmin && !safeSession.role) {
    safeSession.role = 'ADMIN'
  }

  localStorage.setItem(SESSION_KEY, JSON.stringify(safeSession))
}

export function clearSessionUser() {
  localStorage.removeItem(SESSION_KEY)
}
