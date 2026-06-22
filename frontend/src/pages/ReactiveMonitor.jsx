import React, { useState, useEffect } from 'react'

const API_BASE = 'http://localhost:8080/api'

export default function ReactiveMonitor() {
  const [stats, setStats] = useState(null)
  const [events, setEvents] = useState([])
  const [simulating, setSimulating] = useState(false)

  // Polling de estadísticas (Async Mono en backend)
  useEffect(() => {
    const fetchStats = async () => {
      try {
        const rawSession = localStorage.getItem('campuslost_user')
        const session = rawSession ? JSON.parse(rawSession) : null
        const token = session?.token

        const res = await fetch(`${API_BASE}/reactive/claims/stats`, {
          headers: {
            'Authorization': token ? `Bearer ${token}` : undefined
          }
        })
        if (res.ok) {
          const data = await res.json()
          setStats(data)
        }
      } catch (err) {
        console.error('Error al obtener estadísticas', err)
      }
    }

    fetchStats()
    const interval = setInterval(fetchStats, 5000) // cada 5 seg
    return () => clearInterval(interval)
  }, [])

  // Conexión SSE (EventSource) al hot stream
  useEffect(() => {
    // SSE no permite enviar Headers de Autorización fácilmente con el EventSource nativo.
    // Usualmente se envía el token por URL (?token=) o se exime de seguridad la ruta SSE para lectura.
    // Aquí el backend tiene .pathMatchers("/reactive/**").permitAll() configurado.
    
    const eventSource = new EventSource(`${API_BASE}/reactive/claims/stream`)

    eventSource.onmessage = (e) => {
      // Si recibimos mensaje default
    }

    eventSource.addEventListener('claim-event', (e) => {
      const data = JSON.parse(e.data)
      setEvents(prev => [data, ...prev].slice(0, 15)) // Guardar los últimos 15 eventos
    })

    eventSource.addEventListener('simulated-event', (e) => {
      const data = JSON.parse(e.data)
      setEvents(prev => [data, ...prev].slice(0, 15))
    })

    eventSource.onerror = (e) => {
      console.error("SSE error", e)
      eventSource.close()
    }

    return () => eventSource.close()
  }, [])

  const toggleSimulation = async () => {
    setSimulating(true)
    try {
      // Abrimos una conexión SSE corta de 15 segundos hacia el endpoint de simulación
      // Esto arrancará el Flux.interval del backend.
      const simSource = new EventSource(`${API_BASE}/reactive/claims/simulate`)
      simSource.addEventListener('simulated-event', (e) => {
        const data = JSON.parse(e.data)
        setEvents(prev => [data, ...prev].slice(0, 15))
      })
      
      // Cerrar la simulación después de 15s para no saturar
      setTimeout(() => {
        simSource.close()
        setSimulating(false)
      }, 15000)

    } catch (err) {
      console.error(err)
      setSimulating(false)
    }
  }

  const getStatusBadge = (status) => {
    const map = {
      CREATED: 'bg-blue-500',
      APPROVED: 'bg-green-500',
      REJECTED: 'bg-red-500',
      DELETED: 'bg-gray-500',
      PENDING: 'bg-yellow-500'
    }
    return map[status] || 'bg-slate-500'
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6 animate-in fade-in zoom-in-95 duration-500">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-4xl font-black tracking-tight text-slate-900">Monitor Reactivo (WebFlux)</h1>
          <p className="mt-2 text-lg text-slate-600">
            Estadísticas asíncronas y eventos SSE en tiempo real.
          </p>
        </div>
        <button
          onClick={toggleSimulation}
          disabled={simulating}
          className="rounded-2xl bg-indigo-600 px-6 py-3 font-semibold text-white shadow-lg shadow-indigo-600/30 transition hover:-translate-y-1 hover:bg-indigo-700 disabled:opacity-50 disabled:hover:translate-y-0"
        >
          {simulating ? 'Simulando...' : 'Simular Actividad'}
        </button>
      </header>

      {/* Estadísticas Asíncronas */}
      <section className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard title="Total Objetos" value={stats?.totalItems || 0} />
        <StatCard title="Reclamos Activos" value={stats?.totalClaims || 0} />
        <StatCard title="Reclamos Pendientes" value={stats?.pendingClaims || 0} />
        <StatCard title="Aprobados" value={stats?.approvedClaims || 0} color="text-emerald-600" />
        <StatCard title="Rechazados" value={stats?.rejectedClaims || 0} color="text-red-600" />
        <StatCard 
          title="Tasa de Aprobación" 
          value={`${stats?.approvalRate ? stats.approvalRate.toFixed(1) : 0}%`} 
          color="text-indigo-600" 
        />
      </section>

      {/* Stream SSE */}
      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-xl shadow-slate-200/40">
        <h2 className="mb-4 text-2xl font-bold">Stream de Eventos (SSE)</h2>
        <div className="space-y-3">
          {events.length === 0 ? (
            <p className="text-slate-500">Esperando eventos en vivo...</p>
          ) : (
            events.map((ev, idx) => (
              <div key={idx} className="flex items-center justify-between rounded-xl border border-slate-100 bg-slate-50 p-4 transition-all duration-300">
                <div className="flex flex-col">
                  <span className="font-semibold text-slate-900">
                    {ev.itemName} - Reclamado por {ev.userName}
                  </span>
                  <span className="text-xs text-slate-500">
                    ID: {ev.claimId} | {new Date(ev.timestamp).toLocaleTimeString()}
                  </span>
                </div>
                <div className={`rounded-full px-3 py-1 text-xs font-bold text-white shadow-sm ${getStatusBadge(ev.type)}`}>
                  {ev.type}
                </div>
              </div>
            ))
          )}
        </div>
      </section>
    </div>
  )
}

function StatCard({ title, value, color = "text-slate-900" }) {
  return (
    <div className="flex flex-col items-center justify-center rounded-3xl border border-slate-200 bg-white p-6 shadow-xl shadow-slate-200/40 transition hover:-translate-y-1">
      <div className="text-sm font-semibold uppercase tracking-wider text-slate-500">{title}</div>
      <div className={`mt-2 text-4xl font-black tracking-tighter ${color}`}>{value}</div>
    </div>
  )
}
