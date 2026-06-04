# SIGIV Backend - Codigo de barras

Este proyecto expone endpoints para generar codigos de barras en base64 (PNG) con formato CODE_128.

## Endpoint base64

- Por codigo de barras:
  - `GET /productos/codigo-barra/{codigoBarra}/imagen-base64`
- Por id de producto:
  - `GET /productos/{id}/codigo-barra/imagen-base64`

La respuesta incluye el campo `imagenBase64` (PNG) y puede ser usado por el frontend para decodificar o mostrar la imagen.

## Pruebas rapidas

```powershell
.\mvnw.cmd -q test
```

