package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.services.CategoriaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping("/crear-categoria")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> crear(@RequestBody CategoriaRequestDto dto) {
        CategoriaResponseDto created = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Categoria creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> obtenerPorId(@PathVariable Long id) {
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Categoria encontrada", categoria)
        );
    }

    @GetMapping("/list-categorias")
    public ResponseEntity<ApiResponse<List<CategoriaResponseDto>>> listar() {
        List<CategoriaResponseDto> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las categorias listadas", categorias)
        );
    }

    @GetMapping("/list-categoria-status")
    public ResponseEntity<ApiResponse<List<CategoriaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Categoria.Estado estado) {

        List<CategoriaResponseDto> categorias;
        String message;

        if (estado != null) {
            categorias = categoriaService.listarPorEstado(estado);
            message = "Categorias listadas por estado: " + estado;
        } else {
            categorias = categoriaService.listarCategorias();
            message = "Todas las categorias listadas";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, categorias)
        );
    }

    @PutMapping("/update-categoria/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody CategoriaRequestDto dto) {
        CategoriaResponseDto actualizado = categoriaService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Categoria actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-categoria/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Categoria eliminada correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> cambiarEstado(@PathVariable Long id) {
        CategoriaResponseDto actualizado = categoriaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado de la categoria actualizado automáticamente", actualizado)
        );
}

 // 🔥 **ENDPOINT FALTANTE (igual que proveedores)**
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponseDto>> buscarPorNombre(
            @RequestParam String nombre) {
        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }

    @GetMapping("/empresa/{idEmpresa}")
public ResponseEntity<List<CategoriaResponseDto>> listarPorEmpresa(@PathVariable Long idEmpresa) {
    List<CategoriaResponseDto> respuesta = categoriaService.listarPorEmpresa(idEmpresa);
    return ResponseEntity.ok(respuesta);
} 

}
