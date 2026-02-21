package sigiv.Backend.sigiv.Backend.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.data.domain.Page;
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

   



 



    @GetMapping("/empresa/{empresaId}")
public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
        @PathVariable Long empresaId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Categoria.Estado estado,
        @RequestParam(required = false) String nombre
) {

    Page<CategoriaResponseDto> categoriasPage =
            categoriaService.listarCategoriasPorEmpresa(
                    empresaId,
                    page,
                    size,
                    estado,
                    nombre
            );

    Map<String, Object> data = new HashMap<>();
    data.put("categorias", categoriasPage.getContent());
    data.put("totalElements", categoriasPage.getTotalElements());
    data.put("totalPages", categoriasPage.getTotalPages());
    data.put("currentPage", categoriasPage.getNumber());

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Categorias de la empresa " + empresaId + " listadas correctamente",
                    data
            )
    );
}

}
