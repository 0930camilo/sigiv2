package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.services.ProductoService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping("/crear-producto")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> crear(@RequestBody ProductoRequestDto dto) {
        ProductoResponseDto created = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Producto creado correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> obtenerPorId(@PathVariable Long id) {
        ProductoResponseDto producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Producto encontrado", producto)
        );
    }

    @GetMapping("/list-productos")
    public ResponseEntity<ApiResponse<List<ProductoResponseDto>>> listar() {
        List<ProductoResponseDto> productos = productoService.listarProductos();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todos los productos listados", productos)
        );
    }

    @GetMapping("/list-producto-status")
    public ResponseEntity<ApiResponse<List<ProductoResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Producto.Estado estado) {

        List<ProductoResponseDto> productos;
        String message;

        if (estado != null) {
            productos = productoService.listarPorEstado(estado);
            message = "Productos listados por estado: " + estado;
        } else {
            productos = productoService.listarProductos();
            message = "Todos los productos listados";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, productos)
        );
    }

    @PutMapping("/update-producto/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoRequestDto dto) {
        ProductoResponseDto actualizado = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Producto actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-producto/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Producto eliminado correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDto>> cambiarEstado(@PathVariable Long id) {
        ProductoResponseDto actualizado = productoService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado del producto actualizado automáticamente", actualizado)
        );
}

@GetMapping("/buscar")
public ResponseEntity<List<ProductoResponseDto>> buscarPorNombre(
        @RequestParam String nombre) {
    return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
}
}
