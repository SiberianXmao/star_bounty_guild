package com.stud.dictionary.media.client;

import com.stud.dictionary.common.exception.BadRequestException;
import com.stud.dictionary.media.client.FileServiceFeignClient.DeleteFileRequest;
import com.stud.dictionary.media.exception.StorageException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileStorageClient {

    private static final Logger log = LoggerFactory.getLogger(FileStorageClient.class);

    private final FileServiceFeignClient fileService;

    public String uploadImage(String category, UUID ownerId, MultipartFile file) {
        try {
            return fileService.uploadImage(category, ownerId, file).url();
        } catch (FeignException exception) {
            if (exception.status() == 400 || exception.status() == 413) {
                throw new BadRequestException("Image must be a valid JPEG or PNG file up to 5 MB");
            }
            throw new StorageException("File service is temporarily unavailable", exception);
        }
    }

    public void deleteManagedObject(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return;
        }

        try {
            fileService.delete(new DeleteFileRequest(publicUrl));
        } catch (FeignException exception) {
            log.warn("Could not remove obsolete media file '{}' through file-service", publicUrl, exception);
        }
    }
}
