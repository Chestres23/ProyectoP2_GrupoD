import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { lostItems as mockItems } from '../mocks/mockData'
import { createClaim } from '../services/claimApi'
import { getItemById } from '../services/itemApi'
import { getSessionUser } from '../services/authStorage'

export default function CreateClaim(){
  const { itemId } = useParams()
  const navigate = useNavigate()
  const sessionUser = getSessionUser()
  const [item, setItem] = useState(mockItems.find((candidate) => candidate.id === Number(itemId)))
  const [observation, setObservation] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const loadItem = async () => {
      try {
        const remoteItem = await getItemById(itemId)
        setItem(remoteItem)
      } catch {
        setItem(mockItems.find((candidate) => candidate.id === Number(itemId)) || null)
      }
    }

    loadItem()
  }, [itemId])

  const handleSubmit = async (e)=>{
    e.preventDefault()

    if (!sessionUser?.token) {
      setError('Debes iniciar sesión para crear un reclamo.')
      return
    }

    const userId = sessionUser.id || sessionUser.sub || sessionUser.userId
    if (!userId) {
      setError('No se pudo obtener el identificador del usuario desde la sesión.')
      return
    }

    setLoading(true)
    setError('')

    try {
      await createClaim({
        userId,
        itemId: Number(itemId),
        observation
      })
      navigate('/reclamos')
    } catch (claimError) {
      setError(claimError?.response?.data?.message || claimError.message || 'No se pudo crear el reclamo')
    } finally {
      setLoading(false)
    }
  }

  if(!item) return <div>Item not found</div>
  return (
    <div className="max-w-3xl overflow-hidden rounded-[2rem] bg-white shadow-sm ring-1 ring-slate-200/70">
      <div className="border-b border-slate-200 bg-gradient-to-r from-sky-600 to-slate-900 p-6 text-white">
        <p className="text-xs uppercase tracking-[0.25em] text-sky-200">Nuevo reclamo</p>
        <h2 className="mt-2 text-3xl font-black">{item.name}</h2>
        <p className="mt-2 text-sm text-sky-100">Registra quién reclama el objeto y añade observaciones claras para el historial.</p>
      </div>
      <div className="p-6 sm:p-8">
        {error ? <div className="mb-4 rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div> : null}
        <form onSubmit={handleSubmit}>
          <label className="block mb-2 text-sm font-semibold text-slate-700">Observación
          <textarea value={observation} onChange={e=>setObservation(e.target.value)} required className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100" />
        </label>
        <div className="flex gap-2 mt-4">
          <button disabled={loading} className="rounded-2xl bg-slate-950 px-4 py-3 font-semibold text-white disabled:opacity-60">{loading ? 'Enviando...' : 'Guardar reclamo'}</button>
          <button type="button" onClick={()=>navigate(-1)} className="rounded-2xl bg-slate-100 px-4 py-3 font-semibold text-slate-700">Cancelar</button>
        </div>
        </form>
      </div>
    </div>
  )
}
