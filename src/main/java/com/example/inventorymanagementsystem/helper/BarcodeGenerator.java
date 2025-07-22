package com.example.inventorymanagementsystem.helper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class BarcodeGenerator {
    private BarcodeGenerator(){
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static byte[] generateBarcodeImage(String text, int width, int height) throws IOException {
        Code128Writer writer = new Code128Writer();

        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);

        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(barcodeImage, "png", outputStream);
       return outputStream.toByteArray();
    }
}
