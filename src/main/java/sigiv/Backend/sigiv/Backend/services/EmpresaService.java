package sigiv.Backend.sigiv.Backend.services;

import java.util.List;



import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Empresa;


public interface EmpresaService {
    EmpresaResponseDto crearEmpresa(EmpresaRequestDto dto);
    EmpresaResponseDto obtenerPorId(Long id);
    EmpresaResponseDto actualizarEmpresa(Long id, EmpresaRequestDto dto);
    List<EmpresaResponseDto> listarEmpresas();
    List<EmpresaResponseDto> listarPorEstado(Empresa.Estado estado);
    void eliminarEmpresa(Long id);
    EmpresaResponseDto cambiarEstado(Long id);
List<CategoriaResponseDto> categoriasEmpresa(Long id);


}
