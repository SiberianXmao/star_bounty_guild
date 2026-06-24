package com.stud.user.media.service;

import com.stud.user.media.service.AvatarAccountService.AvatarAssignment;
import com.stud.user.media.service.AvatarAccountService.AvatarOwner;
import com.stud.user.media.client.FileStorageClient;
import com.stud.user.media.web.dto.AvatarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarAccountService avatarAccountService;
    private final FileStorageClient fileStorageClient;

    public AvatarResponse upload(String email, MultipartFile file) {
        AvatarOwner owner = avatarAccountService.getOwner(email);
        String newUrl = fileStorageClient.uploadImage("avatars", owner.userId(), file);

        AvatarAssignment assignment;

        try {
            assignment = avatarAccountService.replace(owner.userId(), newUrl);
        } catch (RuntimeException exception) {
            fileStorageClient.deleteManagedObject(newUrl);
            throw exception;
        }

        fileStorageClient.deleteManagedObject(assignment.previousUrl());
        return new AvatarResponse(assignment.avatarUrl());
    }

    public void delete(String email) {
        AvatarOwner owner = avatarAccountService.getOwner(email);
        AvatarAssignment assignment = avatarAccountService.clear(owner.userId());
        fileStorageClient.deleteManagedObject(assignment.previousUrl());
    }
}
