package sigiv.Backend.sigiv.Backend.services;

import java.math.BigDecimal;
import java.util.List;

import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public interface EmpresaService {
    EmpresaResponseDto crearEmpresa(EmpresaRequestDto dto);
    EmpresaResponseDto obtenerPorId(Long id);
    EmpresaResponseDto actualizarEmpresa(Long id, EmpresaRequestDto dto);
    List<EmpresaResponseDto> listarEmpresas();
    List<EmpresaResponseDto> listarPorEstado(Empresa.Estado estado);
    void eliminarEmpresa(Long id);
    List<UsuarioResponseDto> usuariosEmpresa(Long id);
    EmpresaResponseDto cambiarEstado(Long id);
    List<CategoriaResponseDto> categoriasEmpresa(Long id);
    List<ProveedorResponseDto> proveedoresEmpresa(Long id);



    BigDecimal calcularTotalVendido(Long idEmpresa);
    BigDecimal calcularTotalVendidoEntreFechas(Long idEmpresa, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);
    BigDecimal calcularGananciaPorEmpresa(Long idEmpresa);
    long contarUsuariosActivos(Long idEmpresa);
    List<ProductoResponseDto> productosPorCategoria(Long idEmpresa, Long idCategoria);
    List<ProductoResponseDto> productosPorProveedor(Long idEmpresa, Long idProveedor);
    List<ProductoResponseDto> productosPorEmpresa(Long idEmpresa);


}
