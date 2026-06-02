package sigiv.Backend.sigiv.Backend.dto.produc;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoImportResultDto {

    private int totalFilas;
    private int exitosos;
    private int fallidos;
    private List<FilaError> errores;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilaError {
        private int fila;
        private String motivo;
    }
}

