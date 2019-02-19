package de.propra2.ausleiherino24.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ImageStoreServiceTest {

	private ImageStoreService imageStoreService;
	private String path = "imagestoretest";

	@Before
	public void init() {
		imageStoreService = new ImageStoreService(path);
	}

	@After
	public void clean() {
		cleanDir(new File(path));
	}

	@Test
	public void uploadDirectoryDontExists() {
		imageStoreService.createUploadDirectoryIfNotExists();
		assertTrue(new File("/").exists());
	}

	@Test
	public void extensionOfTxt() {
		assertEquals("txt", imageStoreService.getFileExtension("test.txt"));
	}

	@Test
	public void extensionOfPng() {
		assertEquals("png", imageStoreService.getFileExtension("test.png"));
	}

	@Test
	public void extensionOfEmptySting() {
		assertEquals("", imageStoreService.getFileExtension(null));
	}

	@Test
	public void createDirectory() {
		imageStoreService.createBinningDirectory("x");
		assertTrue(new File(path + "/x").exists());
		assertEquals(1, new File(path).listFiles().length);
	}

	@Test
	public void createDirectoryTwice() {
		imageStoreService.createBinningDirectory("x");
		imageStoreService.createBinningDirectory("x");
		assertTrue(new File(path + "/x").exists());
		assertEquals(1, new File(path).listFiles().length);
	}

	@Test
	public void createTwoDirectories() {
		imageStoreService.createBinningDirectory("x");
		imageStoreService.createBinningDirectory("y");
		assertTrue(new File(path + "/x").exists());
		assertTrue(new File(path + "/y").exists());
		assertEquals(2, new File(path).listFiles().length);
	}

	@Test
	public void createDirectoryTree() {
		imageStoreService.createBinningDirectory("x");
		imageStoreService.createBinningDirectory("x/y");
		assertTrue(new File(path + "/x").exists());
		assertTrue(new File(path + "/x/y").exists());
		assertEquals(1, new File(path).listFiles().length);
		assertEquals(1, new File(path + "/x").listFiles().length);
	}

	@Test
	public void testBinning() {
		imageStoreService.ensureBinning(0L);
		assertTrue(new File(path + "/0").exists());
		assertEquals(1, new File(path).listFiles().length);
	}

	@Test
	public void testBinning2() {
		imageStoreService.ensureBinning(100L);
		assertTrue(new File(path + "/0").exists());
		assertEquals(1, new File(path).listFiles().length);
	}

	@Test
	public void testBinning3() {
		imageStoreService.ensureBinning(0L);
		imageStoreService.ensureBinning(100L);
		assertTrue(new File(path + "/0").exists());
		assertEquals(1, new File(path).listFiles().length);
	}

	@Test
	public void fileExists() throws IOException {
		File file = new File(path + "/x.txt");
		file.createNewFile();

		assertTrue(imageStoreService.fileExists(path + "/x.txt"));
	}

	@Test
	public void fileDoesNotExists() {
		assertFalse(imageStoreService.fileExists(path + "/x.txt"));
	}

	@Test
	public void onlyDirectoryExists() {
		File file = new File(path + "/x");
		file.mkdir();

		assertFalse(imageStoreService.fileExists(path + "/x"));
	}


	//Deletes the given directory and all included directories and files
	public void cleanDir(File dir) {
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
