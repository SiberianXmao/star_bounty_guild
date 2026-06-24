package com.stud.files.storage;

import com.stud.files.common.BadRequestException;
import com.stud.files.common.StorageException;
import com.stud.files.config.ObjectStorageProperties;
import com.stud.files.domain.FileCategory;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioFileStorage implements FileStorage {

    private static final String CACHE_CONTROL = "public, max-age=31536000, immutable";

    private final MinioClient minioClient;
    private final ObjectStorageProperties properties;

    @Override
    public String store(FileCategory category, UUID ownerId, byte[] content, String contentType, String extension) {
        String objectName = "%s/%s/%s.%s".formatted(category.path(), ownerId, UUID.randomUUID(), extension);

        try (ByteArrayInputStream input = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .stream(input, content.length, -1)
                    .contentType(contentType)
                    .headers(Map.of("Cache-Control", CACHE_CONTROL))
                    .build());
        } catch (Exception exception) {
            restoreInterruptedFlag(exception);
            throw new StorageException("File storage is temporarily unavailable", exception);
        }

        return normalizedPublicBaseUrl() + "/" + objectName;
    }

    @Override
    public void deleteManagedObject(String publicUrl) {
        String objectName = extractManagedObjectName(publicUrl);

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .build());
        } catch (Exception exception) {
            restoreInterruptedFlag(exception);
            throw new StorageException("File storage is temporarily unavailable", exception);
        }
    }

    private String extractManagedObjectName(String publicUrl) {
        String prefix = normalizedPublicBaseUrl() + "/";
        if (publicUrl == null || !publicUrl.startsWith(prefix) || publicUrl.length() == prefix.length()) {
            throw new BadRequestException("Only managed media files can be deleted");
        }
        return publicUrl.substring(prefix.length());
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
