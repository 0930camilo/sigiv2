package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public interface ProveedorService {
    ProveedorResponseDto crearProveedor(ProveedorRequestDto dto);
    ProveedorResponseDto obtenerPorId(Long id);
    ProveedorResponseDto actualizarProveedor(Long id, ProveedorRequestDto dto);
  
    List<ProveedorResponseDto> listarProveedores();
    
    void eliminarProveedor(Long id);
   

   Page<ProveedorResponseDto> listarProveedoresPorEmpresa(
        Long empresaId,
        int page,
        int size,
        Proveedor.Estado estado,
        String nombre,
        String documento
);
}
  