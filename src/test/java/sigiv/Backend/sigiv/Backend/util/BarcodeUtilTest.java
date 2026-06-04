package sigiv.Backend.sigiv.Backend.util;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class BarcodeUtilTest {

    @Test
    void generaBase64Code128() {
        String base64 = BarcodeUtil.generarCode128Base64("ABC123XYZ", 300, 100);
        assertFalse(base64.isBlank());
    }
}

