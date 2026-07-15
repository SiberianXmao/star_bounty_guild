package com.stud.dictionary.integrations.files;

import com.stud.dictionary.common.exception.BadRequestException;
import com.stud.dictionary.integrations.files.FileServiceFeignClient.DeleteFileRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageClient {

    private final FileServiceFeignClient fileService;

    public String uploadImage(String category, UUID ownerId, MultipartFile file) {
        log.debug("Uploading image through file-service category={} ownerId={} originalFilename='{}' size={}",
                category,
                ownerId,
                file.getOriginalFilename(),
                file.getSize()
        );

        try {
            String url = fileService.uploadImage(category, ownerId, file).url();
            log.info("Uploaded image through file-service category={} ownerId={} url='{}'", category, ownerId, url);
            return url;
        } catch (FeignException exception) {
            if (exception.status() == 400 || exception.status() == 413) {
                log.warn(
                        "File-service rejected image upload category={} ownerId={} status={} originalFilename='{}'",
                        category,
                        ownerId,
                        exception.status(),
                        file.getOriginalFilename()
                );
                throw new BadRequestException("Image must be a valid JPEG or PNG file up to 5 MB");
            }

            log.error("File-service image upload failed category={} ownerId={} status={}", category, ownerId, exception.status(), exception);
            throw new FileStorageException("File service is temporarily unavailable", exception);
        }
    }

    public void deleteManagedObject(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            log.debug("Skipping file-service delete because publicUrl is empty");
            return;
        }

        try {
            fileService.delete(new DeleteFileRequest(publicUrl));
            log.debug("Deleted managed file through file-service publicUrl='{}'", publicUrl);
        } catch (FeignException exception) {
            log.warn("Could not remove obsolete catalog image '{}' through file-service", publicUrl, exception);
        }
    }
}
