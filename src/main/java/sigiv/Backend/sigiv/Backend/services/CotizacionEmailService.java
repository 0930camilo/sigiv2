package sigiv.Backend.sigiv.Backend.services;

public interface CotizacionEmailService {

    void enviarCotizacionPorCorreo(Long cotizacionId, String correoDestino);
}
