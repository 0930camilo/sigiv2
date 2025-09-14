package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {



    private final EmpresaRepository empresaRepository;

    // Obtener todas las empresas
    @GetMapping("/empresas")
    public ResponseEntity<List<Empresa>> obtenerTodasEmpresas() {
        List<Empresa> empresas = empresaRepository.findAll();
        return ResponseEntity.ok(empresas);
    }

    // Obtener empresas activas
    @GetMapping("/empresas/activas")
    public ResponseEntity<List<Empresa>> obtenerEmpresasActivas() {
        return ResponseEntity.ok(empresaRepository.findByEstado(Empresa.Estado.Activo));
    }

    // Obtener empresas inactivas
    @GetMapping("/empresas/inactivas")
    public ResponseEntity<List<Empresa>> obtenerEmpresasInactivas() {
        return ResponseEntity.ok(empresaRepository.findByEstado(Empresa.Estado.Inactivo));
    }

    // Obtener una empresa por su ID
    @GetMapping("/empresa/{id}")
    public ResponseEntity<Empresa> obtenerEmpresaPorId(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar una nueva empresa
    @PostMapping("/empresa")
    public ResponseEntity<Empresa> guardarEmpresa(@RequestBody Empresa nuevaEmpresa) {
        Empresa guardada = empresaRepository.save(nuevaEmpresa);
        return ResponseEntity.ok(guardada);
    }

    // Actualizar una empresa existente
    @PutMapping("/empresa/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(
            @PathVariable Long id,
            @RequestBody Empresa empresaActualizada
    ) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setNombre_empresa(empresaActualizada.getNombre_empresa());
                    empresa.setClave(empresaActualizada.getClave());
                    empresa.setNit(empresaActualizada.getNit());
                    empresa.setTelefono(empresaActualizada.getTelefono());
                    empresa.setDireccion(empresaActualizada.getDireccion());
                    empresa.setEstado(empresaActualizada.getEstado());
                    Empresa actualizada = empresaRepository.save(empresa);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar físicamente una empresa
    @DeleteMapping("/empresa/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar lógicamente una empresa (cambiar estado a Inactivo)
    @PutMapping("/empresa/eliminar/{id}")
    public ResponseEntity<Empresa> eliminarEmpresaLogicamente(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setEstado(Empresa.Estado.Inactivo);
                    Empresa actualizada = empresaRepository.save(empresa);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }
}
