package com.stud.user.media.storage;

import com.stud.user.media.config.ObjectStorageProperties;
import com.stud.user.media.exception.StorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioAvatarStorage implements AvatarStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioAvatarStorage.class);
    private static final String CACHE_CONTROL = "public, max-age=31536000, immutable";

    private final MinioClient minioClient;
    private final ObjectStorageProperties properties;

    @Override
    public String store(UUID userId, byte[] content, String contentType, String extension) {
        String objectName = "avatars/%s/%s.%s".formatted(userId, UUID.randomUUID(), extension);

        try (ByteArrayInputStream input = new ByteArrayInputStream(content)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.bucket())
                            .object(objectName)
                            .stream(input, content.length, -1)
                            .contentType(contentType)
                            .headers(Map.of("Cache-Control", CACHE_CONTROL))
                            .build()
            );
        } catch (Exception exception) {
            restoreInterruptedFlag(exception);
            throw new StorageException("Avatar storage is temporarily unavailable", exception);
        }

        return normalizedPublicBaseUrl() + "/" + objectName;
    }

    @Override
    public void deleteManagedObject(String publicUrl) {
        String objectName = extractManagedObjectName(publicUrl);

        if (objectName == null) {
            return;
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.bucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception exception) {
            restoreInterruptedFlag(exception);
            log.warn("Could not remove obsolete avatar object '{}'", objectName, exception);
        }
    }

    private String extractManagedObjectName(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return null;
        }

        String prefix = normalizedPublicBaseUrl() + "/";
        return publicUrl.startsWith(prefix) ? publicUrl.substring(prefix.length()) : null;
    }

    private String normalizedPublicBaseUrl() {
        return properties.publicBaseUrl().replaceAll("/+$", "");
    }

    private void restoreInterruptedFlag(Exception exception) {
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
