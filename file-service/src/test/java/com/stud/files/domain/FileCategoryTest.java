package com.stud.files.domain;

import com.stud.files.common.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCategoryTest {

    @Test
    void mapsOnlySupportedPublicPaths() {
        assertThat(FileCategory.fromPath("avatars")).isEqualTo(FileCategory.AVATAR);
        assertThat(FileCategory.fromPath("planets")).isEqualTo(FileCategory.PLANET);
        assertThatThrownBy(() -> FileCategory.fromPath("../../private"))
                .isInstanceOf(BadRequestException.class);
    }
}
