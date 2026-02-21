package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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

 




    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Proveedor.Estado estado,
            @RequestParam(required = false) String nombre
    ) {

        Page<ProveedorResponseDto> proveedoresPage =
                proveedorService.listarProveedoresPorEmpresa(
                        empresaId,
                        page,
                        size,
                        estado,
                        nombre
                );

        Map<String, Object> data = new HashMap<>();
        data.put("proveedores", proveedoresPage.getContent());
        data.put("totalElements", proveedoresPage.getTotalElements());
        data.put("totalPages", proveedoresPage.getTotalPages());
        data.put("currentPage", proveedoresPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Proveedores de la empresa " + empresaId + " listados correctamente",
                        data
                )
        );
    }
}


