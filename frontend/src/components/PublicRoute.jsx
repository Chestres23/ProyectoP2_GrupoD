import React from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { getSessionUser } from '../services/authStorage'

export default function PublicRoute() {
  const sessionUser = getSessionUser()

  if (sessionUser?.token) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}