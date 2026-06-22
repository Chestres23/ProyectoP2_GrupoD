import React from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import Sidebar from '../components/Sidebar'
import { clearSessionUser, getSessionUser } from '../services/authStorage'

export default function MainLayout(){
  const navigate = useNavigate()
  const sessionUser = getSessionUser()
  const isAdmin = sessionUser?.isAdmin

  const handleLogout = () => {
    clearSessionUser()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 lg:flex">
      <Sidebar onLogout={handleLogout} sessionUser={sessionUser} />
      <main className="flex-1 p-4 sm:p-6 lg:p-8">
        <header className="mb-6 flex flex-col gap-4 rounded-3xl bg-white/80 p-5 shadow-sm ring-1 ring-slate-200/70 backdrop-blur sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.2em] text-sky-600">CampusLost</p>
            <h1 className="text-2xl font-black text-slate-950 sm:text-3xl">Panel de gestión de objetos perdidos</h1>
            <p className="mt-1 text-sm text-slate-500">
              {isAdmin
                ? 'Vista administrativa para aprobar reclamos y revisar el historial.'
                : 'Vista de usuario para registrar objetos y crear reclamos.'}
            </p>
          </div>
          <div className="rounded-2xl bg-slate-950 px-4 py-3 text-white">
            <p className="text-xs uppercase tracking-[0.2em] text-slate-300">Sesión activa</p>
            <p className="font-semibold">{sessionUser?.name || sessionUser?.email || 'Invitado'}</p>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  )
}
