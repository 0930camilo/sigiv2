package sigiv.Backend.sigiv.Backend.controller;

import java.io.InputStream;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.services.CategoriaService;
import sigiv.Backend.sigiv.Backend.services.ProductoService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService; // Necesario para obtener la empresa de la categoría
    private final JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        return claims.get("id", Long.class);
    }

    private ResponseEntity<ApiResponse> checkPermissions(Long requiredEmpresaId, HttpServletRequest request) {
        Long tokenEmpresaId = getEmpresaIdFromToken(request);
        if (tokenEmpresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null)
            );
        }
        if (!tokenEmpresaId.equals(requiredEmpresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "No tienes permiso para acceder a este recurso", null)
            );
        }
        return null; // All good
    }

    @PostMapping("/crear-producto")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> crear(@RequestBody ProductoRequestDto dto, HttpServletRequest request) {
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(dto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        ProductoResponseDto created = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Producto creado correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        ProductoResponseDto producto = productoService.obtenerPorId(id);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(producto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Producto encontrado", producto)
        );
    }

    @GetMapping("/codigo-barra/{codigoBarra}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> obtenerPorCodigoBarra(@PathVariable String codigoBarra, HttpServletRequest request) {
        ProductoResponseDto producto = productoService.obtenerPorCodigoBarra(codigoBarra);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(producto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Producto encontrado", producto)
        );
    }

    @GetMapping("/codigo-barra/{codigoBarra}/imagen-base64")
    public ResponseEntity<ApiResponse<Object>> obtenerImagenBase64PorCodigo(@PathVariable String codigoBarra, HttpServletRequest request) {
        ProductoResponseDto producto = productoService.obtenerPorCodigoBarra(codigoBarra);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(producto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        String base64 = productoService.obtenerCodigoBarraBase64PorCodigo(codigoBarra);
        var data = new java.util.HashMap<String, Object>();
        data.put("codigoBarra", codigoBarra);
        data.put("imagenBase64", base64);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Imagen generada correctamente", data)
        );
    }

    @GetMapping("/{id}/codigo-barra/imagen-base64")
    public ResponseEntity<ApiResponse<Object>> obtenerImagenBase64PorId(@PathVariable Long id, HttpServletRequest request) {
        ProductoResponseDto producto = productoService.obtenerPorId(id);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(producto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        String base64 = productoService.obtenerCodigoBarraBase64PorId(id);
        var data = new java.util.HashMap<String, Object>();
        data.put("idProducto", id);
        data.put("imagenBase64", base64);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Imagen generada correctamente", data)
        );
    }

    @PutMapping("/update-producto/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> actualizar(
            @PathVariable Long id, @RequestBody ProductoRequestDto dto, HttpServletRequest request) {
        ProductoResponseDto productoExistente = productoService.obtenerPorId(id);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(productoExistente.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        ProductoResponseDto actualizado = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Producto actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-producto/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        ProductoResponseDto producto = productoService.obtenerPorId(id);
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(producto.getCategoriaId());
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        productoService.eliminarProducto(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Producto eliminado correctamente", null)
        );
    }

    @GetMapping("/empresa/{idEmpresa}")
    public ResponseEntity<ApiResponse<Object>> listarProductosEmpresa(
            @PathVariable Long idEmpresa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Producto.Estado estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) String codigoBarra,
            HttpServletRequest request) {

        ResponseEntity<ApiResponse> errorResponse = checkPermissions(idEmpresa, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        Page<ProductoResponseDto> productosPage =
                productoService.productosPorEmpresa(idEmpresa, page, size, estado, nombre, categoria, proveedor, codigoBarra);

        var data = new java.util.HashMap<String, Object>();
        data.put("totalPages", productosPage.getTotalPages());
        data.put("currentPage", productosPage.getNumber());
        data.put("totalElements", productosPage.getTotalElements());
        data.put("productos", productosPage.getContent());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Productos de la empresa " + idEmpresa + " listados correctamente", data)
        );
    }

    @PostMapping("/importar-excel")
    public ResponseEntity<ApiResponse<ProductoImportResultDto>> importarDesdeExcel(
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "El archivo está vacío", null));
            }

            String contentType = archivo.getContentType();
            if (contentType == null ||
                    (!contentType.contains("spreadsheetml") &&
                     !contentType.contains("excel") &&
                     !contentType.contains("octet-stream"))) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), "El archivo debe ser un Excel (.xlsx o .xls)", null));
            }

            InputStream is = archivo.getInputStream();
            ProductoImportResultDto resultado = productoService.importarDesdeExcel(is);

            String mensaje = String.format(
                    "Importación finalizada: %d procesadas, %d exitosas, %d con error",
                    resultado.getTotalFilas(), resultado.getExitosos(), resultado.getFallidos());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, HttpStatus.OK.value(), mensaje, resultado));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar el archivo: " + e.getMessage(), null));
        }
    }

    @GetMapping("/plantilla/empresa/{empresaId}")
    public ResponseEntity<?> descargarPlantillaExcel(@PathVariable Long empresaId, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        try {
            byte[] excelBytes = productoService.generarPlantillaExcel(empresaId);
            String base64 = java.util.Base64.getEncoder().encodeToString(excelBytes);

            var data = new java.util.HashMap<String, Object>();
            data.put("archivo", base64);
            data.put("nombreArchivo", "plantilla_productos_empresa_" + empresaId + ".xlsx");

            return ResponseEntity.ok(
                    new ApiResponse<>(true, HttpStatus.OK.value(), "Plantilla generada exitosamente", data));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al generar la plantilla: " + e.getMessage(), null));
        }
    }
}