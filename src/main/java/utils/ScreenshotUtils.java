package utils;

import constants.AppConstants;
import driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Chup screenshot, luu vao thu muc /screenshots, tra ve duong dan file.
 * Ngoai ra co captureBase64Compressed() de NHUNG anh (da scale + nen JPEG) vao ExtentReport
 * -> HTML tu chua, khong phu thuoc thu muc screenshots, dung luong nhe.
 */
public class ScreenshotUtils {

    // Anh nhung vao report: scale ve chieu rong nay + nen JPEG chat luong nay (nhe HTML).
    private static final int EMBED_WIDTH = 860;
    private static final float EMBED_JPEG_QUALITY = 0.5f;

    private ScreenshotUtils() {
    }

    /**
     * Chup man hinh hien tai.
     *
     * @param testName ten test (dung dat ten file)
     * @return duong dan tuyet doi cua file anh, hoac null neu loi
     */
    public static String capture(String testName) {
        try {
            TakesScreenshot ts = DriverManager.getDriver();
            File src = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName  = testName + "_" + timestamp + ".png";
            String destPath  = AppConstants.SCREENSHOT_DIR + fileName;

            File dest = new File(destPath);
            FileUtils.copyFile(src, dest);
            LogUtils.info("Da chup screenshot: {}", destPath);
            return destPath;
        } catch (Exception e) {
            LogUtils.error("Khong chup duoc screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Chup man hinh hien tai -> scale ve EMBED_WIDTH + nen JPEG -> tra ve chuoi Base64 (khong prefix).
     * Dung de NHUNG thang vao ExtentReport (createScreenCaptureFromBase64String) lam anh minh chung PASS.
     * HTML tu chua (khong can thu muc screenshots), nhe (~40-80KB/anh thay vi PNG full ~300-800KB).
     *
     * @return chuoi base64 JPEG, hoac null neu loi
     */
    /**
     * Do sang TRUNG BINH (0..255) cua 1 vung vuong quanh (cx,cy) tren man hien tai.
     * Dung de xac dinh theme LIGHT (nen sang, > ~140) hay DARK (nen toi, < ~90) khi UI khong
     * expose theme qua a11y. Tra ve -1 neu loi.
     */
    public static int regionBrightness(int cx, int cy, int half) {
        try {
            byte[] png = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            java.awt.image.BufferedImage img =
                    javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(png));
            if (img == null) return -1;
            long sum = 0; int n = 0;
            int x0 = Math.max(0, cx - half), x1 = Math.min(img.getWidth(),  cx + half);
            int y0 = Math.max(0, cy - half), y1 = Math.min(img.getHeight(), cy + half);
            for (int y = y0; y < y1; y += 4) {
                for (int x = x0; x < x1; x += 4) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                    sum += (r + g + b) / 3; n++;
                }
            }
            int avg = n > 0 ? (int) (sum / n) : -1;
            LogUtils.info("Do sang vung ({},{}) = {}", cx, cy, avg);
            return avg;
        } catch (Exception e) {
            LogUtils.error("Khong doc duoc do sang: " + e.getMessage());
            return -1;
        }
    }

    public static String captureBase64Compressed() {
        try {
            TakesScreenshot ts = DriverManager.getDriver();
            byte[] png = ts.getScreenshotAs(OutputType.BYTES);

            BufferedImage src = ImageIO.read(new ByteArrayInputStream(png));
            if (src == null) {
                return Base64.getEncoder().encodeToString(png); // fallback: PNG goc
            }
            int w = EMBED_WIDTH;
            int h = (int) Math.round(src.getHeight() * (w / (double) src.getWidth()));

            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, 0, 0, w, h, Color.WHITE, null); // nen trang (JPEG khong co alpha)
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(EMBED_JPEG_QUALITY);
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(scaled, null, null), param);
            } finally {
                writer.dispose();
            }
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            LogUtils.error("Khong tao duoc base64 screenshot: " + e.getMessage());
            return null;
        }
    }
}
