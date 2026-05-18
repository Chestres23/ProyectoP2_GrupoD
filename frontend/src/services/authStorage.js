const SESSION_KEY = 'campuslost_user'

export function getSessionUser() {
  const rawSession = localStorage.getItem(SESSION_KEY)

  if (!rawSession) {
    return null
  }

  try {
    return JSON.parse(rawSession)
  } catch {
    localStorage.removeItem(SESSION_KEY)
    return null
  }
}

export function setSessionUser(sessionUser) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(sessionUser))
}

export function clearSessionUser() {
  localStorage.removeItem(SESSION_KEY)
}