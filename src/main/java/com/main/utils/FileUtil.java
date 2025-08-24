package com.main.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tiện ích xử lý file ảnh trong thư mục uploads (nằm ngang với src).
 * <p>
 * Chức năng:
 * - Lưu file ảnh được upload.
 * - Xoá file ảnh theo tên.
 */
public final class FileUtil {

    /**
     * Thư mục uploads (nằm ngang với thư mục src).
     */
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    /**
     * Lưu file ảnh vào thư mục uploads.
     *
     * @param file MultipartFile được upload từ client.
     * @return Tên file đã lưu (có kèm timestamp).
     * @throws IOException Nếu có lỗi khi ghi file.
     * @throws IllegalArgumentException Nếu file rỗng.
     */
    public static String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Tạo tên file duy nhất
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Đường dẫn lưu file
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Ghi file
        Files.write(filePath, file.getBytes());

        return fileName;
    }


    public static String saveImage2(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = file.getOriginalFilename();

        // Đường dẫn lưu file
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Ghi file
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    /**
     * Xoá file trong thư mục uploads theo tên file.
     *
     * @param imageName Tên file muốn xoá.
     * @return true nếu xoá thành công, false nếu file không tồn tại hoặc không xoá được.
     */
    public static boolean deleteFile(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return false;
        }

        Path filePath = Paths.get(UPLOAD_DIR, imageName);
        File file = filePath.toFile();

        if (file.exists()) {
            return file.delete();
        }

        return false;
    }
}

