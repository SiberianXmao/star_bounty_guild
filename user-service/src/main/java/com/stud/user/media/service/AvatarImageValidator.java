package com.stud.user.media.service;

import com.stud.user.common.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

@Component
public class AvatarImageValidator {

    static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    static final int MAX_DIMENSION = 4096;

    public ValidatedAvatar validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Avatar file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Avatar must not exceed 5 MB");
        }

        byte[] content = readContent(file);

        try (ImageInputStream imageInput = ImageIO.createImageInputStream(new ByteArrayInputStream(content))) {
            if (imageInput == null) {
                throw unsupportedImage();
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw unsupportedImage();
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(imageInput, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0 || width > MAX_DIMENSION || height > MAX_DIMENSION) {
                    throw new BadRequestException("Avatar dimensions must be between 1 and 4096 pixels");
                }

                return switch (format) {
                    case "jpeg", "jpg" -> new ValidatedAvatar(content, "image/jpeg", "jpg");
                    case "png" -> new ValidatedAvatar(content, "image/png", "png");
                    default -> throw unsupportedImage();
                };
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new BadRequestException("Avatar must be a valid JPEG or PNG image");
        }
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new BadRequestException("Could not read avatar file");
        }
    }

    private BadRequestException unsupportedImage() {
        return new BadRequestException("Only JPEG and PNG avatars are supported");
    }

    public record ValidatedAvatar(byte[] content, String contentType, String extension) {
    }
}
