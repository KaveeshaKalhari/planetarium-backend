package com.example.planetarium.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    /**
     * Generates a QR code for the given booking reference.
     * Returns the image as a Base64-encoded PNG string (for embedding in email).
     */
    public String generateQRCodeBase64(String bookingReference) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Encode the booking reference as the QR content
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(bookingReference, BarcodeFormat.QR_CODE, 300, 300, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
