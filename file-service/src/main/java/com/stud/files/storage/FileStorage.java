package com.stud.files.storage;

import com.stud.files.domain.FileCategory;

import java.util.UUID;

public interface FileStorage {

    String store(FileCategory category, UUID ownerId, byte[] content, String contentType, String extension);

    void deleteManagedObject(String publicUrl);
}
