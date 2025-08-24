package com.main.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${azure.storage.connection-string}")
    private String injectedConnectionString;

    private static String CONNECTION_STRING;
    private static final String CONTAINER_NAME = "images";

    @PostConstruct
    public void init() {
        CONNECTION_STRING = injectedConnectionString; // gán vào static sau khi Spring inject
    }


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

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(CONNECTION_STRING)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        // Upload file
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Gán Content-Type (để browser hiểu đây là ảnh)
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        // Trả về tên file
        return fileName;
    }



    public static String saveImage2(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Tạo tên file duy nhất
        String fileName = file.getOriginalFilename();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(CONNECTION_STRING)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        // Upload file
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Gán Content-Type (để browser hiểu đây là ảnh)
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        // Trả về tên file
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

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(CONNECTION_STRING)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

            BlobClient blobClient = containerClient.getBlobClient(imageName);

            if (blobClient.exists()) {
                blobClient.delete();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}

