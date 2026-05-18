# CampusLost - Frontend

Frontend con Vite + React + Tailwind, conectado al backend de Spring Boot cuando está disponible. Si el backend no responde, algunas vistas siguen mostrando datos mock locales para no romper la interfaz.

Instalación y ejecución:

```bash
cd GrupoD_Proyecto/frontend
npm install
npm run dev
```

Notas:
- El backend base debe estar corriendo en `http://localhost:8080`.
- El frontend consume la API en `http://localhost:8080/api` por defecto.
- Para probar login usa los usuarios sembrados por backend, por ejemplo `admin@test.com` / `123` o `user@test.com` / `123`.
- El objetivo principal sigue siendo el módulo de Reclamos (Claims).
