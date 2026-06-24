package com.stud.files.domain;

import com.stud.files.common.BadRequestException;

import java.util.Arrays;

public enum FileCategory {
    AVATAR("avatars"),
    PLANET("planets"),
    SECTOR("sectors"),
    FACTION("factions");

    private final String path;

    FileCategory(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public static FileCategory fromPath(String path) {
        return Arrays.stream(values())
                .filter(category -> category.path.equalsIgnoreCase(path))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unsupported image category: " + path));
    }
}
