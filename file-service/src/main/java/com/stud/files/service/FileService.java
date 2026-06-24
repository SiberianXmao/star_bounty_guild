package com.stud.files.service;

import com.stud.files.domain.FileCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {

    String uploadImage(FileCategory category, UUID ownerId, MultipartFile file);

    void delete(String publicUrl);
}
