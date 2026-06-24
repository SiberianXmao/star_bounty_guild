package com.stud.files.service;

import com.stud.files.common.BadRequestException;
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
public class ImageValidator {

    static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    static final int MAX_DIMENSION = 4096;

    public ValidatedImage validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Image must not exceed 5 MB");
        }

        byte[] content = readContent(file);
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(content))) {
            if (input == null) {
                throw unsupportedImage();
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw unsupportedImage();
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || width > MAX_DIMENSION || height > MAX_DIMENSION) {
                    throw new BadRequestException("Image dimensions must be between 1 and 4096 pixels");
                }

                return switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg", "jpg" -> new ValidatedImage(content, "image/jpeg", "jpg");
                    case "png" -> new ValidatedImage(content, "image/png", "png");
                    default -> throw unsupportedImage();
                };
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new BadRequestException("Image must be a valid JPEG or PNG file");
        }
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new BadRequestException("Could not read image file");
        }
    }

    private BadRequestException unsupportedImage() {
        return new BadRequestException("Only JPEG and PNG images are supported");
    }

    public record ValidatedImage(byte[] content, String contentType, String extension) {
    }
}
