package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import static org.mockito.Mockito.mock;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void testGetByIdFileFound() throws Exception {
        byte[] fileContent = new byte[] {1, 2, 3};

        FileDto mockFileDto = new FileDto("testFile.img", fileContent);

        when(fileService.getFileById(anyInt())).thenReturn(Optional.of(mockFileDto));

        var response = fileController.getById(1);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(fileContent);
    }

    @Test
    public void testGetByIdFileNotFound() {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());

        var response = fileController.getById(1);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }
}
