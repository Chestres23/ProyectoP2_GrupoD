import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { lostItems as mockItems } from '../mocks/mockData'
import { listItems, deleteItem, getImageUrl } from '../services/itemApi'
import { getSessionUser } from '../services/authStorage'

function Badge({ status }) {
  const map = {
    FOUND: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    CLAIMED: 'bg-blue-100 text-blue-800 border border-blue-200',
    DELIVERED: 'bg-green-100 text-green-800 border border-green-200',
  }
  return (
    <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${map[status] || 'bg-gray-200'}`}>
      {status}
    </span>
  )
}

const PLACEHOLDER = 'https://images.unsplash.com/photo-1567958451986-2de427a4a0be?w=400&q=60'

export default function LostItems() {
  const [items, setItems] = useState(mockItems)
  const [error, setError] = useState('')
  const sessionUser = getSessionUser()
  const isAdmin = sessionUser?.role === 'ADMIN'

  const loadItems = async () => {
    try {
      const remoteItems = await listItems()
      setItems(remoteItems)
    } catch {
      setItems(mockItems)
    }
  }

  useEffect(() => { loadItems() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('¿Seguro que deseas eliminar este objeto?')) return
    try {
      await deleteItem(id)
      await loadItems()
    } catch (err) {
      setError(err?.response?.data?.message || 'No se pudo eliminar el objeto.')
    }
  }

  /** Decide qué src usar para la imagen del card */
  const resolveImage = (item) => {
    if (item.hasImage) return getImageUrl(item.id)
    if (item.imageUrl || item.image_url) return item.imageUrl || item.image_url
    return PLACEHOLDER
  }

  /** El botón "Editar" solo aparece si el usuario es dueño O es admin */
  const canEdit = (item) =>
    isAdmin || sessionUser?.id === item.reporterId

  return (
    <div className="space-y-6">
      <div className="rounded-[2rem] bg-white p-6 shadow-sm ring-1 ring-slate-200/70">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-sm uppercase tracking-[0.2em] text-sky-600">Gestión de objetos</p>
            <h1 className="text-3xl font-black text-slate-950">Objetos extraviados</h1>
            <p className="mt-2 text-sm text-slate-500">
              Cualquier usuario autenticado puede reportar un objeto nuevo o iniciar un reclamo.
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Link to="/reclamos"
              className="inline-flex items-center justify-center rounded-2xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800">
              Ver reclamos
            </Link>
            <Link to="/objetos/nuevo"
              className="inline-flex items-center justify-center rounded-2xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-emerald-500">
              + Registrar objeto
            </Link>
          </div>
        </div>
      </div>

      {error && (
        <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {items.map((item) => (
          <div key={item.id}
            className="overflow-hidden rounded-[1.75rem] border border-slate-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-xl">
            <img
              src={resolveImage(item)}
              alt={item.name}
              className="h-48 w-full object-cover"
              onError={(e) => { e.target.src = PLACEHOLDER }}
            />
            <div className="p-5">
              <div className="mb-2 flex items-center justify-between gap-2">
                <div className="font-bold text-slate-950">{item.name}</div>
                <Badge status={item.status} />
              </div>
              <div className="mb-4 text-sm text-slate-500">
                {item.category} • {item.locationFound || item.location_found}
              </div>

              <div className="flex flex-wrap gap-2">
                <Link to={`/objetos/${item.id}`}
                  className="rounded-2xl bg-slate-100 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-200">
                  Ver detalle
                </Link>
                {item.status === 'FOUND' && (
                  <Link to={`/reclamos/nuevo/${item.id}`}
                    className="rounded-2xl bg-sky-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-sky-500">
                    Reclamar
                  </Link>
                )}

                {/* Solo dueño o admin puede editar */}
                {canEdit(item) && (
                  <Link to={`/objetos/${item.id}/editar`}
                    className="rounded-2xl bg-amber-500 px-3 py-2 text-sm font-semibold text-white transition hover:bg-amber-400">
                    Editar
                  </Link>
                )}

                {/* Solo admin puede eliminar */}
                {isAdmin && (
                  <button onClick={() => handleDelete(item.id)}
                    className="rounded-2xl bg-red-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-red-500">
                    Eliminar
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
