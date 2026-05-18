import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createItem, uploadImage } from '../services/itemApi'
import { getSessionUser } from '../services/authStorage'

export default function CreateItem() {
  const navigate = useNavigate()
  const sessionUser = getSessionUser()
  const [form, setForm] = useState({
    name: '',
    description: '',
    category: '',
    locationFound: '',
    dateFound: '',
  })
  const [imageFile, setImageFile] = useState(null)
  const [imagePreview, setImagePreview] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const updateField = (field, value) =>
    setForm((curr) => ({ ...curr, [field]: value }))

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    setImageFile(file)
    setImagePreview(URL.createObjectURL(file))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!sessionUser?.id) {
      setError('Debes iniciar sesión para registrar un objeto.')
      return
    }
    setLoading(true)
    setError('')
    try {
      const created = await createItem({ ...form, userId: sessionUser.id })
      // Si hay imagen seleccionada, subirla al objeto recién creado
      if (imageFile && created?.id) {
        await uploadImage(created.id, imageFile)
      }
      navigate('/objetos')
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'No se pudo registrar el objeto')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-3xl overflow-hidden rounded-[2rem] bg-white shadow-sm ring-1 ring-slate-200/70">
      <div className="border-b border-slate-200 bg-gradient-to-r from-emerald-600 to-slate-900 p-6 text-white">
        <p className="text-xs uppercase tracking-[0.25em] text-emerald-200">Nuevo objeto</p>
        <h2 className="mt-2 text-3xl font-black">Registrar objeto extraviado</h2>
        <p className="mt-2 text-sm text-emerald-100">Cualquier usuario autenticado puede reportar un nuevo hallazgo.</p>
      </div>

      <div className="p-6 sm:p-8">
        {error && (
          <div className="mb-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <form className="grid gap-4" onSubmit={handleSubmit}>
          <div className="grid gap-4 sm:grid-cols-2">
            <label className="block text-sm font-semibold text-slate-700">
              Nombre
              <input
                required
                value={form.name}
                onChange={(e) => updateField('name', e.target.value)}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-emerald-500 focus:bg-white focus:ring-4 focus:ring-emerald-100"
              />
            </label>
            <label className="block text-sm font-semibold text-slate-700">
              Categoría
              <input
                required
                value={form.category}
                onChange={(e) => updateField('category', e.target.value)}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-emerald-500 focus:bg-white focus:ring-4 focus:ring-emerald-100"
              />
            </label>
          </div>

          <label className="block text-sm font-semibold text-slate-700">
            Descripción
            <textarea
              required
              value={form.description}
              onChange={(e) => updateField('description', e.target.value)}
              className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-emerald-500 focus:bg-white focus:ring-4 focus:ring-emerald-100"
              rows="4"
            />
          </label>

          <div className="grid gap-4 sm:grid-cols-2">
            <label className="block text-sm font-semibold text-slate-700">
              Lugar encontrado
              <input
                required
                value={form.locationFound}
                onChange={(e) => updateField('locationFound', e.target.value)}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-emerald-500 focus:bg-white focus:ring-4 focus:ring-emerald-100"
              />
            </label>
            <label className="block text-sm font-semibold text-slate-700">
              Fecha encontrada
              <input
                required
                type="date"
                value={form.dateFound}
                onChange={(e) => updateField('dateFound', e.target.value)}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 p-3 outline-none focus:border-emerald-500 focus:bg-white focus:ring-4 focus:ring-emerald-100"
              />
            </label>
          </div>

          {/* Foto del objeto */}
          <div>
            <label className="block text-sm font-semibold text-slate-700">
              Foto del objeto
              <div className="mt-2 flex items-center gap-4">
                <label
                  htmlFor="item-image"
                  className="cursor-pointer rounded-2xl border-2 border-dashed border-slate-300 bg-slate-50 px-5 py-3 text-sm text-slate-500 transition hover:border-emerald-400 hover:text-emerald-600"
                >
                  📷 Seleccionar foto
                </label>
                <input
                  id="item-image"
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={handleFileChange}
                />
                {imageFile && (
                  <span className="text-xs text-slate-500 truncate max-w-[160px]">{imageFile.name}</span>
                )}
              </div>
            </label>
            {imagePreview && (
              <img
                src={imagePreview}
                alt="Vista previa"
                className="mt-3 h-40 w-full rounded-2xl object-cover border border-slate-200"
              />
            )}
          </div>

          <div className="flex flex-wrap gap-3">
            <button
              disabled={loading}
              className="rounded-2xl bg-slate-950 px-4 py-3 font-semibold text-white disabled:opacity-60 hover:bg-slate-800 transition"
            >
              {loading ? 'Guardando...' : 'Registrar objeto'}
            </button>
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="rounded-2xl bg-slate-100 px-4 py-3 font-semibold text-slate-700 hover:bg-slate-200 transition"
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}