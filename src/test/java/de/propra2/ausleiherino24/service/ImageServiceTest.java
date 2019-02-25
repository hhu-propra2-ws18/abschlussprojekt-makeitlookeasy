package de.propra2.ausleiherino24.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageServiceTest {

    private ImageService imageService;
    private String path = "imagestoretest";

    @Before
    public void init() {
        imageService = new ImageService(path);
    }

    @After
    public void clean() {
        cleanDir(new File(path));
    }

    @Test
    public void uploadDirectoryDontExists() {
        imageService.createUploadDirectoryIfNotExists();
        assertTrue(new File("/").exists());
    }

    @Test
    public void extensionOfTxt() {
        assertEquals("txt", imageService.getFileExtension("test.txt"));
    }

    @Test
    public void extensionOfPng() {
        assertEquals("png", imageService.getFileExtension("test.png"));
    }

    @Test
    public void extensionOfEmptySting() {
        assertEquals("", imageService.getFileExtension(null));
    }

    @Test
    public void createDirectory() {
        imageService.createBinningDirectory("x");
        assertTrue(new File(path + "/x").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    public void createDirectoryTwice() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("x");
        assertTrue(new File(path + "/x").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    public void createTwoDirectories() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("y");
        assertTrue(new File(path + "/x").exists());
        assertTrue(new File(path + "/y").exists());
        assertEquals(2, new File(path).listFiles().length);
    }

    @Test
    public void createDirectoryTree() {
        imageService.createBinningDirectory("x");
        imageService.createBinningDirectory("x/y");
        assertTrue(new File(path + "/x").exists());
        assertTrue(new File(path + "/x/y").exists());
        assertEquals(1, new File(path).listFiles().length);
        assertEquals(1, new File(path + "/x").listFiles().length);
    }

    @Test
    public void testBinning() {
        imageService.ensureBinning(0L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    public void testBinning2() {
        imageService.ensureBinning(100L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    public void testBinning3() {
        imageService.ensureBinning(0L);
        imageService.ensureBinning(100L);
        assertTrue(new File(path + "/0").exists());
        assertEquals(1, new File(path).listFiles().length);
    }

    @Test
    public void fileExists() throws IOException {
        File file = new File(path + "/x.txt");
        file.createNewFile();

        assertTrue(imageService.fileExists(path + "/x.txt"));
    }

    @Test
    public void fileDoesNotExists() {
        assertFalse(imageService.fileExists(path + "/x.txt"));
    }

    @Test
    public void onlyDirectoryExists() {
        File file = new File(path + "/x");
        file.mkdir();

        assertFalse(imageService.fileExists(path + "/x"));
    }


    //Deletes the given directory and all included directories and files
    private void cleanDir(File dir) {
        if (!dir.isDirectory()) {
            dir.delete();
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.delete()) {
                cleanDir(file);
            }
        }
        dir.delete();
    }
}
