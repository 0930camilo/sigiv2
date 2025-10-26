package sigiv.Backend.sigiv.Backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.services.EmpresaService;

import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController<empresaService> {

    private final EmpresaService empresaService;

    @PostMapping("/crear-empresa")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> crear(@RequestBody EmpresaRequestDto dto) {
        EmpresaResponseDto created = empresaService.crearEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Empresa creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> obtenerPorId(@PathVariable Long id) {
        EmpresaResponseDto empresa = empresaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Empresa encontrada", empresa   )
        );
    }

    @GetMapping("/list-empresas")
    public ResponseEntity<ApiResponse<List<EmpresaResponseDto>>> listar() {
        List<EmpresaResponseDto> empresas = empresaService.listarEmpresas();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las empresas listadas", empresas)
        );
    }

    @GetMapping("/list-empresas-status")
    public ResponseEntity<ApiResponse<List<EmpresaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Empresa.Estado estado) {

        List<EmpresaResponseDto> empresas;
        String message;

        if (estado != null) {
            empresas = empresaService.listarPorEstado(estado);
            message = "Empresas listadas por estado: " + estado;
        } else {
            empresas = empresaService.listarEmpresas();
            message = "Todas las empresas listadas";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, empresas)
        );
    }

    @PutMapping("/update-empresa/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody EmpresaRequestDto dto) {
        EmpresaResponseDto actualizado = empresaService.actualizarEmpresa(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Empresa actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-empresa/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Empresa eliminada correctamente", null)
        );
    }

     @PutMapping("/cambiar-estado/{id}")
public ResponseEntity<ApiResponse<EmpresaResponseDto>> cambiarEstado(@PathVariable Long id) {
    EmpresaResponseDto actualizado = empresaService.cambiarEstado(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Estado de la empresa actualizado automáticamente", actualizado)
    );
}

@GetMapping("/{id}/categorias")
public ResponseEntity<ApiResponse<List<CategoriaResponseDto>>> listarCategoriasPorEmpresa(@PathVariable Long id) {
    List<CategoriaResponseDto> categorias = empresaService.categoriasEmpresa(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Categorías de la empresa con id " + id, categorias)
    );
}

@GetMapping("/{id}/proveedores")
public ResponseEntity<ApiResponse<List<ProveedorResponseDto>>> listarProveedoresPorEmpresa(@PathVariable Long id) {
    List<ProveedorResponseDto> proveedores = empresaService.proveedoresEmpresa(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Proveedores de la empresa con id " + id, proveedores)
    );
}



@GetMapping("/{id}/usuarios")
public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listarUsuariosPorEmpresa(@PathVariable Long id) {
    List<UsuarioResponseDto> usuarios = empresaService.usuariosEmpresa(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Usuarios de la empresa con id " + id, usuarios)
    );
}


@GetMapping("/{id}/total-vendido")
public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorEmpresa(@PathVariable Long id) {
    BigDecimal total = empresaService.calcularTotalVendido(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Total vendido por la empresa con id " + id, total)
    );
}


@GetMapping("/{id}/total-vendido-rango")
public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorEmpresaEntreFechas(
        @PathVariable Long id,
        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

    BigDecimal total = empresaService.calcularTotalVendidoEntreFechas(id, fechaInicio, fechaFin);

    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Total vendido por la empresa con id " + id + " entre " + fechaInicio + " y " + fechaFin,
                    total)
    );
}
@GetMapping("/ganancia/empresa/{idEmpresa}")
public ResponseEntity<ApiResponse<BigDecimal>> obtenerGananciaPorEmpresa(@PathVariable Long idEmpresa) {
    BigDecimal ganancia = empresaService.calcularGananciaPorEmpresa(idEmpresa);
    return ResponseEntity.ok(
        new ApiResponse<>(true, HttpStatus.OK.value(),
                "Ganancia obtenida correctamente para la empresa con id " + idEmpresa, ganancia)
    );
}


}
