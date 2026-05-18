import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import LostItems from './pages/LostItems'
import ItemDetail from './pages/ItemDetail'
import CreateClaim from './pages/CreateClaim'
import CreateItem from './pages/CreateItem'
import EditItem from './pages/EditItem'
import ClaimsPanel from './pages/ClaimsPanel'
import ProtectedRoute from './components/ProtectedRoute'
import PublicRoute from './components/PublicRoute'

export default function App(){
  return (
    <Routes>
      <Route element={<PublicRoute/>}>
        <Route path="/login" element={<Login/>} />
      </Route>
      <Route element={<ProtectedRoute/>}>
        <Route path="/" element={<MainLayout/>}>
          <Route index element={<Dashboard/>} />
          <Route path="objetos" element={<LostItems/>} />
          <Route path="objetos/nuevo" element={<CreateItem/>} />
          <Route path="objetos/:id" element={<ItemDetail/>} />
          <Route path="objetos/:id/editar" element={<EditItem/>} />
          <Route path="reclamos/nuevo/:itemId" element={<CreateClaim/>} />
          <Route path="reclamos" element={<ClaimsPanel/>} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

