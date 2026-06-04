package sigiv.Backend.sigiv.Backend.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.ProductoMapper;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto.FilaError;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.ProveedorRepository;
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;
import sigiv.Backend.sigiv.Backend.services.CategoriaService;
import sigiv.Backend.sigiv.Backend.services.ProductoService;
import sigiv.Backend.sigiv.Backend.services.ProveedorService;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

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

                // Leer proveedorId (puede ser "ID - Nombre" o solo ID)
                String proveedorStr = leerTexto(row, 6);
                final Long proveedorIdFinal = (proveedorStr != null && !proveedorStr.isBlank())
                        ? extraerIdDeTexto(proveedorStr) : null;

                // Leer categoriaId (puede ser "ID - Nombre" o solo ID)
                String categoriaStr = leerTexto(row, 7);
                if (categoriaStr == null || categoriaStr.isBlank())
                    throw new IllegalArgumentException("El campo 'categoriaId' es obligatorio");
                final Long categoriaIdFinal = extraerIdDeTexto(categoriaStr);

                Proveedor proveedor = null;
                if (proveedorIdFinal != null) {
                    proveedor = proveedorRepository.findById(proveedorIdFinal)
                            .orElseThrow(() -> new IllegalArgumentException("Proveedor con id " + proveedorIdFinal + " no encontrado"));
                }

                Categoria categoria = categoriaRepository.findById(categoriaIdFinal)
                        .orElseThrow(() -> new IllegalArgumentException("Categoría con id " + categoriaIdFinal + " no encontrada"));

                ProductoRequestDto dto = new ProductoRequestDto(
                        null, nombre, descripcion, cantidad,
                        precioCompra, precio, LocalDateTime.now(),
                        estado, proveedorIdFinal, categoriaIdFinal
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

/**
 * Extrae el ID de un texto con formato "ID - Nombre" o solo "ID".
 * Ejemplos: "1 - Electrónica" -> 1, "25" -> 25
 */
private Long extraerIdDeTexto(String texto) {
    if (texto == null || texto.isBlank()) return null;

    texto = texto.trim();

    // Si contiene " - ", extraer solo la parte del ID antes del guion
    if (texto.contains(" - ")) {
        String idParte = texto.split(" - ")[0].trim();
        try {
            return Long.parseLong(idParte);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato inválido: '" + texto + "'. Se esperaba 'ID - Nombre' o solo 'ID'");
        }
    }

    // Si no contiene " - ", intentar parsear directamente como número
    try {
        return Long.parseLong(texto);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Formato inválido: '" + texto + "'. Se esperaba 'ID - Nombre' o solo 'ID'");
    }
}

/**
 * Genera una plantilla Excel para importación masiva de productos.
 * Incluye las categorías de la empresa como validación de datos en la columna H.
 */
@Override
public byte[] generarPlantillaExcel(Long empresaId) throws Exception {
    // Obtener todas las categorías activas de la empresa
    Page<CategoriaResponseDto> categoriasPage = categoriaService.listarCategoriasPorEmpresa(
            empresaId, 0, Integer.MAX_VALUE, Categoria.Estado.Activo, null);
    List<CategoriaResponseDto> categorias = categoriasPage.getContent();

    if (categorias.isEmpty()) {
        throw new IllegalArgumentException("La empresa no tiene categorías activas. Debe crear al menos una categoría antes de generar la plantilla.");
    }

    // Obtener todos los proveedores activos de la empresa
    Page<ProveedorResponseDto> proveedoresPage = proveedorService.listarProveedoresPorEmpresa(
            empresaId, 0, Integer.MAX_VALUE, Proveedor.Estado.Activo, null, null);
    List<ProveedorResponseDto> proveedores = proveedoresPage.getContent();

    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Productos");

        // Estilo para encabezados
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"nombre", "descripcion", "cantidad", "precioCompra", "precio",
                           "estado", "proveedorId", "categoriaId"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // Crear hoja oculta con las categorías
        Sheet categoriasSheet = workbook.createSheet("Categorias");
        workbook.setSheetHidden(1, true);

        for (int i = 0; i < categorias.size(); i++) {
            Row row = categoriasSheet.createRow(i);
            CategoriaResponseDto cat = categorias.get(i);
            row.createCell(0).setCellValue(cat.getIdCategoria() + " - " + cat.getNombre());
        }

        // Crear hoja oculta con los proveedores (si existen)
        if (!proveedores.isEmpty()) {
            Sheet proveedoresSheet = workbook.createSheet("Proveedores");
            workbook.setSheetHidden(2, true);

            for (int i = 0; i < proveedores.size(); i++) {
                Row row = proveedoresSheet.createRow(i);
                ProveedorResponseDto prov = proveedores.get(i);
                row.createCell(0).setCellValue(prov.getIdProveedor() + " - " + prov.getNombre());
            }
        }

        // Crear validación de datos para la columna H (categoriaId)
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        String formula = "Categorias!$A$1:$A$" + categorias.size();
        DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 7, 7);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setEmptyCellAllowed(false);
        sheet.addValidationData(validation);

        // Crear validación para proveedorId (columna G) si hay proveedores
        if (!proveedores.isEmpty()) {
            String proveedorFormula = "Proveedores!$A$1:$A$" + proveedores.size();
            DataValidationConstraint proveedorConstraint = validationHelper.createFormulaListConstraint(proveedorFormula);
            CellRangeAddressList proveedorAddressList = new CellRangeAddressList(1, 1000, 6, 6);
            DataValidation proveedorValidation = validationHelper.createValidation(proveedorConstraint, proveedorAddressList);
            proveedorValidation.setShowErrorBox(true);
            proveedorValidation.setEmptyCellAllowed(true);
            sheet.addValidationData(proveedorValidation);
        }

        // Crear validación para estado (columna F)
        DataValidationConstraint estadoConstraint = validationHelper.createExplicitListConstraint(
                new String[]{"Activo", "Inactivo"});
        CellRangeAddressList estadoAddressList = new CellRangeAddressList(1, 1000, 5, 5);
        DataValidation estadoValidation = validationHelper.createValidation(estadoConstraint, estadoAddressList);
        estadoValidation.setShowErrorBox(true);
        sheet.addValidationData(estadoValidation);

        // Generar archivo en memoria
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        return baos.toByteArray();
    }
}
}
