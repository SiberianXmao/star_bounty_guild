package com.stud.user.media.service;

import com.stud.user.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvatarImageValidatorTest {

    private static final byte[] PNG_1X1 = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
    );

    private final AvatarImageValidator validator = new AvatarImageValidator();

    @Test
    void acceptsValidPng() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                PNG_1X1
        );

        AvatarImageValidator.ValidatedAvatar avatar = validator.validate(file);

        assertThat(avatar.contentType()).isEqualTo("image/png");
        assertThat(avatar.extension()).isEqualTo("png");
        assertThat(avatar.content()).isEqualTo(PNG_1X1);
    }

    @Test
    void rejectsContentThatIsNotAnImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "not-an-image".getBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("JPEG and PNG");
    }
}
