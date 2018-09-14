package com.mercadolibre.puzzle.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QRCode {

  public static void createQRCode(String qrCodeData, String filePath,
      String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
      throws WriterException, IOException {
    BitMatrix matrix = new MultiFormatWriter().encode(
        new String(qrCodeData.getBytes(charset), charset),
        BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
    MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
        .lastIndexOf('.') + 1), new File(filePath));
  }

  public static String readQRCode(BufferedImage image)
      throws FileNotFoundException, IOException, NotFoundException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(image, "PNG", out);

    String charset = "UTF-8";

    Map hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
        new BufferedImageLuminanceSource(
            ImageIO.read(new ByteArrayInputStream(out.toByteArray())))));
    Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
    return qrCodeResult.getText();
  }
}