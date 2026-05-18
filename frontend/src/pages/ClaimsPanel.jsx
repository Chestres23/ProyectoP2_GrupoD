import React, { useEffect, useMemo, useState } from 'react'
import { approveClaim, deleteClaim, listClaims, rejectClaim } from '../services/claimApi'
import { getSessionUser } from '../services/authStorage'

const STATUS_LABELS = {
  PENDING: 'Pendiente',
  APPROVED: 'Aprobado',
  REJECTED: 'Rechazado',
}

const ITEM_STATUS_LABELS = {
  FOUND: 'Encontrado',
  CLAIMED: 'Reclamado',
  DELIVERED: 'Entregado',
}

function Badge({ status }) {
  const map = {
    PENDING: 'bg-orange-100 text-orange-800 border border-orange-200',
    APPROVED: 'bg-green-100 text-green-800 border border-green-200',
    REJECTED: 'bg-red-100 text-red-800 border border-red-200',
  }
  return (
    <span className={`rounded-full px-3 py-1 text-xs font-semibold ${map[status] || 'bg-gray-100 text-gray-700'}`}>
      {STATUS_LABELS[status] || status}
    </span>
  )
}

// ── Vista ADMIN: tarjeta completa ────────────────────────────────────────────
function AdminClaimCard({ claim, onAction }) {
  return (
    <div className="rounded-[1.75rem] bg-white p-5 shadow-sm ring-1 ring-slate-200/70">
      {/* Encabezado */}
      <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="text-lg font-black text-slate-950">{claim.itemName}</div>
          <div className="mt-0.5 flex flex-wrap gap-3 text-xs text-slate-500">
            <span>📦 {claim.itemCategory}</span>
            <span>📍 {claim.itemLocation}</span>
            <span>
              Estado del objeto:{' '}
              <strong className="text-slate-700">
                {ITEM_STATUS_LABELS[claim.itemStatus] || claim.itemStatus}
              </strong>
            </span>
          </div>
        </div>
        <Badge status={claim.status} />
      </div>

      {/* Observación */}
      <div className="mb-4 rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-700">
        <p className="mb-1 text-xs font-semibold uppercase tracking-wide text-slate-400">Observación</p>
        {claim.observation}
      </div>

      {/* Datos del solicitante */}
      <div className="mb-4 grid gap-1 rounded-2xl border border-slate-100 bg-white px-4 py-3 text-sm sm:grid-cols-2">
        <div>
          <span className="text-xs font-semibold text-slate-400">Solicitante</span>
          <p className="font-semibold text-slate-800">{claim.userName}</p>
        </div>
        <div>
          <span className="text-xs font-semibold text-slate-400">Correo</span>
          <p className="font-medium text-slate-700">{claim.userEmail}</p>
        </div>
        <div>
          <span className="text-xs font-semibold text-slate-400">Fecha de reclamo</span>
          <p className="text-slate-700">
            {claim.claimDate
              ? new Date(claim.claimDate).toLocaleString('es-EC', { dateStyle: 'medium', timeStyle: 'short' })
              : '—'}
          </p>
        </div>
      </div>

      {/* Acciones */}
      <div className="flex flex-wrap gap-2">
        {claim.status === 'PENDING' && (
          <>
            <button
              onClick={() => onAction(claim.id, 'APPROVE')}
              className="rounded-2xl bg-green-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-green-500"
            >
              ✓ Aprobar
            </button>
            <button
              onClick={() => onAction(claim.id, 'REJECT')}
              className="rounded-2xl bg-slate-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-500"
            >
              ✗ Rechazar
            </button>
          </>
        )}
        <button
          onClick={() => onAction(claim.id, 'DELETE')}
          className="rounded-2xl bg-red-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-red-500"
        >
          🗑 Eliminar
        </button>
      </div>
    </div>
  )
}

// ── Vista USER: tarjeta simplificada ────────────────────────────────────────
function UserClaimCard({ claim }) {
  return (
    <div className="flex flex-col gap-3 rounded-[1.75rem] bg-white p-5 shadow-sm ring-1 ring-slate-200/70 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div className="font-bold text-slate-950">{claim.itemName}</div>
        <div className="mt-1 text-sm text-slate-500">Solicitante: {claim.userName}</div>
      </div>
      <Badge status={claim.status} />
    </div>
  )
}

// ── Componente principal ─────────────────────────────────────────────────────
export default function ClaimsPanel() {
  const [claims, setClaims] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const sessionUser = getSessionUser()
  const isAdmin = sessionUser?.role === 'ADMIN'

  const loadClaims = async () => {
    setLoading(true)
    setError('')
    try {
      setClaims(await listClaims())
    } catch {
      setError('No se pudo cargar los reclamos desde el servidor.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadClaims() }, [])

  const handleAction = async (id, action) => {
    try {
      if (action === 'APPROVE') await approveClaim(id)
      else if (action === 'REJECT') await rejectClaim(id)
      else if (action === 'DELETE') {
        if (!window.confirm('¿Eliminar este reclamo del panel?')) return
        await deleteClaim(id)
      }
      await loadClaims()
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'No se pudo realizar la acción.')
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="rounded-[2rem] bg-white p-6 shadow-sm ring-1 ring-slate-200/70">
        <p className="text-sm uppercase tracking-[0.2em] text-sky-600">
          {isAdmin ? 'Administración' : 'Mis reclamos'}
        </p>
        <h1 className="mt-2 text-3xl font-black text-slate-950">Panel de reclamos</h1>
        <p className="mt-2 text-sm text-slate-500">
          {isAdmin
            ? 'Historial completo de reclamos con todos los detalles del solicitante y el objeto.'
            : 'Listado de reclamos registrados en el sistema.'}
        </p>
      </div>

      {error && (
        <div className="rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          {error}
        </div>
      )}

      {loading && (
        <div className="rounded-2xl bg-white p-6 shadow-sm ring-1 ring-slate-200/70 text-center text-slate-400">
          Cargando reclamos...
        </div>
      )}

      <div className="space-y-4">
        {!loading && claims.map((claim) =>
          isAdmin
            ? <AdminClaimCard key={claim.id} claim={claim} onAction={handleAction} />
            : <UserClaimCard key={claim.id} claim={claim} />
        )}

        {!loading && claims.length === 0 && (
          <div className="rounded-[1.75rem] bg-white p-10 text-center text-sm text-slate-400 shadow-sm ring-1 ring-slate-200/70">
            No hay reclamos registrados.
          </div>
        )}
      </div>
    </div>
  )
}
