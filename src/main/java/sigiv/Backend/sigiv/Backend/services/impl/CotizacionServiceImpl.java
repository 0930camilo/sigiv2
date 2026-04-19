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
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
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
            String fechaFin
    ) {
        // Paginación con ordenamiento por fecha descendente (más recientes primero)
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());

        // Convertir fechas de DD/MM/YYYY a LocalDateTime
        LocalDateTime dtFechaInicio = null;
        LocalDateTime dtFechaFin = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(fechaInicio, formatter);
                dtFechaInicio = date.atStartOfDay(); // 00:00:00
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha inicio inválido. Use DD/MM/YYYY");
            }
        }

        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(fechaFin, formatter);
                dtFechaFin = date.atTime(LocalTime.MAX); // 23:59:59
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha fin inválido. Use DD/MM/YYYY");
            }
        }

        // Lógica condicional: combinar filtros según lo proporcionado
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

        // Convertir Page<Cotizacion> a Page<CotizacionResponseDto>
        return cotizacionesPage.map(cotizacionMapper::toDto);
    }

    @Override
    public byte[] generarCotizacionPdf(Long id) {
        try {
            Cotizacion cotizacion = cotizacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

            // Obtener empresa desde usuario
            Empresa empresa = cotizacion.getUsuario().getEmpresa();

            // Formato Colombia
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
            // 📋 INFORMACIÓN DE COTIZACIÓN
            // ===============================

            document.add(new Paragraph("COTIZACIÓN", tituloFont));
            document.add(new Paragraph(" "));
            
            // ===============================
            // 🏢 INFORMACIÓN DE LA EMPRESA
            // ===============================

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

            // ===============================
            // 📦 TABLA PRODUCTOS
            // ===============================

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
}