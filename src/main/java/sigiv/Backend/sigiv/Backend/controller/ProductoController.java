package sigiv.Backend.sigiv.Backend.controller;



import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;

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

    
    
@GetMapping("/empresa/{idEmpresa}")
public ResponseEntity<ApiResponse<Object>> listarProductosEmpresa(
        @PathVariable Long idEmpresa,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Producto.Estado estado,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String proveedor
        ) {

    Page<ProductoResponseDto> productosPage =
            productoService.productosPorEmpresa(idEmpresa, page, size, estado, nombre, categoria, proveedor);

    var data = new java.util.HashMap<String, Object>();
    data.put("totalPages", productosPage.getTotalPages());
    data.put("currentPage", productosPage.getNumber());
    data.put("totalElements", productosPage.getTotalElements());
    data.put("productos", productosPage.getContent());

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Productos de la empresa " + idEmpresa + " listados correctamente",
                    data
            )
    );
}

}
