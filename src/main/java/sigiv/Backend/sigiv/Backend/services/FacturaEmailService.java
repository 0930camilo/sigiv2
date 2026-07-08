package sigiv.Backend.sigiv.Backend.services;

public interface FacturaEmailService {

    void enviarFacturaPorCorreo(Long ventaId, String correoDestino);

    void enviarFacturaPorCorreo(Long ventaId, String correoDestino, String formatoFactura);
}
