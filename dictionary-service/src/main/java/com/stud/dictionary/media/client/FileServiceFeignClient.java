package com.stud.dictionary.media.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(
        name = "file-service",
        url = "${app.services.file.base-url}",
        path = "/internal/v1/files"
)
public interface FileServiceFeignClient {

    @PostMapping(value = "/images/{category}/{ownerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    StoredFileResponse uploadImage(
            @PathVariable String category,
            @PathVariable UUID ownerId,
            @RequestPart("file") MultipartFile file
    );

    @DeleteMapping
    void delete(@RequestBody DeleteFileRequest request);

    record StoredFileResponse(String url) {
    }

    record DeleteFileRequest(String url) {
    }
}
