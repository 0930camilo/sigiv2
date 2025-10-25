package sigiv.Backend.sigiv.Backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
     @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(false, HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(), null)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(false, HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(), null)
        );
    }
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Object>> handleGeneralError(Exception ex) {
    ex.printStackTrace(); // 🔍 muestra la traza completa en consola

    String causa = (ex.getCause() != null) ? ex.getCause().toString() : "Sin causa interna";

    // 🔹 Incluimos mensaje y causa reales en la respuesta
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiResponse<>(
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error interno del servidor: " + ex.getMessage() + " | Causa: " + causa,
                    null
            )
    );
}

}
