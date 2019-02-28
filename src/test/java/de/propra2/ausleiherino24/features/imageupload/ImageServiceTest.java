package de.propra2.ausleiherino24.features.imageupload;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
class ImageServiceTest {

    private ImageService imageService;
    private String path = "imageStoreTest";

    @BeforeEach
    void init() {
        imageService = spy(new ImageService(path));
    }

    @AfterEach
    void clean() {
        cleanDir(new File(path));
    }

    @Test
    void uploadDirectoryDontExists() {
        imageService.createUploadDirectoryIfNotExists();
        assertTrue(new File("/").exists());
    }

    @Test
    void extensionOfTxt() {
        assertEquals("txt", imageService.getFileExtension("test.txt"));
    }

    @Test
    void extensionOfPng() {
        assertEquals("png", imageService.getFileExtension("test.png"));
    }

    @Test
    void extensionOfEmptySting() {
        assertEquals("", imageService.getFileExtension(null));
    }

    @Test
    void createDirectory() {
        imageService.createBinningDirectory("x");
        assertTrue(new File(path + "/x").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    void createDirectoryTwice() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("x");
        assertTrue(new File(path + "/x").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    void createTwoDirectories() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("y");
        assertTrue(new File(path + "/x").exists());
        assertTrue(new File(path + "/y").exists());
        assertEquals(2, new File(path).listFiles().length);
    }

    @Test
    void createDirectoryTree() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("x/y");
        assertTrue(new File(path + "/x").exists());
        assertTrue(new File(path + "/x/y").exists());
        assertEquals(1, new File(path).listFiles().length);
        assertEquals(1, new File(path + "/x").listFiles().length);
    }

    @Test
    void testBinning() {
        imageService.ensureBinning(0L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    void testBinning2() {
        imageService.ensureBinning(100L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    void testBinning3() {
        imageService.ensureBinning(0L);
        imageService.ensureBinning(100L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    void fileExists() throws IOException {
        final File file = new File(path + "/x.txt");
        file.createNewFile();

        assertTrue(imageService.fileExists(path + "/x.txt"));
    }

    @Test
    void fileDoesNotExists() {
        assertFalse(imageService.fileExists(path + "/x.txt"));
    }

    @Test
    void onlyDirectoryExists() {
        final File file = new File(path + "/x");
        file.mkdir();

        assertFalse(imageService.fileExists(path + "/x"));
    }

    @Test
    void buildPath() {
        when(imageService.getUploadDirectoryPath()).thenReturn("/");

        assertEquals("/test/abc.txt", imageService.buildPath("abc.txt", "test"));
    }

    @Test
    void findFile() throws IOException {
        new File(path + "/0").mkdir();
        final File file = new File(path + "/0/test.txt");
        file.createNewFile();
        when(imageService.getUploadDirectoryPath()).thenReturn(path);

        assertEquals(file, imageService.getFile("test.txt", 100L));
    }

    @Test
    void findFileWithoutBinningId() throws IOException {
        final File file = new File(path + "/test.txt");
        file.createNewFile();
        when(imageService.getUploadDirectoryPath()).thenReturn(path);

        assertEquals(file, imageService.getFile("test.txt", null));
    }

    @Test
    void findNotExistingFile() {
        assertNull(imageService.getFile("test.txt", null));
    }

    @Test
    void storeMultipartFile() throws IOException {
        final MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(imageService.generateFilePath("0", "txt")).thenReturn(path + "/0/test.txt");

        assertEquals("test.txt", imageService.store(file, 100L));
        verify(file).transferTo(new File(path + "/0/test.txt"));
    }


    //Deletes the given directory and all included directories and files
    private void cleanDir(final File dir) {
        if (!dir.isDirectory()) {
            dir.delete();
            return;
        }

        final File[] files = dir.listFiles();
        for (final File file : files) {
            if (!file.delete()) {
                cleanDir(file);
            }
        }
        dir.delete();
    }
}
