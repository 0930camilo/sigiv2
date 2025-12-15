package sigiv.Backend.sigiv.Backend.services;

import java.util.List;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public interface ProveedorService {
    ProveedorResponseDto crearProveedor(ProveedorRequestDto dto);
    ProveedorResponseDto obtenerPorId(Long id);
    ProveedorResponseDto actualizarProveedor(Long id, ProveedorRequestDto dto);
    List<ProveedorResponseDto> listarProveedores();
    List<ProveedorResponseDto> listarPorEstado(Proveedor.Estado estado);
    void eliminarProveedor(Long id);
    ProveedorResponseDto cambiarEstado(Long id);
    List<ProveedorResponseDto> buscarPorNombre(String nombre);
    List<ProveedorResponseDto> listarPorEmpresa(Long idEmpresa);
}
  