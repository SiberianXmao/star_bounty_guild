package com.stud.files.service;

import com.stud.files.domain.FileCategory;
import com.stud.files.service.ImageValidator.ValidatedImage;
import com.stud.files.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ImageValidator imageValidator;
    private final FileStorage fileStorage;

    @Override
    public String uploadImage(FileCategory category, UUID ownerId, MultipartFile file) {
        ValidatedImage image = imageValidator.validate(file);
        return fileStorage.store(category, ownerId, image.content(), image.contentType(), image.extension());
    }

    @Override
    public void delete(String publicUrl) {
        fileStorage.deleteManagedObject(publicUrl);
    }
}
