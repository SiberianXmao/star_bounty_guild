package com.stud.user.media.service;

import com.stud.user.media.service.AvatarAccountService.AvatarAssignment;
import com.stud.user.media.service.AvatarAccountService.AvatarOwner;
import com.stud.user.media.service.AvatarImageValidator.ValidatedAvatar;
import com.stud.user.media.storage.AvatarStorage;
import com.stud.user.media.web.dto.AvatarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarAccountService avatarAccountService;
    private final AvatarImageValidator avatarImageValidator;
    private final AvatarStorage avatarStorage;

    public AvatarResponse upload(String email, MultipartFile file) {
        AvatarOwner owner = avatarAccountService.getOwner(email);
        ValidatedAvatar avatar = avatarImageValidator.validate(file);
        String newUrl = avatarStorage.store(
                owner.userId(),
                avatar.content(),
                avatar.contentType(),
                avatar.extension()
        );

        AvatarAssignment assignment;

        try {
            assignment = avatarAccountService.replace(owner.userId(), newUrl);
        } catch (RuntimeException exception) {
            avatarStorage.deleteManagedObject(newUrl);
            throw exception;
        }

        avatarStorage.deleteManagedObject(assignment.previousUrl());
        return new AvatarResponse(assignment.avatarUrl());
    }

    public void delete(String email) {
        AvatarOwner owner = avatarAccountService.getOwner(email);
        AvatarAssignment assignment = avatarAccountService.clear(owner.userId());
        avatarStorage.deleteManagedObject(assignment.previousUrl());
    }
}
