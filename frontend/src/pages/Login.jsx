import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, register, resolveCurrentUser } from '../services/authApi'
import { clearSessionUser, setSessionUser } from '../services/authStorage'

export default function Login() {
  const navigate = useNavigate()
  const [mode, setMode] = useState('login') // 'login' | 'register'
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const switchMode = (newMode) => {
    setMode(newMode)
    setError('')
    setName('')
    setEmail('')
    setPassword('')
    setConfirmPassword('')
  }

  const handleLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const authResponse = await login(email, password)
      const partialSession = { token: authResponse.token, email }
      setSessionUser(partialSession)

      try {
        const currentUser = await resolveCurrentUser(email)
        if (currentUser) {
          setSessionUser({ ...currentUser, token: authResponse.token })
        }
      } catch (err) {
        console.error('No se pudo resolver el usuario autenticado.', err)
      }

      navigate('/')
    } catch (err) {
      clearSessionUser()
      setError(err?.response?.data?.message || err.message || 'No se pudo iniciar sesión')
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.')
      return
    }
    if (password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres.')
      return
    }
    setLoading(true)
    setError('')
    try {
      const authResponse = await register(name, email, password)
      const partialSession = { token: authResponse.token, email }
      setSessionUser(partialSession)

      try {
        const currentUser = await resolveCurrentUser(email)
        if (currentUser) {
          setSessionUser({ ...currentUser, token: authResponse.token })
        }
      } catch (err) {
        console.error('No se pudo resolver el usuario registrado.', err)
      }

      navigate('/')
    } catch (err) {
      clearSessionUser()
      setError(err?.response?.data?.message || err.message || 'No se pudo registrar la cuenta')
    } finally {
      setLoading(false)
    }
  }

  const isLogin = mode === 'login'

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.18),_transparent_45%),linear-gradient(135deg,_#020617_0%,_#0f172a_45%,_#111827_100%)] px-4 py-8 text-white sm:px-6 lg:px-8">
      <div className="mx-auto grid min-h-[calc(100vh-4rem)] max-w-6xl items-center gap-8 lg:grid-cols-[1.2fr_0.8fr]">
        {/* Hero */}
        <section className="space-y-6">
          <div className="inline-flex rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-sky-300 backdrop-blur">
            CampusLost · Control de objetos perdidos
          </div>
          <div className="space-y-4">
            <h1 className="max-w-2xl text-4xl font-black tracking-tight sm:text-5xl">
              Reclamos, objetos y entrega en una sola interfaz moderna.
            </h1>
            <p className="max-w-xl text-base leading-7 text-slate-300 sm:text-lg">
              Ingresa para revisar objetos extraviados, registrar reclamos y gestionar entregas con un panel claro para personal y administradores.
            </p>
          </div>
        </section>

        {/* Card */}
        <div className="rounded-[2rem] border border-white/10 bg-white p-8 text-slate-900 shadow-2xl shadow-sky-950/30">
          {/* Tabs */}
          <div className="mb-6 flex rounded-2xl bg-slate-100 p-1">
            <button
              type="button"
              onClick={() => switchMode('login')}
              className={`flex-1 rounded-xl py-2 text-sm font-semibold transition ${
                isLogin ? 'bg-white shadow text-slate-900' : 'text-slate-500 hover:text-slate-700'
              }`}
            >
              Iniciar sesión
            </button>
            <button
              type="button"
              onClick={() => switchMode('register')}
              className={`flex-1 rounded-xl py-2 text-sm font-semibold transition ${
                !isLogin ? 'bg-white shadow text-slate-900' : 'text-slate-500 hover:text-slate-700'
              }`}
            >
              Crear cuenta
            </button>
          </div>

          {/* Header */}
          <div className="mb-6">
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-sky-700">
              {isLogin ? 'Bienvenido' : 'Registro gratuito'}
            </p>
            <h2 className="mt-2 text-3xl font-black text-slate-950">
              {isLogin ? 'Iniciar sesión' : 'Crear una cuenta'}
            </h2>
            <p className="mt-2 text-sm text-slate-500">
              {isLogin
                ? 'Accede con tus credenciales para continuar.'
                : 'Completa el formulario. Tu rol será Usuario.'}
            </p>
          </div>

          {error && (
            <div className="mb-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
              {error}
            </div>
          )}

          {/* Login form */}
          {isLogin && (
            <form onSubmit={handleLogin} className="space-y-4">
              <label className="block text-sm font-semibold text-slate-700">
                Correo electrónico
                <input
                  required
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="admin@test.com"
                />
              </label>
              <label className="block text-sm font-semibold text-slate-700">
                Contraseña
                <input
                  required
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="••••••••"
                />
              </label>
              <button
                disabled={loading}
                className="mt-2 w-full rounded-2xl bg-slate-950 px-4 py-3 font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {loading ? 'Ingresando...' : 'Entrar al sistema'}
              </button>
            </form>
          )}

          {/* Register form */}
          {!isLogin && (
            <form onSubmit={handleRegister} className="space-y-4">
              <label className="block text-sm font-semibold text-slate-700">
                Nombre completo
                <input
                  required
                  type="text"
                  minLength={3}
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="Juan Pérez"
                />
              </label>
              <label className="block text-sm font-semibold text-slate-700">
                Correo electrónico
                <input
                  required
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="juan@ejemplo.com"
                />
              </label>
              <label className="block text-sm font-semibold text-slate-700">
                Contraseña
                <input
                  required
                  type="password"
                  minLength={6}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="Mínimo 6 caracteres"
                />
              </label>
              <label className="block text-sm font-semibold text-slate-700">
                Confirmar contraseña
                <input
                  required
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition placeholder:text-slate-400 focus:border-sky-500 focus:bg-white focus:ring-4 focus:ring-sky-100"
                  placeholder="Repite tu contraseña"
                />
              </label>
              <div className="rounded-2xl border border-sky-100 bg-sky-50 px-4 py-2 text-xs text-sky-700">
                🔒 Tu cuenta se creará con rol <strong>Usuario</strong>. Solo los administradores pueden gestionar roles.
              </div>
              <button
                disabled={loading}
                className="mt-2 w-full rounded-2xl bg-sky-600 px-4 py-3 font-semibold text-white transition hover:bg-sky-500 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {loading ? 'Creando cuenta...' : 'Registrarme'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}
