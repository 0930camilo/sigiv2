package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.services.ProveedorService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping("/crear-proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> crear(@RequestBody ProveedorRequestDto dto) {
        ProveedorResponseDto created = proveedorService.crearProveedor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Proveedor creado correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> obtenerPorId(@PathVariable Long id) {
        ProveedorResponseDto proveedor = proveedorService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Proveedor encontrado", proveedor)
        );
    }

    @GetMapping("/list-proveedores")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDto>>> listar() {
        List<ProveedorResponseDto> proveedores = proveedorService.listarProveedores();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todos los proveedores listados", proveedores)
        );
    }

    @GetMapping("/list-proveedor-status")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Proveedor.Estado estado) {

        List<ProveedorResponseDto> proveedores;
        String message;

        if (estado != null) {
            proveedores = proveedorService.listarPorEstado(estado);
            message = "Proveedores listados por estado: " + estado;
        } else {
            proveedores = proveedorService.listarProveedores();
            message = "Todos los proveedores listados";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, proveedores)
        );
    }

    @PutMapping("/update-proveedor/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody ProveedorRequestDto dto) {
        ProveedorResponseDto actualizado = proveedorService.actualizarProveedor(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Proveedor actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-proveedor/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Proveedor eliminado correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> cambiarEstado(@PathVariable Long id) {
        ProveedorResponseDto actualizado = proveedorService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado del proveedor actualizado automáticamente", actualizado)
        );
}

@GetMapping("/buscar")
public ResponseEntity<List<ProveedorResponseDto>> buscarPorNombre(
        @RequestParam String nombre) {
    return ResponseEntity.ok(proveedorService.buscarPorNombre(nombre));
}


    @GetMapping("/empresa/{idEmpresa}")
public ResponseEntity<List<ProveedorResponseDto>> listarPorEmpresa(@PathVariable Long idEmpresa) {
    List<ProveedorResponseDto> respuesta = proveedorService.listarPorEmpresa(idEmpresa);
    return ResponseEntity.ok(respuesta);
} 


}
