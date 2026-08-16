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
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import sigiv.Backend.sigiv.Backend.dto.abono.AbonoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.abono.AbonoResponseDto;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.AbonoMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleVentaMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.VentasMapper;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.ResumenVendedorDto;
import sigiv.Backend.sigiv.Backend.entity.*;
import sigiv.Backend.sigiv.Backend.entity.Ventas.EstadoPago;
import sigiv.Backend.sigiv.Backend.entity.Ventas.TipoPago;
import sigiv.Backend.sigiv.Backend.repository.*;
import sigiv.Backend.sigiv.Backend.services.VentasService;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import org.springframework.data.domain.Pageable;

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
    private AbonoRepository abonoRepository;
    @Autowired
    private VentasMapper ventasMapper;
    @Autowired
    private DetalleVentaMapper detalleVentaMapper;
    @Autowired
    private AbonoMapper abonoMapper;

    @Override
    @Transactional
    public VentasResponseDto crearVenta(VentasRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Empresa empresa = usuario.getEmpresa();

        Ventas venta = ventasMapper.toEntity(dto, usuario, empresa);
        venta.setFecha(LocalDateTime.now());
        venta.setTipoPago(resolverTipoPago(dto));

        BigDecimal subtotalVenta = BigDecimal.ZERO;
        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            if (detalleDto.getCantidad().compareTo(producto.getCantidad()) > 0) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            BigDecimal subtotal = producto.getPrecio().multiply(detalleDto.getCantidad());
            DetalleVentas detalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(detalle);
            producto.setCantidad(producto.getCantidad().subtract(detalleDto.getCantidad()));
            productoRepository.save(producto);
            subtotalVenta = subtotalVenta.add(subtotal);
        }

        BigDecimal descuentoTotal = normalizarDescuento(dto.getDescuentoTotal(), subtotalVenta);
        BigDecimal totalVenta = subtotalVenta.subtract(descuentoTotal);
        venta.setSubtotal(subtotalVenta);
        venta.setDescuentoTotal(descuentoTotal);
        venta.setTotal(totalVenta);

        if (venta.getTipoPago() == TipoPago.CONTADO) {
            venta.setEstadoPago(EstadoPago.PAGADA);
            if (dto.getEfectivo() != null) {
                venta.setCambio(dto.getEfectivo().subtract(totalVenta));
            }
        } else { // CRÉDITO
            BigDecimal abonoInicial = dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO;
            if (abonoInicial.compareTo(BigDecimal.ZERO) > 0) {
                if (abonoInicial.compareTo(totalVenta) > 0) {
                    throw new IllegalArgumentException("El abono inicial no puede ser mayor que el total de la venta.");
                }
                Abono abono = new Abono();
                abono.setVenta(venta);
                abono.setUsuario(usuario);
                abono.setValor(abonoInicial);
                abono.setFecha(LocalDateTime.now());
                abono.setMetodoPago(dto.getMetodoPagoAbonoInicial() != null ? dto.getMetodoPagoAbonoInicial() : Abono.MetodoPago.EFECTIVO);
                abono.setObservacion("Abono inicial de la venta");
                venta.getAbonos().add(abono);
            }

            BigDecimal totalAbonado = venta.getAbonos().stream().map(Abono::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalAbonado.compareTo(totalVenta) >= 0) {
                venta.setEstadoPago(EstadoPago.PAGADA);
            } else {
                venta.setEstadoPago(EstadoPago.PENDIENTE);
            }
        }
        
        ventasRepository.save(venta);
        return ventasMapper.toDto(venta);
    }

    @Override
    @Transactional
    public AbonoResponseDto registrarAbono(Long ventaId, AbonoRequestDto abonoDto) {
        Ventas venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getTipoPago() != TipoPago.CREDITO) {
            throw new IllegalStateException("Solo se pueden registrar abonos en ventas a crédito.");
        }

        BigDecimal totalAbonado = venta.getAbonos().stream().map(Abono::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalAbonado);

        if (abonoDto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor del abono debe ser positivo.");
        }

        if (abonoDto.getValor().compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException("El valor del abono no puede ser mayor que el saldo pendiente de $" + saldoPendiente);
        }

        Usuario usuario = usuarioRepository.findById(abonoDto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para registrar el abono"));

        Abono abono = abonoMapper.toEntity(abonoDto, venta, usuario);
        abono.setFecha(LocalDateTime.now());
        
        abonoRepository.save(abono);

        BigDecimal nuevoTotalAbonado = totalAbonado.add(abono.getValor());
        if (nuevoTotalAbonado.compareTo(venta.getTotal()) >= 0) {
            venta.setEstadoPago(EstadoPago.PAGADA);
        }

        ventasRepository.save(venta);
        return abonoMapper.toDto(abono);
    }

    @Override
    public List<AbonoResponseDto> getAbonosByVentaId(Long ventaId) {
        if (!ventasRepository.existsById(ventaId)) {
            throw new RuntimeException("Venta no encontrada");
        }
        return abonoRepository.findByVentaIdventa(ventaId).stream()
                .map(abonoMapper::toDto)
                .collect(Collectors.toList());
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
        // La edición de ventas a crédito puede tener implicaciones complejas.
        // Por ahora, se mantiene la lógica original, pero se recomienda revisar
        // si se debe permitir editar una venta que ya tiene abonos.
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        String nombre = (dto.getNombreCliente() == null || dto.getNombreCliente().isBlank()) 
                        ? "NN" : dto.getNombreCliente();
        venta.setNombreCliente(nombre);
        
        venta.setTelefonoCliente(dto.getTelefonoCliente());
        venta.setCorreoCliente(dto.getCorreoCliente());
        
        String documento = (dto.getDocumentoCliente() == null || dto.getDocumentoCliente().isBlank()) 
                           ? "999999999" : dto.getDocumentoCliente();
        venta.setDocumentoCliente(documento);
        
        venta.setEfectivo(dto.getEfectivo());

        List<DetalleVentas> detallesAntiguos = detalleVentaRepository.findByVentaIdventa(id);
        for (DetalleVentas d : detallesAntiguos) {
            Producto producto = d.getProducto();
            if (producto != null) {
                producto.setCantidad(producto.getCantidad().add(d.getCantidad()));
                productoRepository.save(producto);
            }
        }
        detalleVentaRepository.deleteAll(detallesAntiguos);

        BigDecimal subtotalVenta = BigDecimal.ZERO;
        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalleDto.getCantidad().compareTo(producto.getCantidad()) > 0) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio().multiply(detalleDto.getCantidad());
            DetalleVentas nuevoDetalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(nuevoDetalle);

            producto.setCantidad(producto.getCantidad().subtract(detalleDto.getCantidad()));
            productoRepository.save(producto);

            subtotalVenta = subtotalVenta.add(subtotal);
        }

        BigDecimal descuentoTotal = normalizarDescuento(dto.getDescuentoTotal(), subtotalVenta);
        BigDecimal totalVenta = subtotalVenta.subtract(descuentoTotal);
        venta.setSubtotal(subtotalVenta);
        venta.setDescuentoTotal(descuentoTotal);
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
    public Page<VentasResponseDto> listarVentasPorEmpresaPaginado(Long empresaId, int page, int size, String fechaInicio, String fechaFin, String cliente) {
        LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isBlank()) ? LocalDate.parse(fechaInicio).atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null && !fechaFin.isBlank()) ? LocalDate.parse(fechaFin).atTime(LocalTime.MAX) : null;
        Page<Ventas> ventasPage = ventasRepository.findVentasByEmpresa(empresaId, inicio, fin, cliente, PageRequest.of(page, size, Sort.by("idventa").descending()));
        return ventasPage.map(ventasMapper::toDto);
    }

    @Override
    public byte[] generarFacturaPdf(Long id) {
        return generarFacturaPdfInterno(id, false);
    }

    @Override
    public byte[] generarFacturaPosPdf(Long id) {
        return generarFacturaPdfInterno(id, true);
    }

    @Override
    public Page<VentasResponseDto> buscarVentaPorIdYEmpresa(Long empresaId, Long idVenta, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idventa").descending());
        Page<Ventas> ventasPage = ventasRepository.findByIdventaAndEmpresaOUsuarioEmpresa(idVenta, empresaId, pageable);
        return ventasPage.map(ventasMapper::toDto);
    }

    @Override
    public List<ResumenVendedorDto> resumenVentasPorUsuario(Long empresaId, String fechaInicio, String fechaFin) {
        LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
        LocalDateTime fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
        List<Object[]> resultados = ventasRepository.resumenVentasPorUsuario(empresaId, inicio, fin);
        List<ResumenVendedorDto> resumen = new ArrayList<>();
        for (Object[] row : resultados) {
            resumen.add(new ResumenVendedorDto((String) row[0], (Long) row[1], (BigDecimal) row[2]));
        }
        return resumen;
    }

    @Override
    public Page<VentasResponseDto> listarVentasPorUsuarioPaginado(Long usuarioId, int page, int size, String fechaInicio, String fechaFin, String cliente) {
        LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isBlank()) ? LocalDate.parse(fechaInicio).atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null && !fechaFin.isBlank()) ? LocalDate.parse(fechaFin).atTime(LocalTime.MAX) : null;
        Page<Ventas> ventasPage = ventasRepository.findVentasByUsuario(usuarioId, inicio, fin, cliente, PageRequest.of(page, size, Sort.by("idventa").descending()));
        return ventasPage.map(ventasMapper::toDto);
    }

    @Override
    public Page<VentasResponseDto> buscarVentaPorIdYUsuario(Long usuarioId, Long idVenta, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idventa").descending());
        Page<Ventas> ventasPage = ventasRepository.findByIdventaAndUsuarioIdUsuario(idVenta, usuarioId, pageable);
        return ventasPage.map(ventasMapper::toDto);
    }

    // Métodos auxiliares
    @Transactional(readOnly = true)
    byte[] generarFacturaPdfInterno(Long id, boolean formatoPos) {
        try {
            Ventas venta = ventasRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            Empresa empresa = venta.getUsuario() != null ? venta.getUsuario().getEmpresa() : venta.getEmpresa();
            if (empresa == null) {
                throw new IllegalArgumentException("La venta no tiene empresa asociada");
            }

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es", "CO"));
            formatoNumero.setMinimumFractionDigits(0);
            formatoNumero.setMaximumFractionDigits(0);

            BigDecimal total = valorSeguro(venta.getTotal());
            BigDecimal totalAbonado = venta.getAbonos() != null
                    ? venta.getAbonos().stream()
                    .map(abono -> abono.getValor() != null ? abono.getValor() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
            BigDecimal saldoPendiente = total.subtract(totalAbonado);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = formatoPos
                    ? new Document(new Rectangle(226.77f, 600f), 8f, 8f, 8f, 8f)
                    : new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            if (formatoPos) {
                Font tituloFont = new Font(Font.COURIER, 10, Font.BOLD);
                Font normalFont = new Font(Font.COURIER, 8, Font.NORMAL);
                Font boldFont = new Font(Font.COURIER, 8, Font.BOLD);

                addCentered(document, safeText(empresa.getNombreEmpresa(), "SIGIV"), tituloFont);
                addCentered(document, "FACTURA POS", normalFont);
                addCentered(document, "No. " + venta.getIdventa(), normalFont);
                addCentered(document, "NIT: " + safeText(empresa.getNit(), "-"), normalFont);
                addCentered(document, "Direccion: " + safeText(empresa.getDireccion(), "-"), normalFont);
                addCentered(document, "Telefono: " + safeText(empresa.getTelefono(), "-"), normalFont);
                addLine(document, "-------------------------------------------", normalFont);
                addLabelValueLine(document, "Fecha:", formatFecha(venta.getFecha()), boldFont, normalFont);
                addLabelValueLine(document, "Cliente:", safeText(venta.getNombreCliente(), "NN"), boldFont, normalFont);
                addLabelValueLine(document, "Telefono:", safeText(venta.getTelefonoCliente(), "-"), boldFont, normalFont);
                addLabelValueLine(document, "Documento:", safeText(venta.getDocumentoCliente(), "-"), boldFont, normalFont);
                addLabelValueLine(document, "Vendedor:", venta.getUsuario() != null ? safeText(venta.getUsuario().getNombres(), "-") : "-", boldFont, normalFont);
                addLabelValueLine(document, "Tipo pago:", safeText(venta.getTipoPago(), "-"), boldFont, normalFont);
                addLabelValueLine(document, "Estado:", safeText(venta.getEstadoPago(), "-"), boldFont, normalFont);
                addLine(document, "-------------------------------------------", normalFont);

                if (venta.getDetalles() != null) {
                    for (DetalleVentas detalle : venta.getDetalles()) {
                        String nombreProducto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
                        BigDecimal cantidad = valorSeguro(detalle.getCantidad());
                        BigDecimal precio = valorSeguro(detalle.getPrecio());
                        BigDecimal subtotal = valorSeguro(detalle.getSubtotal());

                        Paragraph pNombre = new Paragraph(limitar(nombreProducto, 32), boldFont);
                        pNombre.setSpacingAfter(2f);
                        document.add(pNombre);

                        PdfPTable itemTable = new PdfPTable(2);
                        itemTable.setWidthPercentage(100);
                        itemTable.setWidths(new float[]{60, 40});

                        PdfPCell leftCell = new PdfPCell(new Paragraph(cantidad.stripTrailingZeros().toPlainString() + " x $" + formatoNumero.format(precio), normalFont));
                        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        leftCell.setBorder(PdfPCell.NO_BORDER);

                        PdfPCell rightCell = new PdfPCell(new Paragraph("$" + formatoNumero.format(subtotal), normalFont));
                        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        rightCell.setBorder(PdfPCell.NO_BORDER);

                        itemTable.addCell(leftCell);
                        itemTable.addCell(rightCell);
                        itemTable.setSpacingAfter(2f);
                        document.add(itemTable);
                    }
                }

                addLine(document, "-------------------------------------------", normalFont);
                PdfPTable totalTable = new PdfPTable(2);
                totalTable.setWidthPercentage(100);
                totalTable.setWidths(new float[]{50, 50});
                totalTable.addCell(celdaSinBorde("Subtotal", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(valorSeguro(venta.getSubtotal())), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Descuento", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(valorSeguro(venta.getDescuentoTotal())), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Total", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(total), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Efectivo", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(valorSeguro(venta.getEfectivo())), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Cambio", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(valorSeguro(venta.getCambio())), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Abonado", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(totalAbonado), boldFont, Element.ALIGN_RIGHT));
                totalTable.addCell(celdaSinBorde("Saldo", boldFont, Element.ALIGN_LEFT));
                totalTable.addCell(celdaSinBorde("$" + formatoNumero.format(saldoPendiente), boldFont, Element.ALIGN_RIGHT));
                document.add(totalTable);
                addLine(document, "-------------------------------------------", normalFont);
                addCentered(document, "Gracias por su compra", normalFont);
            } else {
                Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
                Font empresaFont = new Font(Font.HELVETICA, 14, Font.BOLD);
                Font normalFont = new Font(Font.HELVETICA, 12);

                document.add(new Paragraph("FACTURA DE VENTA", tituloFont));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(safeText(empresa.getNombreEmpresa(), "SIGIV"), empresaFont));
                document.add(new Paragraph("NIT: " + safeText(empresa.getNit(), "-"), normalFont));
                document.add(new Paragraph("Dirección: " + safeText(empresa.getDireccion(), "-"), normalFont));
                document.add(new Paragraph("Teléfono: " + safeText(empresa.getTelefono(), "-"), normalFont));
                document.add(new Paragraph(" "));

                document.add(new Paragraph("Factura #: " + venta.getIdventa(), normalFont));
                document.add(new Paragraph("Fecha: " + formatFecha(venta.getFecha()), normalFont));
                document.add(new Paragraph("Cliente: " + safeText(venta.getNombreCliente(), "NN"), normalFont));
                document.add(new Paragraph("Vendedor: " + (venta.getUsuario() != null ? safeText(venta.getUsuario().getNombres(), "-") : "-"), normalFont));
                document.add(new Paragraph("Estado: " + safeText(venta.getEstadoPago(), "-"), normalFont));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.addCell("Producto");
                table.addCell("Cantidad");
                table.addCell("Precio");
                table.addCell("Subtotal");

                if (venta.getDetalles() != null) {
                    for (DetalleVentas detalle : venta.getDetalles()) {
                        table.addCell(detalle.getProducto() != null ? safeText(detalle.getProducto().getNombre(), "Producto") : "Producto");
                        table.addCell(formatCantidad(detalle.getCantidad()));
                        table.addCell(formatoNumero.format(valorSeguro(detalle.getPrecio())));
                        table.addCell(formatoNumero.format(valorSeguro(detalle.getSubtotal())));
                    }
                }

                document.add(table);
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Subtotal: " + formatoNumero.format(valorSeguro(venta.getSubtotal())), normalFont));
                document.add(new Paragraph("Descuento: " + formatoNumero.format(valorSeguro(venta.getDescuentoTotal())), normalFont));
                document.add(new Paragraph("Total: " + formatoNumero.format(total), empresaFont));
                document.add(new Paragraph("Abonado: " + formatoNumero.format(totalAbonado), normalFont));
                document.add(new Paragraph("Saldo pendiente: " + formatoNumero.format(saldoPendiente), normalFont));
                document.add(new Paragraph("Estado de pago: " + safeText(venta.getEstadoPago(), "-"), normalFont));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando factura PDF", e);
        }
    }

    TipoPago resolverTipoPago(VentasRequestDto dto) {
        if (dto.getTipoPago() != null) {
            BigDecimal abonoInicial = dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO;
            if (dto.getTipoPago() == TipoPago.CONTADO && abonoInicial.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("No se puede registrar abono inicial cuando el tipo de pago es CONTADO.");
            }
            return dto.getTipoPago();
        }

        BigDecimal abonoInicial = dto.getAbonoInicial() != null ? dto.getAbonoInicial() : BigDecimal.ZERO;
        if (abonoInicial.compareTo(BigDecimal.ZERO) > 0 || dto.getMetodoPagoAbonoInicial() != null) {
            return TipoPago.CREDITO;
        }

        return TipoPago.CONTADO;
    }

    private PdfPCell celdaSinBorde(String texto, Font font, int alineacion) {
        PdfPCell celda = new PdfPCell(new Paragraph(texto, font));
        celda.setHorizontalAlignment(alineacion);
        celda.setBorder(PdfPCell.NO_BORDER);
        return celda;
    }

    private void addCentered(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private void addLine(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        document.add(paragraph);
    }

    private void addLabelValueLine(Document document, String label, String value, Font labelFont, Font valueFont) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{40, 60});

        PdfPCell leftCell = new PdfPCell(new Paragraph(label, labelFont));
        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        leftCell.setBorder(PdfPCell.NO_BORDER);

        PdfPCell rightCell = new PdfPCell(new Paragraph(value, valueFont));
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setBorder(PdfPCell.NO_BORDER);

        table.addCell(leftCell);
        table.addCell(rightCell);
        table.setSpacingAfter(0f);
        document.add(table);
    }

    private String safeText(Object value, String fallback) {
        if (value == null || String.valueOf(value).isBlank()) {
            return fallback;
        }
        return String.valueOf(value);
    }

    private String limitar(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

    private String formatFecha(LocalDateTime fecha) {
        return fecha != null ? fecha.toString() : "-";
    }

    private String formatCantidad(BigDecimal cantidad) {
        BigDecimal segura = valorSeguro(cantidad);
        return segura.stripTrailingZeros().toPlainString();
    }

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal normalizarDescuento(BigDecimal descuento, BigDecimal subtotal) {
        BigDecimal descuentoSeguro = descuento != null ? descuento : BigDecimal.ZERO;
        BigDecimal subtotalSeguro = subtotal != null ? subtotal : BigDecimal.ZERO;
        if (descuentoSeguro.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento total no puede ser negativo");
        }
        if (descuentoSeguro.compareTo(subtotalSeguro) > 0) {
            throw new IllegalArgumentException("El descuento total no puede superar el subtotal de la venta");
        }
        return descuentoSeguro;
    }
}