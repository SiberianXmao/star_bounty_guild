package com.stud.files.service;

import com.stud.files.common.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageValidatorTest {

    private static final byte[] PNG_1X1 = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
    );

    private final ImageValidator validator = new ImageValidator();

    @Test
    void acceptsRealPngAndUsesDetectedType() {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", PNG_1X1);

        ImageValidator.ValidatedImage image = validator.validate(file);

        assertThat(image.contentType()).isEqualTo("image/png");
        assertThat(image.extension()).isEqualTo("png");
        assertThat(image.content()).isEqualTo(PNG_1X1);
    }

    @Test
    void rejectsContentThatIsNotAnImage() {
        MockMultipartFile file = new MockMultipartFile("file", "fake.png", "image/png", "not-an-image".getBytes());

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("JPEG and PNG");
    }
}
