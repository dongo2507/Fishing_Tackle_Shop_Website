package com.fishing.util;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Lưu và xóa file ảnh upload.
 * Ảnh được lưu NGOÀI thư mục webapp (mặc định: <thư mục người dùng>/fishingshop-uploads)
 * để không bị mất khi build lại hoặc deploy lại Tomcat.
 * Đổi thư mục bằng tham số JVM: -Dfishing.upload.dir=D:/uploads
 */
public final class UploadUtil {

    public static final long MAX_SIZE = 5L * 1024 * 1024;   // 5MB mỗi ảnh
    public static final String URL_PREFIX = "/uploads/";

    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]*");
    private static final Path DIR;

    static {
        String dir = System.getProperty("fishing.upload.dir");
        if (dir == null || dir.isBlank()) {
            dir = System.getProperty("user.home") + java.io.File.separator + "fishingshop-uploads";
        }
        DIR = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(DIR);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private UploadUtil() {}

    /**
     * Kiểm tra và lưu một ảnh.
     * @return đường dẫn lưu vào database (ví dụ /uploads/xxxx.jpg), hoặc null nếu không chọn file
     * @throws IllegalArgumentException nếu ảnh quá lớn hoặc không phải ảnh hợp lệ
     */
    public static String saveImage(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Ảnh vượt quá 5MB.");
        }

        // Xác định loại ảnh theo NỘI DUNG file (không tin tên file hay Content-Type do người dùng gửi)
        String ext;
        try (InputStream in = part.getInputStream()) {
            ext = detectExtension(in.readNBytes(12));
        }
        if (ext == null) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG, GIF hoặc WebP.");
        }

        String fileName = UUID.randomUUID() + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, DIR.resolve(fileName));
        }
        return URL_PREFIX + fileName;
    }

    /** Xóa file theo đường dẫn đã lưu trong database. Bỏ qua mọi lỗi và mọi đường dẫn lạ. */
    public static void deleteQuietly(String url) {
        if (url == null || !url.startsWith(URL_PREFIX)) {
            return;
        }
        Path p = resolve(url.substring(URL_PREFIX.length()));
        if (p != null) {
            try {
                Files.deleteIfExists(p);
            } catch (IOException ignored) {
                // không xóa được file thì thôi, không làm hỏng luồng chính
            }
        }
    }

    /** Trả về đường dẫn file thật, hoặc null nếu tên file không an toàn (chống ../ ). */
    public static Path resolve(String fileName) {
        if (fileName == null || !SAFE_NAME.matcher(fileName).matches()) {
            return null;
        }
        Path p = DIR.resolve(fileName).normalize();
        return p.startsWith(DIR) ? p : null;
    }

    private static String detectExtension(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (b.length >= 4 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') {
            return "gif";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }
}
