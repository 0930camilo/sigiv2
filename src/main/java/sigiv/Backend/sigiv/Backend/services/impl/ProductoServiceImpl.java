package sigiv.Backend.sigiv.Backend.services.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.ProductoMapper;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto.FilaError;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.ProveedorRepository;
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;
import sigiv.Backend.sigiv.Backend.services.ProductoService;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;

    // Crear producto
    @Override
    public ProductoResponseDto crearProducto(ProductoRequestDto dto) {
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto = ProductoMapper.toEntityForCreate(dto, proveedor, categoria);
        Producto guardado = productoRepository.save(producto);
        return ProductoMapper.toDto(guardado);
    }

    // Obtener producto por ID
    @Override
    public ProductoResponseDto obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return ProductoMapper.toDto(producto);
    }

    // Actualizar producto
    @Override
    public ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        }

        Categoria categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        }

        ProductoMapper.updateEntityFromDto(dto, producto, proveedor, categoria);
        Producto actualizado = productoRepository.save(producto);
        return ProductoMapper.toDto(actualizado);
    }



    // Eliminar producto
    @Override
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productoRepository.deleteById(id);
    }

  
@Override
public Page<ProductoResponseDto> productosPorEmpresa(
        Long idEmpresa,
        int page,
        int size,
        Producto.Estado estado,
        String nombre,
        String categoria,
        String proveedor
) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("idproducto").ascending());

    Page<Producto> productosPage =
            productoRepository.buscarProductosConFiltros(
                    idEmpresa,
                    estado,
                    nombre,
                    categoria,
                    proveedor,
                    pageable
            );

    return productosPage.map(ProductoMapper::toDto);
}

/**
 * Importa productos desde un archivo Excel (.xlsx o .xls).
 *
 * Columnas esperadas (fila 1 = encabezado, datos desde fila 2):
 *   A: nombre        (texto, obligatorio)
 *   B: descripcion   (texto, opcional)
 *   C: cantidad      (entero, obligatorio)
 *   D: precioCompra  (decimal, obligatorio)
 *   E: precio        (decimal, obligatorio)
 *   F: estado        (Activo | Inactivo, opcional → defecto Activo)
 *   G: proveedorId   (número, opcional)
 *   H: categoriaId   (número, obligatorio)
 */
@Override
public ProductoImportResultDto importarDesdeExcel(InputStream inputStream) {
    List<FilaError> errores = new ArrayList<>();
    int exitosos = 0;
    int totalFilas = 0;

    try (Workbook workbook = WorkbookFactory.create(inputStream)) {
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || esFilaVacia(row)) continue;
            totalFilas++;

            try {
                String nombre = leerTexto(row, 0);
                if (nombre == null || nombre.isBlank())
                    throw new IllegalArgumentException("El campo 'nombre' es obligatorio");

                String descripcion = leerTexto(row, 1);

                Integer cantidad = leerEntero(row, 2);
                if (cantidad == null)
                    throw new IllegalArgumentException("El campo 'cantidad' es obligatorio y debe ser un número entero");

                BigDecimal precioCompra = leerDecimal(row, 3);
                if (precioCompra == null)
                    throw new IllegalArgumentException("El campo 'precioCompra' es obligatorio");

                BigDecimal precio = leerDecimal(row, 4);
                if (precio == null)
                    throw new IllegalArgumentException("El campo 'precio' es obligatorio");

                String estadoStr = leerTexto(row, 5);
                Producto.Estado estado = Producto.Estado.Activo;
                if (estadoStr != null && !estadoStr.isBlank()) {
                    try {
                        estado = Producto.Estado.valueOf(estadoStr.trim());
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("El campo 'estado' debe ser 'Activo' o 'Inactivo', se recibió: " + estadoStr);
                    }
                }

                Long proveedorId = leerLong(row, 6);
                Long categoriaId = leerLong(row, 7);
                if (categoriaId == null)
                    throw new IllegalArgumentException("El campo 'categoriaId' es obligatorio");

                Proveedor proveedor = null;
                if (proveedorId != null) {
                    proveedor = proveedorRepository.findById(proveedorId)
                            .orElseThrow(() -> new IllegalArgumentException("Proveedor con id " + proveedorId + " no encontrado"));
                }

                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new IllegalArgumentException("Categoría con id " + categoriaId + " no encontrada"));

                ProductoRequestDto dto = new ProductoRequestDto(
                        null, nombre, descripcion, cantidad,
                        precioCompra, precio, LocalDateTime.now(),
                        estado, proveedorId, categoriaId
                );

                Producto producto = ProductoMapper.toEntityForCreate(dto, proveedor, categoria);
                productoRepository.save(producto);
                exitosos++;

            } catch (Exception e) {
                errores.add(new FilaError(i + 1, e.getMessage()));
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage(), e);
    }

    return new ProductoImportResultDto(totalFilas, exitosos, errores.size(), errores);
}

// ───────── helpers ─────────

private boolean esFilaVacia(Row row) {
    for (int c = 0; c <= 7; c++) {
        Cell cell = row.getCell(c);
        if (cell != null && cell.getCellType() != CellType.BLANK) return false;
    }
    return true;
}

private String leerTexto(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return null;
    return switch (cell.getCellType()) {
        case STRING -> cell.getStringCellValue().trim();
        case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
        case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
        default -> null;
    };
}

private Integer leerEntero(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
    if (cell.getCellType() == CellType.STRING) {
        try { return Integer.parseInt(cell.getStringCellValue().trim()); } catch (NumberFormatException e) { return null; }
    }
    return null;
}

private BigDecimal leerDecimal(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(cell.getNumericCellValue());
    if (cell.getCellType() == CellType.STRING) {
        try { return new BigDecimal(cell.getStringCellValue().trim()); } catch (NumberFormatException e) { return null; }
    }
    return null;
}

private Long leerLong(Row row, int col) {
    Cell cell = row.getCell(col);
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) return (long) cell.getNumericCellValue();
    if (cell.getCellType() == CellType.STRING) {
        try { return Long.parseLong(cell.getStringCellValue().trim()); } catch (NumberFormatException e) { return null; }
    }
    return null;
}
}
