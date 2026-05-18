import api from './api'

export async function listClaims() {
  const response = await api.get('/claims')
  return response.data
}

export async function createClaim(payload) {
  const response = await api.post('/claims', payload)
  return response.data
}

export async function approveClaim(id) {
  const response = await api.patch(`/claims/${id}/approve`)
  return response.data
}

export async function rejectClaim(id) {
  const response = await api.patch(`/claims/${id}/reject`)
  return response.data
}

export async function deleteClaim(id) {
  await api.delete(`/claims/${id}`)
}