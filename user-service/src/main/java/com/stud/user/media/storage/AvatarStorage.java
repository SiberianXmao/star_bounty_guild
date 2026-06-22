package com.stud.user.media.storage;

import java.util.UUID;

public interface AvatarStorage {

    String store(UUID userId, byte[] content, String contentType, String extension);

    void deleteManagedObject(String publicUrl);
}
