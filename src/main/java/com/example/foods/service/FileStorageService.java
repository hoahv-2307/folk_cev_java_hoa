package com.example.foods.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  String uploadFile(MultipartFile file);

  String uploadFileToTemp(MultipartFile file);

  void moveFile(String sourceKey, String destinationKey);

  byte[] downloadFile(String filename);

  void deleteFile(String filename);

  boolean fileExists(String filename);
}
