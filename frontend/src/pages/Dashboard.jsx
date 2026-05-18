import React, { useEffect, useState } from 'react'
import { claims as mockClaims, lostItems as mockItems } from '../mocks/mockData'
import { listClaims } from '../services/claimApi'
import { listItems } from '../services/itemApi'
import { getSessionUser } from '../services/authStorage'

function StatCard({title, value}){
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-1 hover:shadow-lg">
      <div className="text-sm font-medium text-slate-500">{title}</div>
      <div className="mt-2 text-3xl font-black text-slate-950">{value}</div>
    </div>
  )
}

export default function Dashboard(){
  const [items, setItems] = useState(mockItems)
  const [claims, setClaims] = useState(mockClaims)

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const [remoteItems, remoteClaims] = await Promise.all([listItems(), listClaims()])
        setItems(remoteItems)
        setClaims(remoteClaims)
      } catch {
        setItems(mockItems)
        setClaims(mockClaims)
      }
    }

    loadDashboardData()
  }, [])

  const totalFound = items.filter((item) => item.status === 'FOUND').length
  const pendingClaims = claims.filter((claim) => claim.status === 'PENDING').length
  const delivered = items.filter((item) => item.status === 'DELIVERED').length
  const sessionUser = getSessionUser()

  return (
    <div className="space-y-6">
      <section className="grid gap-4 rounded-[2rem] bg-gradient-to-br from-sky-600 via-sky-700 to-slate-900 p-6 text-white shadow-xl shadow-sky-900/20 lg:grid-cols-[1.4fr_0.6fr] lg:p-8">
        <div>
          <p className="text-sm uppercase tracking-[0.25em] text-sky-200">Resumen general</p>
          <h2 className="mt-2 text-3xl font-black">Hola, {sessionUser?.name || 'usuario'}.</h2>
          <p className="mt-3 max-w-2xl text-sm leading-6 text-sky-100/90 sm:text-base">
            Revisa los objetos extraviados, sigue los reclamos y gestiona las entregas desde un panel pensado para el personal universitario.
          </p>
        </div>
        <div className="rounded-3xl border border-white/10 bg-white/10 p-5 backdrop-blur">
          <p className="text-xs uppercase tracking-[0.2em] text-sky-200">Estado actual</p>
          <div className="mt-3 space-y-2 text-sm text-sky-50">
            <div className="flex items-center justify-between"><span>Objetos visibles</span><strong>{totalFound}</strong></div>
            <div className="flex items-center justify-between"><span>Reclamos pendientes</span><strong>{pendingClaims}</strong></div>
            <div className="flex items-center justify-between"><span>Entregados</span><strong>{delivered}</strong></div>
          </div>
        </div>
      </section>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard title="Objetos encontrados" value={totalFound} />
        <StatCard title="Reclamos pendientes" value={pendingClaims} />
        <StatCard title="Objetos entregados" value={delivered} />
      </div>
    </div>
  )
}
