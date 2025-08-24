package com.main.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tiện ích xử lý file ảnh trong thư mục uploads (nằm ngang với src).
 * <p>
 * Chức năng:
 * - Lưu file ảnh được upload.
 * - Xoá file ảnh theo tên.
 */
@Service
public class FileUtil {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    private static final String CONTAINER_NAME = "images";

    /**
     * Lưu file ảnh vào Azure Blob Storage.
     */
    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Tạo tên file duy nhất
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        // Upload file
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Gán Content-Type
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        return fileName;
    }

    /**
     * Lưu file với tên gốc.
     */
    public String saveImage2(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = file.getOriginalFilename();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        return fileName;
    }

    /**
     * Xoá file trong Azure Blob Storage.
     */
    public boolean deleteFile(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return false;
        }

        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
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

