package com.stud.files.web;

import jakarta.validation.constraints.NotBlank;

public final class FileDtos {

    private FileDtos() {
    }

    public record StoredFileResponse(String url) {
    }

    public record DeleteFileRequest(@NotBlank String url) {
    }
}
