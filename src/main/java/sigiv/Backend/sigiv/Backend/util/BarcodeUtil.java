package sigiv.Backend.sigiv.Backend.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;

public final class BarcodeUtil {

    private BarcodeUtil() {
    }

    public static String generarCode128Base64(String data, int width, int height) {
        byte[] png = generarCode128Png(data, width, height);
        return Base64.getEncoder().encodeToString(png);
    }

    public static byte[] generarCode128Png(String data, int width, int height) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("El código de barras no puede estar vacío");
        }

        Code128Writer writer = new Code128Writer();
        try {
            BitMatrix matrix = writer.encode(data, BarcodeFormat.CODE_128, width, height);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar la imagen del código de barras", e);
        }
    }
}


