package sigiv.Backend.sigiv.Backend.services.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.text.NumberFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.CotizacionMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleCotizacionMapper;
import sigiv.Backend.sigiv.Backend.entity.*;
import sigiv.Backend.sigiv.Backend.repository.*;
import sigiv.Backend.sigiv.Backend.services.CotizacionService;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Autowired
    private CotizacionMapper cotizacionMapper;

    @Autowired
    private DetalleCotizacionMapper detalleMapper;

    @Override
    @Transactional
    public CotizacionResponseDto crearCotizacion(CotizacionRequestDto dto) {

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new RuntimeException("La cotización debe tener al menos un detalle.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cotizacion cot = cotizacionMapper.toEntity(dto, usuario);
        cot.setFecha(LocalDateTime.now());
        cot.setTotal(BigDecimal.ZERO);

        cotizacionRepository.save(cot);

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleCotizacion> listaDetalles = new java.util.ArrayList<>();

        for (DetalleCotizacionRequestDto detalleDto : dto.getDetalles()) {

            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            DetalleCotizacion detalle = detalleMapper.toEntity(detalleDto, cot, producto, subtotal);

            detalleCotizacionRepository.save(detalle);

            total = total.add(subtotal);
            listaDetalles.add(detalle);
        }

        cot.setTotal(total);
        cot.setDetalles(listaDetalles);

        cotizacionRepository.save(cot);

        return cotizacionMapper.toDto(cot);
    }

    @Override
    public List<CotizacionResponseDto> listarCotizaciones() {
        return cotizacionRepository.findAll()
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CotizacionResponseDto obtenerCotizacion(Long id) {
        Cotizacion cot = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        return cotizacionMapper.toDto(cot);
    }

    @Override
    public void eliminarCotizacion(Long id) {
        cotizacionRepository.deleteById(id);
    }

    @Override
    public List<DetalleCotizacion> findByCotizacionId(Long idcotizacion) {
        return detalleCotizacionRepository.findByCotizacion_Idcotizacion(idcotizacion);
    }

    @Override
    public Page<CotizacionResponseDto> listarCotizacionesPorEmpresa(
            Long empresaId,
            int page,
            int size,
            Long usuarioId,
            String nombreCliente,
            String fechaInicio,
            String fechaFin,
            Long idCotizacion
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idcotizacion").descending());

        if (idCotizacion != null) {
            if (usuarioId != null) {
                return cotizacionRepository.findByIdAndEmpresaIdAndUsuarioId(idCotizacion, empresaId, usuarioId, pageable)
                        .map(cotizacionMapper::toDto);
            } else {
                return cotizacionRepository.findByIdAndEmpresaId(idCotizacion, empresaId, pageable)
                        .map(cotizacionMapper::toDto);
            }
        }

        LocalDateTime dtFechaInicio = null;
        LocalDateTime dtFechaFin = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(fechaInicio, formatter);
                dtFechaInicio = date.atStartOfDay();
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha inicio inválido. Use DD/MM/YYYY");
            }
        }

        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(fechaFin, formatter);
                dtFechaFin = date.atTime(LocalTime.MAX);
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha fin inválido. Use DD/MM/YYYY");
            }
        }

        Page<Cotizacion> cotizacionesPage;
        boolean tieneUsuario = usuarioId != null;
        boolean tieneCliente = nombreCliente != null && !nombreCliente.trim().isEmpty();
        boolean tieneFechas = dtFechaInicio != null && dtFechaFin != null;

        if (tieneUsuario && tieneCliente && tieneFechas) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndUsuarioIdAndNombreClienteContainingAndFechaRange(
                    empresaId, usuarioId, nombreCliente, dtFechaInicio, dtFechaFin, pageable);
        } else if (tieneUsuario && tieneCliente) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndUsuarioIdAndNombreClienteContaining(
                    empresaId, usuarioId, nombreCliente, pageable);
        } else if (tieneUsuario && tieneFechas) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndUsuarioIdAndFechaRange(
                    empresaId, usuarioId, dtFechaInicio, dtFechaFin, pageable);
        } else if (tieneCliente && tieneFechas) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndNombreClienteContainingAndFechaRange(
                    empresaId, nombreCliente, dtFechaInicio, dtFechaFin, pageable);
        } else if (tieneUsuario) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndUsuarioId(
                    empresaId, usuarioId, pageable);
        } else if (tieneCliente) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndNombreClienteContaining(
                    empresaId, nombreCliente, pageable);
        } else if (tieneFechas) {
            cotizacionesPage = cotizacionRepository.findByEmpresaIdAndFechaRange(
                    empresaId, dtFechaInicio, dtFechaFin, pageable);
        } else {
            cotizacionesPage = cotizacionRepository.findByEmpresaId(empresaId, pageable);
        }

        return cotizacionesPage.map(cotizacionMapper::toDto);
    }

    @Override
    public byte[] generarCotizacionPdf(Long id) {
        try {
            Cotizacion cotizacion = cotizacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

            Empresa empresa = cotizacion.getUsuario().getEmpresa();
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

            document.add(new Paragraph("COTIZACIÓN", tituloFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph(empresa.getNombreEmpresa(), empresaFont));
            document.add(new Paragraph("NIT: " + empresa.getNit(), normalFont));
            document.add(new Paragraph("Dirección: " + empresa.getDireccion(), normalFont));
            document.add(new Paragraph("Teléfono: " + formatoNumero.format(empresa.getTelefono()), normalFont));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Cotización #: " + cotizacion.getIdcotizacion(), normalFont));
            document.add(new Paragraph("Fecha: " + cotizacion.getFecha(), normalFont));
            document.add(new Paragraph("Cliente: " + cotizacion.getNombreCliente(), normalFont));
            document.add(new Paragraph("Vendedor: " + cotizacion.getUsuario().getNombres(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Precio");
            table.addCell("Subtotal");

            for (DetalleCotizacion d : cotizacion.getDetalles()) {
                table.addCell(d.getProducto().getNombre());
                table.addCell(String.valueOf(d.getCantidad()));
                table.addCell(formatoNumero.format(d.getPrecio()));
                table.addCell(formatoNumero.format(d.getSubtotal()));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: " + formatoNumero.format(cotizacion.getTotal()), empresaFont));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando cotización PDF", e);
        }
    }

    @Override
    public byte[] generarCotizacionPosPdf(Long id) {
        try {
            Cotizacion cotizacion = cotizacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

            Empresa empresa = cotizacion.getUsuario().getEmpresa();
            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es", "CO"));
            formatoNumero.setMinimumFractionDigits(0);
            formatoNumero.setMaximumFractionDigits(0);

            int cantidadDetalles = cotizacion.getDetalles() != null ? cotizacion.getDetalles().size() : 0;
            float altoPagina = Math.max(450f, 350f + (cantidadDetalles * 25f));
            com.lowagie.text.Rectangle pageSize = new com.lowagie.text.Rectangle(226.77f, altoPagina);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(pageSize, 8f, 8f, 8f, 8f);
            PdfWriter.getInstance(document, out);

            document.open();

            Font tituloFont = new Font(Font.COURIER, 10, Font.BOLD);
            Font normalFont = new Font(Font.COURIER, 8, Font.NORMAL);
            Font boldFont = new Font(Font.COURIER, 8, Font.BOLD);

            addCentered(document, safeText(empresa.getNombreEmpresa(), "Mi Empresa"), tituloFont);
            addCentered(document, "Cotizacion ", normalFont);
            addCentered(document, "No." + cotizacion.getIdcotizacion(), normalFont);
            addCentered(document, "NIT: " + safeText(empresa.getNit(), "-"), normalFont);
            addCentered(document, "Direccion: " + safeText(empresa.getDireccion(), "-"), normalFont);
            addCentered(document, "Telefono: " + safeText(empresa.getTelefono(), "-"), normalFont);

            addLine(document, "-------------------------------------------", normalFont);
            addLabelValueLine(document, "Fecha:", formatFecha(cotizacion.getFecha()), boldFont, normalFont);
            addLabelValueLine(document, "Cliente:", safeText(cotizacion.getNombreCliente(), "-"), boldFont, normalFont);
            addLabelValueLine(document, "Telefono:", safeText(cotizacion.getTelefonoCliente(), "-"), boldFont, normalFont);
            addLabelValueLine(document, "Vendedor:", safeText(cotizacion.getUsuario().getNombres(), "-"), boldFont, normalFont);

            addLine(document, "-------------------------------------------", normalFont);

            if (cotizacion.getDetalles() != null) {
                for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
                    String nombreProducto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
                    BigDecimal precioProducto = detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO;
                    BigDecimal subtotal = detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO;

                    Paragraph pNombre = new Paragraph(limitar(nombreProducto, 32), boldFont);
                    pNombre.setSpacingAfter(2f);
                    document.add(pNombre);

                    PdfPTable itemTable = new PdfPTable(2);
                    itemTable.setWidthPercentage(100);
                    itemTable.setWidths(new float[]{60, 40});

                    String qtyPrice = detalle.getCantidad() + " x $" + formatoNumero.format(precioProducto);
                    PdfPCell leftCell = new PdfPCell(new Paragraph(qtyPrice, normalFont));
                    leftCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
                    leftCell.setBorder(PdfPCell.NO_BORDER);

                    String subtotalText = "$" + formatoNumero.format(subtotal);
                    PdfPCell rightCell = new PdfPCell(new Paragraph(subtotalText, normalFont));
                    rightCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
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

            PdfPCell totalLabel = new PdfPCell(new Paragraph("Total", boldFont));
            totalLabel.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
            totalLabel.setBorder(PdfPCell.NO_BORDER);

            String totalValue = "$" + formatoNumero.format(valorSeguro(cotizacion.getTotal()));
            PdfPCell totalVal = new PdfPCell(new Paragraph(totalValue, boldFont));
            totalVal.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            totalVal.setBorder(PdfPCell.NO_BORDER);

            totalTable.addCell(totalLabel);
            totalTable.addCell(totalVal);
            document.add(totalTable);

            addLine(document, "-------------------------------------------", normalFont);
            addCentered(document, "Cotizacion sujeta a disponibilidad", normalFont);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando cotización POS PDF", e);
        }
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

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}