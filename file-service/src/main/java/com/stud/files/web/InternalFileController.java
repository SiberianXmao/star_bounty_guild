package com.stud.files.web;

import com.stud.files.domain.FileCategory;
import com.stud.files.service.FileService;
import com.stud.files.web.FileDtos.DeleteFileRequest;
import com.stud.files.web.FileDtos.StoredFileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/files")
public class InternalFileController {

    private final FileService fileService;

    @PostMapping(value = "/images/{category}/{ownerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoredFileResponse uploadImage(
            @PathVariable String category,
            @PathVariable UUID ownerId,
            @RequestPart("file") MultipartFile file
    ) {
        String url = fileService.uploadImage(FileCategory.fromPath(category), ownerId, file);
        return new StoredFileResponse(url);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @RequestBody DeleteFileRequest request) {
        fileService.delete(request.url());
    }
}
