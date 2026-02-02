package com.example.foods.service.impl;

import com.example.foods.service.FileStorageService;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

  private final S3Client s3Client;
  private final String bucketName;

  public FileStorageServiceImpl(
      S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
  public String uploadFile(MultipartFile file) {
    String uniqueFileName =
        UUID.randomUUID()
            + "_"
            + (file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
    try {
      s3Client.putObject(
          builder -> builder.bucket(bucketName).key(uniqueFileName).build(),
          software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
      log.info("File uploaded successfully: {}", uniqueFileName);
      return uniqueFileName;
    } catch (IOException e) {
      log.error("Failed to upload file: {}", uniqueFileName, e);
      throw new RuntimeException("Failed to upload file", e);
    }
  }

  @Override
  public byte[] downloadFile(String filename) {
    try {
      byte[] fileBytes =
          s3Client
              .getObjectAsBytes(builder -> builder.bucket(bucketName).key(filename).build())
              .asByteArray();
      log.info("File downloaded successfully: {}", filename);
      return fileBytes;
    } catch (Exception e) {
      log.error("Failed to download file: {}", filename, e);
      throw new RuntimeException("Failed to download file", e);
    }
  }

  @Override
  public String uploadFileToTemp(MultipartFile file) {
    String uniqueFileName =
        "temp/"
            + UUID.randomUUID()
            + "_"
            + (file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
    try {
      s3Client.putObject(
          builder -> builder.bucket(bucketName).key(uniqueFileName).build(),
          software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
      log.info("Temp file uploaded successfully: {}", uniqueFileName);
      return uniqueFileName;
    } catch (IOException e) {
      log.error("Failed to upload temp file: {}", uniqueFileName, e);
      throw new RuntimeException("Failed to upload file", e);
    }
  }

  @Override
  public void deleteFile(String filename) {
    try {
      s3Client.deleteObject(builder -> builder.bucket(bucketName).key(filename).build());
      log.info("File deleted successfully: {}", filename);
    } catch (Exception e) {
      log.error("Failed to delete file: {}", filename, e);
      throw new RuntimeException("Failed to delete file", e);
    }
  }

  @Override
  public boolean fileExists(String filename) {
    try {
      s3Client.headObject(builder -> builder.bucket(bucketName).key(filename).build());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void moveFile(String sourceKey, String destinationKey) {
    try {
      s3Client.copyObject(
          builder ->
              builder
                  .sourceBucket(bucketName)
                  .sourceKey(sourceKey)
                  .destinationBucket(bucketName)
                  .destinationKey(destinationKey)
                  .build());
      s3Client.deleteObject(builder -> builder.bucket(bucketName).key(sourceKey).build());
      log.info("Moved file from {} to {}", sourceKey, destinationKey);
    } catch (Exception e) {
      log.error("Failed to move file from {} to {}", sourceKey, destinationKey, e);
      throw new RuntimeException("Failed to move file", e);
    }
  }
}
