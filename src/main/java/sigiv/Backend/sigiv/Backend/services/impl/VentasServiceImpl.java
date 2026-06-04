package sigiv.Backend.sigiv.Backend.services.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleVentaMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.VentasMapper;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.ResumenVendedorDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.DetalleVentaRepository;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;
import sigiv.Backend.sigiv.Backend.services.VentasService;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



@Service
public class VentasServiceImpl implements VentasService {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentasMapper ventasMapper;

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    @Override
    @Transactional
    public VentasResponseDto crearVenta(VentasRequestDto dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Ventas venta = ventasMapper.toEntity(dto, usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO);
        ventasRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalleDto.getCantidad() > producto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            DetalleVentas detalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(detalle);

            producto.setCantidad(producto.getCantidad() - detalleDto.getCantidad());
            productoRepository.save(producto);

            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        if (dto.getEfectivo() != null) {
            venta.setCambio(dto.getEfectivo().subtract(totalVenta));
        }

        ventasRepository.save(venta);
        return ventasMapper.toDto(venta);
    }

    @Override
    public List<VentasResponseDto> listarVentas() {
        return ventasRepository.findAll()
                .stream()
                .map(ventasMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VentasResponseDto obtenerVenta(Long id) {
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return ventasMapper.toDto(venta);
    }

    @Override
    @Transactional
    public VentasResponseDto editarVenta(Long id, VentasRequestDto dto) {
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // Actualizar datos básicos del cliente o efectivo
        venta.setNombreCliente(dto.getNombreCliente());
        venta.setTelefonoCliente(dto.getTelefonoCliente());
        venta.setEfectivo(dto.getEfectivo());

        // Eliminar detalles antiguos y reponer stock
        List<DetalleVentas> detallesAntiguos = detalleVentaRepository.findByVentaIdventa(id);
        for (DetalleVentas d : detallesAntiguos) {
            Producto producto = d.getProducto();
            if (producto != null) {
                producto.setCantidad(producto.getCantidad() + d.getCantidad());
                productoRepository.save(producto);
            }
        }
        detalleVentaRepository.deleteAll(detallesAntiguos);

        // Agregar nuevos detalles
        BigDecimal totalVenta = BigDecimal.ZERO;
        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalleDto.getCantidad() > producto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            DetalleVentas nuevoDetalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(nuevoDetalle);

            producto.setCantidad(producto.getCantidad() - detalleDto.getCantidad());
            productoRepository.save(producto);

            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        if (dto.getEfectivo() != null) {
            venta.setCambio(dto.getEfectivo().subtract(totalVenta));
        }

        ventasRepository.save(venta);
        return ventasMapper.toDto(venta);
    }

    @Override
    public void eliminarVenta(Long id) {
        if (!ventasRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }
        ventasRepository.deleteById(id);
    }

 @Override
public Page<VentasResponseDto> listarVentasPorEmpresaPaginado(
        Long empresaId,
        int page,
        int size
) {

    Page<Ventas> ventasPage = ventasRepository.findVentasByEmpresa(
            empresaId,
            PageRequest.of(page, size, Sort.by("idventa").descending())
    );

    return ventasPage.map(ventasMapper::toDto);
}


@Override
public byte[] generarFacturaPdf(Long id) {

    try {
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // 🔹 Obtener empresa desde usuario
        Empresa empresa = venta.getUsuario().getEmpresa();

        // 🔹 Formato Colombia
        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es", "CO"));
        formatoNumero.setMinimumFractionDigits(0);
        formatoNumero.setMaximumFractionDigits(0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font empresaFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12);

  

        // ===============================
        // 🧾 INFORMACIÓN DE FACTURA
        // ===============================

        document.add(new Paragraph("FACTURA DE VENTA", tituloFont));
        document.add(new Paragraph(" "));
              // ===============================
        // 🏢 INFORMACIÓN DE LA EMPRESA
        // ===============================

        document.add(new Paragraph("Empresa: " + empresa.getNombreEmpresa(), empresaFont));
        document.add(new Paragraph("NIT: " + empresa.getNit(), normalFont));
        document.add(new Paragraph("Dirección: " + empresa.getDireccion(), normalFont));
        document.add(new Paragraph("Teléfono: " + formatoNumero.format(empresa.getTelefono()), normalFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Factura #: " + venta.getIdventa(), normalFont));
        document.add(new Paragraph("Fecha: " + venta.getFecha()));
        document.add(new Paragraph("Cliente: " + venta.getNombreCliente()));
        document.add(new Paragraph("Teléfono Cliente: " + venta.getTelefonoCliente()));
        document.add(new Paragraph("Vendedor: " + venta.getUsuario().getNombres()));
        document.add(new Paragraph(" "));

        // ===============================
        // 📦 TABLA PRODUCTOS
        // ===============================

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        table.addCell("Producto");
        table.addCell("Cantidad");
        table.addCell("Precio");
        table.addCell("Subtotal");

        for (DetalleVentas d : venta.getDetalles()) {
            table.addCell(d.getProducto().getNombre());
            table.addCell(String.valueOf(d.getCantidad()));
            table.addCell(formatoNumero.format(d.getProducto().getPrecio()));
            table.addCell(formatoNumero.format(d.getSubtotal()));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total: " + formatoNumero.format(venta.getTotal()), empresaFont));
        document.add(new Paragraph("Efectivo: " + formatoNumero.format(venta.getEfectivo())));
        document.add(new Paragraph("Cambio: " + formatoNumero.format(venta.getCambio())));

        document.close();

        return out.toByteArray();

    } catch (Exception e) {
        throw new RuntimeException("Error generando factura PDF", e);
    }
}



@Override
public Page<VentasResponseDto> buscarVentaPorIdYEmpresa(
        Long empresaId,
        Long idVenta,
        int page,
        int size
) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("idventa").descending());

    Page<Ventas> ventasPage =
            ventasRepository.findByIdventaAndUsuarioEmpresaIdEmpresa(
                    idVenta,
                    empresaId,
                    pageable
            );

    return ventasPage.map(ventasMapper::toDto);
}

@Override
public List<ResumenVendedorDto> resumenVentasPorUsuario(
        Long empresaId,
        String fechaInicio,
        String fechaFin
) {
    LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
    LocalDateTime fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);

    List<Object[]> resultados = ventasRepository.resumenVentasPorUsuario(empresaId, inicio, fin);
    List<ResumenVendedorDto> resumen = new ArrayList<>();

    for (Object[] row : resultados) {
        resumen.add(new ResumenVendedorDto(
            (String) row[0],
            (Long) row[1],
            (BigDecimal) row[2]
        ));
    }

    return resumen;
}

@Override
public Page<VentasResponseDto> listarVentasPorUsuarioPaginado(
        Long usuarioId,
        int page,
        int size
) {
    Page<Ventas> ventasPage = ventasRepository.findVentasByUsuario(
            usuarioId,
            PageRequest.of(page, size, Sort.by("idventa").descending())
    );

    return ventasPage.map(ventasMapper::toDto);
}

@Override
public Page<VentasResponseDto> buscarVentaPorIdYUsuario(
        Long usuarioId,
        Long idVenta,
        int page,
        int size
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("idventa").descending());

    Page<Ventas> ventasPage = ventasRepository.findByIdventaAndUsuarioIdUsuario(
            idVenta,
            usuarioId,
            pageable
    );

    return ventasPage.map(ventasMapper::toDto);
}


}
