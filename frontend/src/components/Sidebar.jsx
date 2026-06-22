import React from 'react'
import { NavLink } from 'react-router-dom'

const nav = [
  {to:'/', label:'Inicio'},
  {to:'/objetos', label:'Objetos extraviados'},
  {to:'/objetos/nuevo', label:'Registrar objeto'},
  {to:'/reclamos', label:'Reclamos'},
  {to:'/monitor', label:'Monitor Reactivo'}
]

export default function Sidebar({ onLogout, sessionUser }){
  return (
    <aside className="border-b border-slate-200 bg-slate-950 text-white lg:min-h-screen lg:w-80 lg:border-b-0 lg:border-r lg:border-slate-800">
      <div className="border-b border-slate-800 bg-gradient-to-br from-slate-900 to-slate-950 p-6">
        <div className="text-xs uppercase tracking-[0.25em] text-sky-400">Sistema universitario</div>
        <div className="mt-2 text-2xl font-black tracking-tight">CampusLost</div>
        <p className="mt-2 text-sm text-slate-300">Gestión de objetos perdidos y reclamos con control administrativo.</p>
        <div className="mt-4 rounded-2xl bg-white/10 p-4 ring-1 ring-white/10">
          <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Usuario</p>
          <p className="mt-1 font-semibold">{sessionUser?.name || sessionUser?.email}</p>
          <p className="text-sm text-slate-300">{sessionUser?.role === 'ADMIN' ? 'Administrador' : 'Usuario'}</p>
        </div>
      </div>
      <nav className="space-y-2 p-4">
        {nav.map(n => (
          <NavLink
            key={n.to}
            to={n.to}
            className={({isActive}) =>
              isActive
                ? 'block rounded-2xl bg-sky-500 px-4 py-3 font-semibold text-white shadow-lg shadow-sky-500/20'
                : 'block rounded-2xl px-4 py-3 text-slate-300 transition hover:bg-white/10 hover:text-white'
            }
          >
            {n.label}
          </NavLink>
        ))}
        <button
          onClick={onLogout}
          className="mt-4 w-full rounded-2xl border border-slate-700 px-4 py-3 text-left text-slate-200 transition hover:border-red-400 hover:bg-red-500/10 hover:text-white"
        >
          Cerrar sesión
        </button>
      </nav>
    </aside>
  )
}
