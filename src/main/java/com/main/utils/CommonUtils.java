package com.main.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommonUtils {

    public static byte[] generateQRCodeWithAvatar(String content, int width, int height, BufferedImage avatarImage) {
        try {
            // 1. Generate QR Code (manually to support ARGB color)
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            // Tạo ảnh QR với hỗ trợ màu
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : new Color(255, 255, 255, 0).getRGB());
                }
            }

            // 2. Tính toán vị trí và kích thước ảnh avatar
            int overlaySize = Math.min(width, height) / 6;
            int overlayX = (qrImage.getWidth() - overlaySize) / 2;
            int overlayY = (qrImage.getHeight() - overlaySize) / 2;

            // 3. Tạo avatar hình tròn
            BufferedImage circularAvatar = createCircularImage(
                    resizeImageWithQuality(avatarImage, overlaySize, overlaySize)
            );

            // 4. Vẽ avatar và nền trắng
            Graphics2D g = qrImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ nền trắng hình tròn bên dưới avatar
            int borderSize = overlaySize / 10;
            g.setColor(Color.WHITE);
            g.fillOval(
                    overlayX - borderSize,
                    overlayY - borderSize,
                    overlaySize + 2 * borderSize,
                    overlaySize + 2 * borderSize
            );

            // Vẽ avatar hình tròn
            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(circularAvatar, overlayX, overlayY, null);
            g.dispose();

            // 5. Convert to PNG byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            return baos.toByteArray();

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code with avatar", e);
        }
    }


    private static BufferedImage createCircularImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        // Create circular clipping mask
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, width, height);
        g2.setClip(circle);

        // Draw the image
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return output;
    }

    private static BufferedImage resizeImageWithQuality(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

}
